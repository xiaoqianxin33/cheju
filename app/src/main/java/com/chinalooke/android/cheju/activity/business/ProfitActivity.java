package com.chinalooke.android.cheju.activity.business;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.config.MyLeanCloudApp;
import com.chinalooke.android.cheju.utills.DateUtils;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.view.SyListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfitActivity extends AppCompatActivity {

    @Bind(R.id.tv_time_year)
    TextView mTvTimeYear;
    @Bind(R.id.tv_time_month)
    TextView mTvTimeMonth;
    @Bind(R.id.tv_score)
    TextView mTvScore;
    @Bind(R.id.tv_scores)
    TextView mTvScores;
    @Bind(R.id.tv_nojiesuan)
    TextView mTvNojiesuan;
    @Bind(R.id.tv_no)
    TextView mTvNo;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.lv_goods)
    ListView mLvGoods;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    @Bind(R.id.activity_profit)
    LinearLayout mActivityProfit;
    private AVUser mCurrentUser;
    private AVObject mShop;
    private List<AVObject> mStatics = new ArrayList<>();
    private MyAdapter mMyAdapter;
    private String mTimeAll;
    private Date mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);
        ButterKnife.bind(this);
        mCurrentUser = AVUser.getCurrentUser();
        mShop = MyLeanCloudApp.getAVObject();
        mMyAdapter = new MyAdapter();
        mLvGoods.setAdapter(mMyAdapter);
        initData();
        initView();

    }

    private void initData() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            AVQuery<AVObject> query = new AVQuery<>("Order");
            query.whereEqualTo("shopId", mShop.getObjectId());
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    mProgressBar.setVisibility(View.GONE);
                    if (e == null) {
                        if (list == null || list.size() == 0) {
                            mTvNo.setText("暂无数据");
                            mTvNo.setVisibility(View.VISIBLE);
                        } else {
                            for (AVObject avObject : list) {
                                String status = avObject.getString("status");
                                if ("4".equals(status)) {
                                    Date updatedAt = avObject.getUpdatedAt();
                                    String timeAll = MyUtills.getTimeAll(updatedAt);
                                    if (mTimeAll.substring(0, 7).equals(timeAll.substring(0, 7))) {
                                        mStatics.add(avObject);
                                    }
                                }
                            }
                            if (mStatics.size() == 0) {
                                mTvNo.setText("该月暂无数据");
                                mTvNo.setVisibility(View.VISIBLE);
                            } else {
                                Collections.sort(mStatics, new Comparator<AVObject>() {
                                    @Override
                                    public int compare(AVObject lhs, AVObject rhs) {
                                        Date updatedAt = lhs.getUpdatedAt();
                                        Date updatedAt1 = rhs.getUpdatedAt();
                                        if (updatedAt.before(updatedAt1)) {
                                            return 0;
                                        } else {
                                            return 1;
                                        }
                                    }
                                });
                                mTvNo.setVisibility(View.GONE);
                            }
                            mMyAdapter.notifyDataSetChanged();
                        }
                    } else {
                        mTvNo.setText("获取数据失败");
                        mTvNo.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTvNo.setText("网络未连接");
            mTvNo.setVisibility(View.VISIBLE);
        }

    }

    private void initView() {
        mDate = new Date();
        mTimeAll = MyUtills.getTimeAll(mDate);
        String substring = mTimeAll.substring(0, 4);
        mTvTimeYear.setText(substring + "年");
        String substring1 = mTimeAll.substring(6, 8);
        mTvTimeMonth.setText(substring1 + "月");
        mTvScores.setText(mCurrentUser.getNumber("score") + "");
    }

    int MIN_CLICK_DELAY_TIME = 1000;
    long lastClickTime = 0;

    @OnClick({R.id.iv_wirte_back, R.id.tv_shoutian, R.id.rl_time})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.iv_wirte_back:
                    finish();
                    break;
                case R.id.tv_shoutian:
                    break;
                case R.id.rl_time:
                    break;
            }
        }
    }

    private int getSize(int i) {
        if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 | i == 12) {
            return 31;
        } else if (i == 2) {
            return 28;
        } else {
            return 20;
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            String s = mTvTimeMonth.getText().toString();
            String substring = s.substring(0, 2);
            return getSize(Integer.parseInt(substring));
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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_profit_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String year = mTvTimeYear.getText().toString().substring(0, 4);
            String month = mTvTimeMonth.getText().toString().substring(0, 2);
            String day = "";
            int p = position + 1;
            if (position < 9) {
                day = "0" + p;
            } else {
                day = p + "";
            }
            List<AVObject> dayList = new ArrayList<>();
            String date = year + "-" + month + "-" + day;
            String week = DateUtils.getWeek(date);
            viewHolder.mTvTime.setText(p + "日(" + week + ")");
            for (AVObject avObject : mStatics) {
                Date updatedAt = avObject.getUpdatedAt();
                String timeAll = MyUtills.getTimeAll(updatedAt);
                String days = timeAll.substring(8, 10);
                if (day.equals(days)) {
                    dayList.add(avObject);
                }
            }

            if (dayList.size() == 0) {
                viewHolder.mLinearLayout.setVisibility(View.GONE);
            } else {
                InnerAdapter innerAdapter = new InnerAdapter(dayList);
                viewHolder.mLvProfit.setAdapter(innerAdapter);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.ll_item)
        LinearLayout mLinearLayout;
        @Bind(R.id.lv_profit)
        SyListView mLvProfit;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class InnerAdapter extends BaseAdapter {
        private List<AVObject> mArrayList;

        InnerAdapter(List<AVObject> list) {
            this.mArrayList = list;
        }

        @Override
        public int getCount() {
            return mArrayList.size();
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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_inner_profit_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            AVObject avObject = mArrayList.get(0);
            viewHolder.mTvHao.setText(avObject.getObjectId());
            viewHolder.mTvScore.setText(avObject.getNumber("price") + "积分");
            String timeAll = MyUtills.getTimeAll(avObject.getUpdatedAt());
            String substring = timeAll.substring(5);
            viewHolder.mTvTime.setText(substring + "完成");

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tv_hao)
            TextView mTvHao;
            @Bind(R.id.tv_time)
            TextView mTvTime;
            @Bind(R.id.tv_score)
            TextView mTvScore;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
