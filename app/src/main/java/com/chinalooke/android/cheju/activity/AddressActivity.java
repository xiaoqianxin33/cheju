package com.chinalooke.android.cheju.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVUser;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.User;
import com.chinalooke.android.cheju.fragment.AddAdressFragment;
import com.chinalooke.android.cheju.fragment.ShowAddressFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressActivity extends FragmentActivity {


    @Bind(R.id.btn_address)
    Button mBtnAddress;
    private User mUser;
    private FragmentManager mSupportFragmentManager;

    public Button getBtnAddress() {
        return mBtnAddress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {

        String address = AVUser.getCurrentUser().getString("address");

        if (!TextUtils.isEmpty(address)) {
            mBtnAddress.setText("编辑收货地址");
            mUser = new User();
            mUser.setAddress(address);
            mUser.setRealName(AVUser.getCurrentUser().getString("realName"));
            mUser.setPhone(AVUser.getCurrentUser().getMobilePhoneNumber());
            changeFragment(0);
        }
    }

    public User getUser() {
        return mUser;
    }

    @OnClick({R.id.iv_address_back, R.id.fl_address_back, R.id.btn_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_address_back:
                finish();
                break;
            case R.id.fl_address_back:
                finish();
                break;
            case R.id.btn_address:
                mBtnAddress.setVisibility(View.GONE);
                changeFragment(1);
                break;
        }
    }

    public void changeFragment(int i) {
        mSupportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mSupportFragmentManager.beginTransaction();
        switch (i) {
            case 1:
                fragmentTransaction.replace(R.id.fl_address, new AddAdressFragment()).commit();
                break;
            case 0:
                fragmentTransaction.replace(R.id.fl_address, new ShowAddressFragment()).commit();
                break;

        }
    }
}
