package com.chinalooke.android.cheju.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.et_register_phone)
    EditText mEtRegisterPhone;
    @Bind(R.id.et_login_sms)
    EditText mEtLoginSms;
    @Bind(R.id.et_register_password)
    EditText mEtRegisterPassword;
    @Bind(R.id.et_register_password_again)
    EditText mEtRegisterPasswordAgain;
    @Bind(R.id.btn_login_getsms_r)
    Button mBtnLoginGetsms;
    @Bind(R.id.et_register_email)
    EditText mEtRegisterEmail;
    private String phoneNumer;
    private ProgressDialog mProgressDialog;

    Handler mHandler = new Handler();
    private Toast mToast;
    private String mPhone;
    private String mPassword;
    private String mPasswordAgain;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);


    }

    @OnClick({R.id.btn_login_getsms_r, R.id.btn_complete, R.id.iv_login_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_getsms_r:
                phoneNumer = mEtRegisterPhone.getText().toString();
                if (MyUtills.CheckPhoneNumer(phoneNumer)) {
                    if (NetUtil.is_Network_Available(getApplicationContext())) {
                        initDialog("正在发送中...");
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendSms(phoneNumer);
                            }
                        }, 500);
                    } else {
                        mToast.setText("网络不给力,请检查网络");
                        mToast.show();
                    }
                } else {
                    mToast.setText("请输入正确的手机号码");
                    mToast.show();
                }
                break;

            case R.id.btn_complete:
                if (checkRegister()) {
                    if (NetUtil.is_Network_Available(getApplicationContext())) {
                        initDialog("注册中");
                        final String smsNumer = mEtLoginSms.getText().toString();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                checkSms(smsNumer);
                            }
                        }, 500);
                    } else {
                        mToast.setText("网络不给力,请检查网络");
                        mToast.show();
                    }
                }
                break;
            case R.id.iv_login_back:
                finish();
                break;

        }
    }


    private void checkSms(String s) {
        AVUser.signUpOrLoginByMobilePhoneInBackground(phoneNumer, s, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    mToast.setText("注册成功！");
                    mToast.show();
                    mProgressDialog.setMessage("登录中");
                    avUser.put("password", mPassword);
                    avUser.put("phone", mPhone);
                    if (!TextUtils.isEmpty(mEmail))
                        avUser.put("referrer", mEmail);
                    avUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                login();
                            } else {
                                mProgressDialog.dismiss();
                                mToast.setText("登录失败，请检查网络情况");
                                mToast.show();
                            }
                        }
                    });
                } else {
                    mToast.setText("验证码错误，请重新填写");
                    mToast.show();
                }
            }
        });
    }

    private void login() {
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
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
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
    }


    private boolean checkRegister() {


        mPhone = mEtRegisterPhone.getText().toString();
        if (!MyUtills.CheckPhoneNumer(mPhone)) {
            mToast.setText("请填写正确的手机号码");
            mToast.show();
            return false;
        }

        mPassword = mEtRegisterPassword.getText().toString();
        mPasswordAgain = mEtRegisterPasswordAgain.getText().toString();
        if (!MyUtills.password(mPassword)) {
            mToast.setText("请填写至少六位密码");
            mToast.show();
            return false;
        } else if (!mPassword.equals(mPasswordAgain)) {
            mToast.setText("两次输入密码不一致");
            mToast.show();
            return false;
        }

        mEmail = mEtRegisterEmail.getText().toString();
        if (TextUtils.isEmpty(mEmail)) {
            return true;
        } else if (!MyUtills.CheckPhoneNumer(mEmail)) {
            mToast.setText("请重新正确的推荐人手机号码");
            mToast.show();
            return false;
        }
        return true;
    }

    private void initDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    private void sendSms(String phoneNumer) {
        AVOSCloud.requestSMSCodeInBackground(phoneNumer, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    mToast.setText("短信发送成功！");
                    mToast.show();
                    new CountTimer(60000, 1000, mBtnLoginGetsms) {
                    }.start();
                }
            }
        });
    }


    public class CountTimer extends CountDownTimer {
        private Button mButton;

        public CountTimer(long millisInFuture, long countDownInterval, Button button) {
            super(millisInFuture, countDownInterval);
            this.mButton = button;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mButton.setText("获取验证码(" + millisUntilFinished / 1000 + ")");
            mButton.setClickable(false);
            mButton.setBackgroundResource(R.drawable.shape2);
        }

        @Override
        public void onFinish() {
            mButton.setText(R.string.getsms);
            mButton.setClickable(true);
            mButton.setBackgroundResource(R.drawable.shape);
        }

    }
}
