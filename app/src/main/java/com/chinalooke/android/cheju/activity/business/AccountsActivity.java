package com.chinalooke.android.cheju.activity.business;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.config.MyLeanCloudApp;
import com.chinalooke.android.cheju.utills.MyUtills;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountsActivity extends AppCompatActivity {

    @Bind(R.id.et_name)
    EditText mEtName;
    @Bind(R.id.et_ali)
    EditText mEtAli;
    @Bind(R.id.et_phone)
    EditText mEtPhone;
    @Bind(R.id.btn_ok)
    Button mBtnOk;
    private String mName;
    private Toast mToast;
    private String mAli;
    private String mPhone;
    int MIN_CLICK_DELAY_TIME = 1000;
    long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        ButterKnife.bind(this);
        mToast = MyLeanCloudApp.getToast();
    }


    @OnClick({R.id.iv_wirte_back, R.id.btn_ok})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.iv_wirte_back:
                    finish();
                    break;
                case R.id.btn_ok:
                    boolean b = checkInput();
                    if (b) {

                    }
                    break;
            }
        }

    }

    private boolean checkInput() {
        mName = mEtName.getText().toString();
        if (TextUtils.isEmpty(mName)) {
            mToast.setText("姓名不能为空");
            mToast.show();
            mEtName.setFocusable(true);
            mEtName.setFocusableInTouchMode(true);
            mEtName.requestFocus();
            return false;
        }
        mAli = mEtAli.getText().toString();
        if (TextUtils.isEmpty(mAli)) {
            mToast.setText("支付宝账号不能为空");
            mToast.show();
            mEtAli.setFocusable(true);
            mEtAli.setFocusableInTouchMode(true);
            mEtAli.requestFocus();
            return false;
        }
        mPhone = mEtPhone.getText().toString();
        if (TextUtils.isEmpty(mPhone)) {
            mToast.setText("手机号不能为空");
            mToast.show();
            mEtPhone.setFocusable(true);
            mEtPhone.setFocusableInTouchMode(true);
            mEtPhone.requestFocus();
            return false;
        } else if (!MyUtills.CheckPhoneNumer(mPhone)) {
            mToast.setText("请输入正确的手机号");
            mToast.show();
            mEtPhone.setFocusable(true);
            mEtPhone.setFocusableInTouchMode(true);
            mEtPhone.requestFocus();
            return false;
        }
        return true;
    }
}
