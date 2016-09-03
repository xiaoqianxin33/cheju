package com.chinalooke.android.cheju.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.User;
import com.chinalooke.android.cheju.utills.MyUtills;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.hyphenate.chat.EMClient;
//import com.hyphenate.exceptions.HyphenateException;

public class LoginActivity extends Activity {

    @Bind(R.id.et_login_phone)
    EditText mEPhone;
    @Bind(R.id.et_login_sms)
    EditText mEtSms;
    @Bind(R.id.btn_login_getsms)
    Button mBtnsms;
    @Bind(R.id.iv_login_back)
    ImageView mIvLoginBack;
    @Bind(R.id.btn_fastlogin)
    Button mBtnFastlogin;
    @Bind(R.id.ll_login)
    LinearLayout mLlLogin;
    @Bind(R.id.ll_find)
    LinearLayout mLlFind;
    @Bind(R.id.ll_register)
    LinearLayout mLlRegister;
    @Bind(R.id.et_register_phone)
    EditText mEtRegisterPhone;
    @Bind(R.id.et_register_password)
    EditText mEtRegisterPassword;
    @Bind(R.id.et_register_email)
    EditText mEtRegisterEmail;
    @Bind(R.id.et_login_password)
    EditText mEtPassword;
    @Bind(R.id.et_register_password_again)
    EditText mEtPasswordAgain;
    @Bind(R.id.et_findpwd_sms)
    EditText mEtFindpwdSms;
    @Bind(R.id.et_findpwd)
    EditText mEtFindPwd;
    @Bind(R.id.et_find_pwd)
    EditText mEtPwd;
    @Bind(R.id.et_find_password_again)
    EditText mEtFindPasswordAgain;
    @Bind(R.id.btn_find_getsms)
    Button mBtnFindGetsms;
    private String phoneNumer;
    private ProgressDialog mProgressDialog;
    Handler mHandler = new Handler();
    private User mUser;
    private Toast mToast;
    private String mEmail;
    private String mPhone;
    private String mPassword;
    private String mPassword1;
    private String mPasswordAgain;
    private String mFindPhone;
    private String mFindSms;
    private String mNewPwd;
    private String mFindPwdAgain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mLlRegister.setVisibility(View.GONE);
        mLlLogin.setVisibility(View.VISIBLE);
        mLlFind.setVisibility(View.GONE);
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

    @OnClick({R.id.btn_login_getsms, R.id.btn_fastlogin, R.id.iv_login_back, R.id.btn_register
            , R.id.btn_complete, R.id.btn_back, R.id.tv_findpwd, R.id.btn_find_getsms
            , R.id.btn_find, R.id.btn_back_find})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back_find:
                mLlFind.setVisibility(View.GONE);
                mLlRegister.setVisibility(View.GONE);
                mLlLogin.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_find:
                if (checkFind()) {
                    MyUtills.showSingerDialog(this, "提示", "确定找回密码吗", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initDialog("请求发送中");
                            findPwd();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
                break;
            case R.id.btn_find_getsms:
                mFindPhone = mEtFindPwd.getText().toString();
                if (MyUtills.CheckPhoneNumer(mFindPhone)) {
                    initDialog("正在发送短信");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findSms();
                        }
                    }, 500);
                } else {
                    mToast.setText("请输入正确的手机号码");
                    mToast.show();
                }
                break;
            case R.id.tv_findpwd:
                mLlLogin.setVisibility(View.GONE);
                mLlRegister.setVisibility(View.GONE);
                mLlFind.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_back:
                mLlLogin.setVisibility(View.VISIBLE);
                mLlRegister.setVisibility(View.GONE);
                mLlFind.setVisibility(View.GONE);
                break;
            case R.id.btn_register:
                mLlLogin.setVisibility(View.GONE);
                mLlRegister.setVisibility(View.VISIBLE);
                mLlFind.setVisibility(View.GONE);
                break;
            case R.id.btn_complete:
                if (checkRegister()) {
                    initDialog("注册中");
                    final String smsNumer = mEtSms.getText().toString();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkSms(smsNumer);
                        }
                    }, 500);
                }
                break;

            case R.id.btn_login_getsms:
                phoneNumer = mEtRegisterPhone.getText().toString();
                if (MyUtills.CheckPhoneNumer(phoneNumer)) {
                    initDialog("正在发送中...");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendSms(phoneNumer);
                        }
                    }, 500);
                } else {
                    mToast.setText("请输入正确的手机号码");
                    mToast.show();
                }
                break;

            case R.id.iv_login_back:
                finish();
                break;
            case R.id.btn_fastlogin:
                mPhone = mEPhone.getText().toString();
                mPassword1 = mEtPassword.getText().toString();
                if (MyUtills.CheckPhoneNumer(mPhone)) {
                    if (TextUtils.isEmpty(mPassword1)) {
                        mToast.setText("请输入密码");
                        mToast.show();
                    } else {
                        initDialog("登录中");
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                login(mPhone, mPassword1);
                            }
                        }, 500);
                    }
                } else {
                    mToast.setText("请输入正确的手机号码");
                    mToast.show();
                }
                break;
        }
    }

    private void findPwd() {
        AVUser.resetPasswordBySmsCodeInBackground(mFindSms, mFindPwdAgain, new UpdatePasswordCallback() {
            @Override
            public void done(AVException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    mToast.setText("密码重置成功");
                    mToast.show();
                    initDialog("登录中");
                    login(mFindPhone, mFindPwdAgain);
                } else {
                    mToast.setText(e.getMessage());
                    mToast.show();
                }
            }
        });
    }

    private boolean checkFind() {
        mFindPhone = mEtFindPwd.getText().toString();
        if (!MyUtills.CheckPhoneNumer(mFindPhone)) {
            mToast.setText("请填写正确的手机号码");
            mToast.show();
            return false;
        }


        mFindSms = mEtFindpwdSms.getText().toString();
        if (TextUtils.isEmpty(mFindSms)) {
            mToast.setText("请填写短信验证码");
            mToast.show();
            return false;
        }

        mNewPwd = mEtPwd.getText().toString();
        if (!MyUtills.password(mNewPwd)) {
            mToast.setText("请填写六位新密码");
            mToast.show();
            return false;
        }

        mFindPwdAgain = mEtFindPasswordAgain.getText().toString();
        if (!mNewPwd.equals(mFindPwdAgain)) {
            mToast.setText("两次密码输入不一致");
            mToast.show();
            return false;
        }
        return true;
    }

    private void login(String phone, String password) {
        AVUser.logInInBackground(phone, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    mToast.setText(e.getMessage());
                    mToast.show();
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
        mPasswordAgain = mEtPasswordAgain.getText().toString();
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
        } else if (!MyUtills.CheckEmail(mEmail)) {
            mToast.setText("邮箱格式错误，请重新填写");
            mToast.show();
            return false;
        }
        return true;
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
                    avUser.put("email", mEmail);
                    avUser.put("phone", mPhone);
                    avUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            mProgressDialog.dismiss();
                            if (e == null) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
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

    private void sendSms(String phoneNumer) {
        AVOSCloud.requestSMSCodeInBackground(phoneNumer, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    mToast.setText("短信发送成功！");
                    mToast.show();
                    new CountTimer(60000, 1000, mBtnsms) {
                    }.start();
                }
            }
        });
    }

    private void findSms() {
        AVUser.requestPasswordResetBySmsCodeInBackground(mFindPhone, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    mToast.setText("短信发送成功！");
                    mToast.show();
                    new CountTimer(60000, 1000, mBtnFindGetsms) {
                    }.start();
                } else {
                    mToast.setText(e.getMessage());
                    mToast.show();
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
