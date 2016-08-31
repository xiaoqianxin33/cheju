package com.chinalooke.android.cheju.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiao on 2016/8/9.
 */
public class MyListViewAdapt extends BaseAdapter {

    private List datas=new ArrayList();
    private Activity mActivity;
    private int mResouce;




    public MyListViewAdapt(List list) {
        this.datas=list;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        mActivity.getLayoutInflater().inflate(mResouce,null);

        return null;
    }
}
