package com.chinalooke.android.cheju.activity.business;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.bigkoo.pickerview.TimePickerView;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.config.MyLeanCloudApp;
import com.chinalooke.android.cheju.utills.DateUtils;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;

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

    private AVUser mCurrentUser;
    private AVObject mShop;
    private List<AVObject> mStatics = new ArrayList<>();
    private MyAdapter mMyAdapter;
    private Date mDate;
    private Calendar mCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);
        ButterKnife.bind(this);
        mCurrentUser = AVUser.getCurrentUser();
        mCal = Calendar.getInstance();
        mShop = MyLeanCloudApp.getAVObject();
        mDate = new Date();
        initView();
        mMyAdapter = new MyAdapter();
        mLvGoods.setAdapter(mMyAdapter);
        initData();
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
                                    if (mDate.getMonth() == updatedAt.getMonth() && mDate.getYear() == updatedAt.getYear()) {
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
                        }
                    } else {
                        mTvNo.setText("获取数据失败");
                        mTvNo.setVisibility(View.VISIBLE);
                    }
                    mMyAdapter.notifyDataSetChanged();
                }
            });
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTvNo.setText("网络未连接");
            mTvNo.setVisibility(View.VISIBLE);
        }

    }


    private void initView() {
        String timeAll = MyUtills.getTimeAll(mDate);
        int month = mDate.getMonth();
        String substring = timeAll.substring(0, 4);
        mTvTimeYear.setText(substring + "年");
        mTvTimeMonth.setText(++month + "月");
        mTvScores.setText(mCurrentUser.getNumber("score") + "");
    }

    int MIN_CLICK_DELAY_TIME = 1000;
    long lastClickTime = 0;

    @OnClick({R.id.iv_wirte_back, R.id.tv_shoutian, R.id.rl_time, R.id.tv_nojiesuan})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.tv_nojiesuan:
                    startActivity(new Intent(ProfitActivity.this, NoAccountActivity.class));
                    break;
                case R.id.iv_wirte_back:
                    finish();
                    break;
                case R.id.tv_shoutian:
                    startActivity(new Intent(ProfitActivity.this, AccountsActivity.class));
                    break;
                case R.id.rl_time:
                    showOption();
                    break;
            }
        }
    }

    private void showOption() {
        TimePickerView timePickerView = new TimePickerView(ProfitActivity.this, TimePickerView.Type.YEAR_MONTH);
        timePickerView.setTime(new Date());
        timePickerView.setCyclic(false);
        timePickerView.setCancelable(true);

        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                mDate = date;
                mStatics.clear();
                mMyAdapter.clear();
                initView();
                initData();
            }
        });
        timePickerView.show();
    }

    class MyAdapter extends BaseAdapter {
        private List<String> mLists = new ArrayList<>();

        public void clear() {
            mLists.clear();
        }

        @Override
        public int getCount() {
            return mStatics.size();
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
            AVObject avObject = mStatics.get(position);
            Date updatedAt = avObject.getUpdatedAt();
            mCal.setTime(updatedAt);
            int day = mCal.get(Calendar.DATE);
            ViewHolder viewHolder;
            ViewHolder2 viewHolder2;
            if (mLists.contains(day + "")) {
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.item_inner_profit_listview, null);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.mTvTime.setText(day + "日" + updatedAt.getHours() + ":" + updatedAt
                        .getMinutes() + "完成");
                viewHolder.mTvHao.setText(avObject.getObjectId());
                viewHolder.mTvScore.setText(avObject.getString("price") + "积分");
            } else {
                if (convertView == null) {
                    mLists.add(day + "");
                    convertView = View.inflate(getApplicationContext(), R.layout.item_profit_listview, null);
                    viewHolder2 = new ViewHolder2(convertView);
                    convertView.setTag(viewHolder2);
                } else {
                    viewHolder2 = (ViewHolder2) convertView.getTag();
                }
                String date = mDate.getYear() + "-" + mDate.getMonth() + "-" + day;
                String week = DateUtils.getWeek(date);
                viewHolder2.mTvTime.setText(day + "日(" + week + ")");
                viewHolder2.mTvTimeIner.setText(updatedAt.getDay() + "日" + updatedAt.getHours() + ":" + updatedAt
                        .getMinutes() + "完成");
                viewHolder2.mTvHao.setText(avObject.getObjectId());
                viewHolder2.mTvScore.setText(avObject.getString("price") + "积分");
            }

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

        class ViewHolder2 {
            @Bind(R.id.tv_time)
            TextView mTvTime;
            @Bind(R.id.tv_hao)
            TextView mTvHao;
            @Bind(R.id.tv_time_iner)
            TextView mTvTimeIner;
            @Bind(R.id.tv_score)
            TextView mTvScore;

            ViewHolder2(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }


}
