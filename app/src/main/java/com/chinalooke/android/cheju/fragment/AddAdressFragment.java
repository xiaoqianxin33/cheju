package com.chinalooke.android.cheju.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.AddressActivity;
import com.chinalooke.android.cheju.activity.WheelViewActivity;
import com.chinalooke.android.cheju.utills.MyUtills;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddAdressFragment extends Fragment {


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
                ((AddressActivity) getActivity()).changeFragment(0);
                break;
            case R.id.btn_address_ok:
                boolean checkInput = checkInput();
                if (checkInput) {
                    initDialog("正在保存中");
                    saveAddress();
                }
                break;
            case R.id.tv_location_address:
                startActivityForResult(new Intent(getActivity(), WheelViewActivity.class), REQUSET);
                break;
        }
    }

    private void saveAddress() {
        mCurrentUser = AVUser.getCurrentUser();
        AVObject tag2 = new AVObject("Address");// 构建对象
        tag2.put("name", mName);
        tag2.put("phone", mPhone);
        tag2.put("address", mAddress);
        AVRelation<AVObject> relation = mCurrentUser.getRelation("address");

        relation.add(tag2);
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    mToast.setText("地址保存成功！");
                    mToast.show();
                } else {
                    mToast.setText(e.getMessage());
                    mToast.show();
                }
            }
        });

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

        String location = mTvLocationAddress.getText().toString();
        if (TextUtils.isEmpty(location)) {
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
}
