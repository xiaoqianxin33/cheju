package com.chinalooke.android.cheju.adapter;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiao on 2016/9/28.
 */

public abstract class MyBaseAdaper<ITEMBEANTYPE> extends BaseAdapter {
    protected List<ITEMBEANTYPE> mDataSource = new ArrayList<>();

    public MyBaseAdaper(List<ITEMBEANTYPE> dataSource) {
        mDataSource = dataSource;
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
