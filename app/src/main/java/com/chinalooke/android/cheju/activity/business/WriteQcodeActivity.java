package com.chinalooke.android.cheju.activity.business;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.config.MyLeanCloudApp;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.jungly.gridpasswordview.GridPasswordView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WriteQcodeActivity extends AppCompatActivity {

    @Bind(R.id.pswView)
    GridPasswordView mPswView;
    @Bind(R.id.btn_ok)
    Button mBtnOk;
    private Toast mToast;
    private String mPassWord;
    private AVObject mShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_qcode);
        ButterKnife.bind(this);
        mToast = MyLeanCloudApp.getToast();
        initData();
        initView();
        mPswView.requestFocus();
    }

    private void initData() {
        mShop = getIntent().getParcelableExtra("shop");
    }

    private void initView() {
        mPswView.setPasswordVisibility(true);
    }

    @OnClick({R.id.iv_wirte_back, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wirte_back:
                finish();
                break;
            case R.id.btn_ok:
                mBtnOk.setEnabled(false);
                boolean b = checkInput();
                if (b) {
                    if (NetUtil.is_Network_Available(getApplicationContext())) {
                        Intent intent = new Intent(WriteQcodeActivity.this, SweepQcodeActivity.class);
                        intent.putExtra("result", mPassWord);
                        startActivity(intent);
                    } else {
                        mBtnOk.setEnabled(true);
                        mToast.setText("网络不可用，请检查网络连接");
                        mToast.show();
                    }
                } else {
                    mBtnOk.setEnabled(true);
                }
                break;
        }
    }


    private boolean checkInput() {
        mPassWord = mPswView.getPassWord();
        if (mPassWord.length() < 8) {
            mToast.setText("请填写完整的优惠码");
            mToast.show();
            return false;
        }
        return true;
    }
}
