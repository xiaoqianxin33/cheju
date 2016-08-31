package com.chinalooke.android.cheju.utills;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.config.MyLeanCloudApp;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiao on 2016/8/12.
 */
public class LeanCloudTools {


    public static void addAttr(String objectId, String tableName, String rowName, String content) {
        AVObject todo = AVObject.createWithoutData(tableName, objectId);
        todo.put(rowName, content);
        // 保存到云端
        todo.saveInBackground();
    }


    public static List<AVObject> inquireUseID(String tableName, String userid) {
        final List<AVObject>[] mlist = null;
        AVQuery<AVObject> query = new AVQuery<>(tableName);
        query.whereEqualTo("userid", userid);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null)
                    mlist[0] = list;
            }
        });
        return mlist[0];
    }

    public static List<AVObject> inquier(String tableName, String where, String wherecontent) {
        final List<AVObject>[] mlist = null;
        AVQuery<AVObject> query = new AVQuery<>(tableName);
        query.whereEqualTo(where, wherecontent);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null)
                    mlist[0] = list;
            }
        });
        return mlist[0];
    }


    public static void loadImage(){


        AVFile avFile=new AVFile();



    }
}
