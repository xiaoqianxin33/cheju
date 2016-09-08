package com.chinalooke.android.cheju.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.utills.StringUtils;
import com.chinalooke.android.cheju.view.XListView;

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
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.fl_load)
    FrameLayout mFlLoad;
    @Bind(R.id.tv_tui)
    TextView mTvTui;
    private List<AVUser> mCustomers;
    private List<AVUser> mAVUsers = new ArrayList<>();
    private MyAdapt mMyAdapt;
    private int mInt;
    private Handler mHandler = new Handler();
    private Toast mToast;
    private List<String> mTimes = new ArrayList<>();
    private List<String> mMessage = new ArrayList<>();
    private List<AVObject> mMessageList = new ArrayList<>();
    private List<AVUser> mArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        setContentView(R.layout.activity_customer);
        ButterKnife.bind(this);
        initView();
        initData();

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
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            for (int i = 0; i < mCustomers.size(); i++) {
                AVUser avUser = mCustomers.get(i);
                String objectId = avUser.getObjectId();
                AVQuery<AVObject> query = new AVQuery<>("Statistics");
                query.whereEqualTo("userId", objectId);
                final int finalI = i;
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            if (list != null && list.size() != 0)
                                mMessageList.addAll(list);
                            if (finalI == mCustomers.size() - 1) {
                                mMyAdapt.notifyDataSetChanged();
                            }
                        } else {
                            mToast.setText("暂无客户消费情况");
                            mToast.show();
                        }
                    }
                });

            }
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }
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
            if (mMessageList != null) {
                return mMessageList.size();
            } else {
                return 0;
            }
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
        AVObject avObject = mMessageList.get(positon);
        Date date = avObject.getDate("date");
        String userId = avObject.getString("userId");
        viewHolder.mTvTime.setText(StringUtils.getTime(date));
        viewHolder.mTvMessage.setText("客户" + userId.substring(0, 5) + "********消费了" + avObject.getNumber("price"));
    }

    private void initCustomerData() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {

            AVQuery<AVUser> userQuery = new AVQuery<>("_User");

            userQuery.whereEqualTo("referrer", AVUser.getCurrentUser().getMobilePhoneNumber());

            userQuery.findInBackground(new FindCallback<AVUser>() {
                @Override
                public void done(List<AVUser> list, AVException e) {
                    mPbLoad.setVisibility(View.GONE);
                    if (e == null) {
                        if (list.size() != 0) {
                            mFlLoad.setVisibility(View.GONE);
                            mArrayList = list;
                        }
                    }
                }
            });
        } else {
            mPbLoad.setVisibility(View.GONE);
            mTvTui.setText("网络不可用,请检查网络连接");
        }
    }
}
