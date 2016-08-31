package com.chinalooke.android.cheju.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.AddressActivity;
import com.chinalooke.android.cheju.bean.User;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ShowAddressFragment extends Fragment {


    @Bind(R.id.tv_address_name)
    TextView mTvAddressName;
    @Bind(R.id.tv_address_phone)
    TextView mTvAddressPhone;
    @Bind(R.id.tv_address_address)
    TextView mTvAddressAddress;
    private User mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_adress, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUser = ((AddressActivity) getActivity()).getUser();
        initView();
    }

    private void initView() {
        mTvAddressAddress.setText(mUser.getAddress());
        mTvAddressName.setText(mUser.getName());
        mTvAddressPhone.setText(mUser.getPhone());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
