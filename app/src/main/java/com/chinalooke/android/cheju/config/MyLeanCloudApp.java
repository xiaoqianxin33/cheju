package com.chinalooke.android.cheju.config;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
//import com.hyphenate.easeui.controller.EaseUI;

/**
 * Created by xiao on 2016/8/5.
 */
public class MyLeanCloudApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "IXN9lU9PD8RGArnaNM08pX4s-gzGzoHsz", "g4Dt8awTrsnjbsYQpPu1dCMj");
        EMOptions options = new EMOptions();
        EMClient.getInstance().init(this, options);

//        EaseUI.getInstance().init(getApplicationContext(), options);
        EMClient.getInstance().setDebugMode(true);
        MultiDex.install(this);

    }

}
