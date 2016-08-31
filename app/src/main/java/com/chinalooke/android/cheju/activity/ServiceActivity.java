package com.chinalooke.android.cheju.activity;

//import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.avos.avoscloud.AVUser;
import com.chinalooke.android.cheju.R;
//import com.hyphenate.easeui.EaseConstant;
//import com.hyphenate.easeui.ui.EaseChatFragment;

public class ServiceActivity extends AppCompatActivity {

//    private FragmentManager mSupportFragmentManager;
//    private EaseChatFragment mEaseChatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
//        mSupportFragmentManager = getSupportFragmentManager();
//
//        initView();
    }

//    private void initView() {
//
//        mEaseChatFragment = new EaseChatFragment();
//        Bundle args = new Bundle();
//        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
//        args.putString(EaseConstant.EXTRA_USER_ID, AVUser.getCurrentUser().getMobilePhoneNumber());
//        mEaseChatFragment.setArguments(args);
//
//
//        mSupportFragmentManager.beginTransaction()
//                .replace(R.id.fl_service, mEaseChatFragment).commit();
//
//    }
}
