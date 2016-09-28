package com.chinalooke.android.cheju.activity.business;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.LoginActivity;
import com.chinalooke.android.cheju.activity.MainActivity;
import com.chinalooke.android.cheju.config.MyLeanCloudApp;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;

import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class BusinessMainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private AVObject mAvObject;
    private Toast mToast;
    private int RC_CAMERA_AND_WIFI = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_main);
        ButterKnife.bind(this);
        mToast = MyLeanCloudApp.getToast();
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            bindBusinessShop();
        } else {
            mToast.setText("网络不可用，无法获取商店信息");
            mToast.show();
        }
    }

    private long lastClickTime = 1;

    @OnClick({R.id.rl_goods, R.id.rl_qcode, R.id.rl_profit})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        int MIN_CLICK_DELAY_TIME = 1000;
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.rl_goods:
                    if (mAvObject != null) {
                        Intent intent = new Intent(BusinessMainActivity.this, MyGoodsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("shop", mAvObject);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    break;
                case R.id.rl_qcode:
                    String[] perms = {Manifest.permission.CAMERA, Manifest.permission.CHANGE_WIFI_STATE};
                    if (EasyPermissions.hasPermissions(this, perms)) {
                        if (mAvObject != null) {
                            Intent intent = new Intent(BusinessMainActivity.this, QRCodeActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("shop", mAvObject);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        break;
                    } else {
                        EasyPermissions.requestPermissions(this, "拍照需要摄像头权限",
                                RC_CAMERA_AND_WIFI, perms);
                    }

                case R.id.rl_profit:
                    if (mAvObject != null) {
                        startActivity(new Intent(BusinessMainActivity.this, ProfitActivity.class));
                    }
                    break;
            }
        }
    }

    private void bindBusinessShop() {
        AVQuery<AVObject> query = new AVQuery<>("BusinessShop");
        AVUser currentUser = AVUser.getCurrentUser();
        String objectId = currentUser.getObjectId();
        query.whereEqualTo("userID", objectId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mAvObject = list.get(0);
                    MyLeanCloudApp.setAVObject(mAvObject);
                } else {
                    mToast.setText("获取商店信息失败");
                    mToast.show();
                }
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MyUtills.showDialog(BusinessMainActivity.this, "提示", "确定退出车聚吗?");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (mAvObject != null && requestCode == RC_CAMERA_AND_WIFI) {
            Intent intent = new Intent(BusinessMainActivity.this, QRCodeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("shop", mAvObject);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
