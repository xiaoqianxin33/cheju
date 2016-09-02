package com.chinalooke.android.cheju.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.AddressActivity;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.lljjcoder.citypickerview.widget.CityPickerView;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddAdressFragment extends Fragment implements AMapLocationListener {


    private static final int REQUSET = 1;
    @Bind(R.id.et_adrress_name)
    EditText mEtAdrressName;
    @Bind(R.id.et_phone_address)
    EditText mEtPhoneAddress;
    @Bind(R.id.et_detail_address)
    EditText mEtDetailAddress;
    @Bind(R.id.tv_location_address)
    TextView mTvLocationAddress;
    private String mAddress;
    private String mName;
    private AVUser mCurrentUser;
    private String mPhone;
    private Toast mToast;
    private ProgressDialog mProgressDialog;
    private AddressActivity mAddressActivity;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private String aMapLocationAddress;
    private String mLocation;
    private Button mBtnAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_address, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        mAddressActivity = (AddressActivity) getActivity();
        mBtnAddress = mAddressActivity.getBtnAddress();
        location();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.btn_address_cancle, R.id.btn_address_ok, R.id.tv_location_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_address_cancle:
                mBtnAddress.setVisibility(View.VISIBLE);
                mAddressActivity.switchContent(AddAdressFragment.this, mAddressActivity.getShowAddressFragment());

                break;
            case R.id.btn_address_ok:
                boolean checkInput = checkInput();
                if (checkInput) {
                    initDialog("正在保存中");
                    saveAddress();
                }
                break;
            case R.id.tv_location_address:
                selectLocation();
                break;
        }
    }

    private void saveAddress() {

        mCurrentUser = AVUser.getCurrentUser();
        final AVObject tag2 = new AVObject("Address");// 构建对象
        tag2.put("name", mName);
        tag2.put("phone", mPhone);
        tag2.put("address", mLocation + mAddress);

        AVObject.saveAllInBackground(Arrays.asList(tag2), new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    AVRelation<AVObject> relation = mCurrentUser.getRelation("address");
                    relation.add(tag2);
                    mCurrentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            mProgressDialog.dismiss();
                            if (e == null) {
                                mToast.setText("地址保存成功！");
                                mToast.show();
                                mAddressActivity.switchContent(mAddressActivity.getAddAdressFragment(), mAddressActivity.getShowAddressFragment());
                                mBtnAddress.setVisibility(View.VISIBLE);
                                ShowAddressFragment showAddressFragment = mAddressActivity.getShowAddressFragment();
                                showAddressFragment.refreshData();
                            } else {
                                mToast.setText(e.getMessage());
                                mToast.show();
                            }
                        }
                    });
                } else {
                    mProgressDialog.dismiss();
                    mToast.setText(e.getMessage());
                    mToast.show();
                }
            }
        });

    }


    private void location() {

        mLocationClient = new AMapLocationClient(getActivity());

        mLocationClient.setLocationListener(this);

        mLocationOption = new AMapLocationClientOption();

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mLocationOption.setInterval(5000);

        mLocationOption.setWifiActiveScan(false);

        mLocationOption.setOnceLocationLatest(true);

        mLocationClient.setLocationOption(mLocationOption);

        mLocationClient.startLocation();

    }


    private void initDialog(String message) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    private boolean checkInput() {
        mName = mEtAdrressName.getText().toString();
        if (TextUtils.isEmpty(mName)) {
            MyUtills.showToast(getActivity(), "收货人姓名不能为空");
            return false;
        }

        mPhone = mEtPhoneAddress.getText().toString();
        if (TextUtils.isEmpty(mPhone)) {
            MyUtills.showToast(getActivity(), "收货人号码不能为空");
            return false;
        }

        mAddress = mEtDetailAddress.getText().toString();
        if (TextUtils.isEmpty(mAddress)) {
            MyUtills.showToast(getActivity(), "详细地址不能为空");
            return false;
        }

        mLocation = mTvLocationAddress.getText().toString();
        if (TextUtils.isEmpty(mLocation)) {
            MyUtills.showToast(getActivity(), "请选择地址");
            return false;
        }
        return true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.REQUSET && resultCode == 1) {
            String location = data.getStringExtra("location1");
            if (location != null)
                mTvLocationAddress.setText(location);
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        aMapLocationAddress = aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict();
        if (!TextUtils.isEmpty(aMapLocationAddress)) {
            mTvLocationAddress.setText(aMapLocationAddress);
        }
    }


    private void selectLocation() {
        final CityPickerView cityPickerView = new CityPickerView(getActivity());
        cityPickerView.setTextColor(Color.BLACK);
        cityPickerView.setTextSize(20);
        cityPickerView.setVisibleItems(5);
        cityPickerView.setIsCyclic(false);
        cityPickerView.show();

        cityPickerView.setOnCityItemClickListener(new CityPickerView.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                mTvLocationAddress.setText(citySelected[0] + citySelected[1] + citySelected[2]);
            }
        });
    }
}
