package com.chinalooke.android.cheju.fragment.salesman;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVUser;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.utills.MyUtills;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PerformanceFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_performance, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_loginout)
    public void onClick() {
        AVUser.logOut();
        MyUtills.showToast(getActivity(), "退出成功!");
    }
}
