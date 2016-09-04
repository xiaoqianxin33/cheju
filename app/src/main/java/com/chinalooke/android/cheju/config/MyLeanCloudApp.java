package com.chinalooke.android.cheju.config;

import android.app.ActivityManager;
import android.app.Application;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

import java.util.Iterator;
import java.util.List;


/**
 * Created by xiao on 2016/8/5.
 */
public class MyLeanCloudApp extends Application {


    private MyLeanCloudApp appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "IXN9lU9PD8RGArnaNM08pX4s-gzGzoHsz", "g4Dt8awTrsnjbsYQpPu1dCMj");
        MultiDex.install(this);
        appContext = this;
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        if (processAppName == null || !processAppName.equalsIgnoreCase(appContext.getPackageName())) {
            return;
        }
        EMOptions options = new EMOptions();
        EMClient.getInstance().init(getApplicationContext(), options);
        EaseUI.getInstance().init(getApplicationContext(), options);
        EMClient.getInstance().setDebugMode(true);

    }


    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {

            }
        }
        return processName;
    }

}
