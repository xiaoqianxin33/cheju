package com.chinalooke.android.cheju.activity.business;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.config.MyLeanCloudApp;
import com.chinalooke.android.cheju.utills.MyUtills;

import java.util.Calendar;
import java.util.Date;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);
        ButterKnife.bind(this);
        mCurrentUser = AVUser.getCurrentUser();
        initView();

    }

    private void initView() {
        Date date = new Date();
        String timeAll = MyUtills.getTimeAll(date);
        String substring = timeAll.substring(0, 4);
        mTvTimeYear.setText(substring + "年");
        String substring1 = timeAll.substring(6, 8);
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
}
