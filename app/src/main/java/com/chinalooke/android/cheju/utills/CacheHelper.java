package com.chinalooke.android.cheju.utills;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.chinalooke.android.cheju.config.MyLeanCloudApp;


/**
 * @author max
 * @Name CacheHelper
 * @Description 缓存帮助类
 * @Date 2013-2-16
 */
public class CacheHelper {

    public static final int CONFIG_CACHE_MOBILE_TIMEOUT = 3600000 * 6; // 6 hour
    public static final int CONFIG_CACHE_WIFI_TIMEOUT = 60000 * 10; // 10 minute
    private static final String TAG = CacheHelper.class.getName();

    /**
     * 通过地址取得缓存文件 add by max [2013-3-20]
     *
     * @param url
     * @return
     */
    public static String getUrlCache(String url, Context context) {
        if (url == null) {
            return null;
        }

        String result = null;
        File file = new File(MyLeanCloudApp.sdcardCacheDir + File.separator
                + StringUtils.replaceUrlWithPlus(url));
        if (file.exists() && file.isFile()) {
            long expiredTime = System.currentTimeMillis() - file.lastModified();

            // 1. in case the system time is incorrect (the time is turn back
            // long ago)
            // 2. when the network is invalid, you can only read the cache
            if (NetUtil.is_Network_Available(context)) {
                return null;
            }
            if (NetUtil.is_Network_Available(context)
                    && expiredTime > CONFIG_CACHE_WIFI_TIMEOUT) {
                return null;
            } else if (NetUtil.is_Network_Available(context)
                    && expiredTime > CONFIG_CACHE_MOBILE_TIMEOUT) {
                return null;
            }
            try {
                result = FileUtils.readTextFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 缓存文件到sd卡中 add by max [2013-3-20]
     *
     * @param data 要保存的数据
     * @param url  数据来源地址
     */
    public static void setUrlCache(String data, String url) {
        if (MyLeanCloudApp.sdcardCacheDir == null) {
            return;
        }
        File dir = new File(MyLeanCloudApp.sdcardCacheDir);
        if (!dir.exists()
                && Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            dir.mkdirs();
        }
        File file = new File(MyLeanCloudApp.sdcardCacheDir + File.separator
                + StringUtils.replaceUrlWithPlus(url));
        try {
            // 创建缓存数据到磁盘，就是创建文件
            FileUtils.writeTextFile(file, data);
        } catch (IOException e) {
            Log.d(TAG, "write " + file.getAbsolutePath() + " data failed!");
            e.printStackTrace();
        }
    }
}
