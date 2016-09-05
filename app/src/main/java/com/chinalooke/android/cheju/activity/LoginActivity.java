package com.chinalooke.android.cheju.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.User;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.utills.PreferenceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.hyphenate.chat.EMClient;
//import com.hyphenate.exceptions.HyphenateException;

public class LoginActivity extends Activity {

    @Bind(R.id.et_login_phone)
    EditText mEPhone;
    @Bind(R.id.ll_login)
    LinearLayout mLlLogin;
    @Bind(R.id.et_login_password)
    EditText mEtPassword;


    private ProgressDialog mProgressDialog;
    Handler mHandler = new Handler();
    private Toast mToast;
    private String mPhone;
    private String mPassword1;
    private User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mUser = new User();
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
    }


    private void initDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    @OnClick({R.id.tv_findpwd, R.id.btn_register, R.id.btn_fastlogin, R.id.iv_wirte_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wirte_back:
                finish();
                break;
            case R.id.tv_findpwd:
                startActivity(new Intent(LoginActivity.this, FindPasswordActivity.class));
                break;
            case R.id.btn_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.btn_fastlogin:
                mPhone = mEPhone.getText().toString();
                mPassword1 = mEtPassword.getText().toString();
                if (MyUtills.CheckPhoneNumer(mPhone)) {
                    if (TextUtils.isEmpty(mPassword1)) {
                        mToast.setText("请输入密码");
                        mToast.show();
                    } else {
                        if (NetUtil.is_Network_Available(getApplicationContext())) {

                            initDialog("登录中");
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    login(mPhone, mPassword1);
                                }
                            }, 500);
                        } else {
                            mToast.setText("网络不给力,请检查网络");
                            mToast.show();
                        }
                    }
                } else {
                    mToast.setText("请输入正确的手机号码");
                    mToast.show();
                }
                break;
        }
    }


    private void login(String phone, String password) {
        AVUser.logInInBackground(phone, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null) {
                    AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                        public void done(AVException e) {
                            if (e == null) {
                                String mInstallationId = AVInstallation.getCurrentInstallation().getInstallationId();
                                AVUser currentUser = AVUser.getCurrentUser();
                                currentUser.put("installationId", mInstallationId);
                                currentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        mProgressDialog.dismiss();
                                        if (e == null) {
                                            PushService.setDefaultPushCallback(getApplicationContext(), TakePhotoActivity.class);
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            mToast.setText("登录失败");
                                            mToast.show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    mProgressDialog.dismiss();
                    mToast.setText("登录失败");
                    mToast.show();
                }
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
