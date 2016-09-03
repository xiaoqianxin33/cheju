package com.chinalooke.android.cheju.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.view.XListView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerActivity extends AppCompatActivity implements XListView.IXListViewListener {

    @Bind(R.id.xlistview_customer)
    XListView mXlistviewCustomer;
    private List<AVUser> mCustomers;
    private List<AVUser> mAVUsers = new ArrayList<>();
    private MyAdapt mMyAdapt;
    private int mInt;
    private Handler mHandler = new Handler();
    private Toast mToast;
    private List<String> mTimes = new ArrayList<>();
    private List<String> mMessage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        setContentView(R.layout.activity_customer);
        ButterKnife.bind(this);
        initData();
        initView();

    }

    private void initData() {
        mTimes.add("2016-9-1");
        mTimes.add("2016-9-2");
        mTimes.add("2016-9-3");
        mTimes.add("2016-9-4");
        mMessage.add("客户158********购买了商业险");
        mMessage.add("客户138********购买了商业险");
        mMessage.add("客户157********购买了强制险");
        mMessage.add("客户156********购买了商业险");
        mMessage.add("客户155********购买了商业险");

        mCustomers = (List<AVUser>) getIntent().getSerializableExtra("customers");
        getItems();
    }

    private void getItems() {
        for (int i = mInt; i < mInt + 5; i++) {
            if (i == mCustomers.size()) {
                mToast.setText("没有更多了");
                mToast.show();
                mXlistviewCustomer.dropFooter();
                break;
            }
            mAVUsers.add(mCustomers.get(i));
        }
    }

    private void initView() {
        mXlistviewCustomer.setPullLoadEnable(true);
//        mXlistviewCustomer.setPullRefreshEnable(false);
//        mXlistviewCustomer.setAutoLoadEnable(true);
        mXlistviewCustomer.setXListViewListener(this);
        mXlistviewCustomer.setRefreshTime(getTime());
        mMyAdapt = new MyAdapt();
        mXlistviewCustomer.setAdapter(mMyAdapt);
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    @OnClick({R.id.iv_wirte_back, R.id.tv_lookscro})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wirte_back:
                finish();
                break;
            case R.id.tv_lookscro:
                startActivity(new Intent(CustomerActivity.this, ScoreActivity.class));
                break;
        }
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInt = 0;
                mAVUsers.clear();
                getItems();
                mMyAdapt = new MyAdapt();
                mXlistviewCustomer.setAdapter(mMyAdapt);
                onLoad();
            }
        }, 50);
    }

    private void onLoad() {
        mXlistviewCustomer.stopRefresh();
        mXlistviewCustomer.stopLoadMore();
        mXlistviewCustomer.setRefreshTime(getTime());
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInt = mInt + 5;
                getItems();
                mMyAdapt.notifyDataSetChanged();
                onLoad();
            }
        }, 200);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            mXlistviewCustomer.autoRefresh();
        }
    }


    class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return mTimes.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(CustomerActivity.this, R.layout.item_fragment_custom_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder) convertView.getTag();
            }

            setDtails(viewHolder, position);

//            viewHolder.mTvDial.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String phoneNumber = mAVUsers.get(position).getUsername();
//                    Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
//                    startActivity(intentPhone);
//                }
//            });

            return convertView;
        }
    }

    static class ViewHolder {

        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_message)
        TextView mTvMessage;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

    private void setDtails(ViewHolder viewHolder, int positon) {
        viewHolder.mTvTime.setText(mTimes.get(positon));
        viewHolder.mTvMessage.setText(mMessage.get(positon));
    }
}
