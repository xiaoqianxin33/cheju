package com.chinalooke.android.cheju.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.utills.MyUtills;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindPasswordActivity extends AppCompatActivity {

    @Bind(R.id.et_findpwd)
    EditText mEtFindpwd;
    @Bind(R.id.et_findpwd_sms)
    EditText mEtFindpwdSms;
    @Bind(R.id.et_find_pwd)
    EditText mEtFindPwd;
    @Bind(R.id.et_find_password_again)
    EditText mEtFindPasswordAgain;
    @Bind(R.id.btn_find_getsms)
    Button mBtnFindGetsms;
    private String mFindPhone;
    private Toast mToast;
    private String mFindSms;
    private String mNewPwd;
    private String mFindPwdAgain;
    private ProgressDialog mProgressDialog;

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        ButterKnife.bind(this);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    @OnClick({R.id.btn_find_getsms, R.id.btn_find, R.id.iv_login_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_find_getsms:
                mFindPhone = mEtFindpwd.getText().toString();
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
            case R.id.iv_login_back:
                finish();
                break;
        }
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


    private void login(String phone, String password) {
        AVUser.logInInBackground(phone, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    startActivity(new Intent(FindPasswordActivity.this, MainActivity.class));
                    finish();
                } else {
                    mToast.setText(e.getMessage());
                    mToast.show();
                }
            }
        });
    }

    private void initDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    private boolean checkFind() {
        mFindPhone = mEtFindpwd.getText().toString();
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

        mNewPwd = mEtFindPwd.getText().toString();
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

}
