package com.chinalooke.android.cheju.activity.business;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.config.MyLeanCloudApp;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class QRCodeActivity extends AppCompatActivity implements QRCodeView.Delegate {

    private AVObject mAvObject;
    private QRCodeView mQRCodeView;
    private Toast mToast;
    private ProgressDialog mProgressDialog;
    private AVUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode2);
        ButterKnife.bind(this);
        mCurrentUser = AVUser.getCurrentUser();
        mToast = MyLeanCloudApp.getToast();
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        mQRCodeView.startSpot();
        initData();
    }

    private void initData() {
        mAvObject = MyLeanCloudApp.getAVObject();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        mQRCodeView.stopSpot();
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            if (!TextUtils.isEmpty(result)) {
                Intent intent = new Intent(QRCodeActivity.this, SweepQcodeActivity.class);
                intent.putExtra("result", result);
                startActivity(intent);
            }
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }
    }


    @Override
    public void onScanQRCodeOpenCameraError() {
        mToast.setText("摄像头打开失败");
        mToast.show();
    }

    @OnClick({R.id.iv_wirte_back, R.id.tv_shoutian})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wirte_back:
                finish();
                break;
            case R.id.tv_shoutian:
                Intent intent = new Intent(QRCodeActivity.this, WriteQcodeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("shop", mAvObject);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
