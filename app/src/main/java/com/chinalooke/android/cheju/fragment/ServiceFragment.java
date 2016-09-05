package com.chinalooke.android.cheju.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVUser;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.LoginActivity;
import com.chinalooke.android.cheju.activity.MainActivity;
import com.chinalooke.android.cheju.activity.ServiceActivity;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.utills.PreferenceUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiao on 2016/8/6.
 */
public class ServiceFragment extends Fragment {

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                login();
            }

        }
    };
    private String mName;
    private String mPsd;
    private AVUser mCurrentUser;

    private void login() {
        EMClient.getInstance().login(mName, mPsd, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        ButterKnife.bind(this, view);
        mCurrentUser = ((MainActivity) getActivity()).getCurrentUser();
        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser = AVUser.getCurrentUser();
    }

    private void initData() {
        if (mCurrentUser != null) {
            if (NetUtil.is_Network_Available(getActivity())) {

                mName = "a" + AVUser.getCurrentUser().getMobilePhoneNumber();
                mPsd = AVUser.getCurrentUser().getMobilePhoneNumber();
                String hx = PreferenceUtils.getPrefString(getActivity(), "hx", "");
                if (TextUtils.isEmpty(hx)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().createAccount(mName, mPsd);
                                PreferenceUtils.setPrefString(getActivity(), "hx", "a" + AVUser.getCurrentUser().getMobilePhoneNumber());
                                mHandler.sendEmptyMessage(1);
                            } catch (HyphenateException e) {
                                login();
                                e.printStackTrace();
                            }
                        }
                    }) {

                    }.start();
                } else {
                    login();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.btn_inquir_service, R.id.btn_phone_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_inquir_service:
                if (mCurrentUser != null) {
                    startActivity(new Intent(getActivity(), ServiceActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.btn_phone_service:
                Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + 400));
                startActivity(intentPhone);
                break;
        }
    }
}
