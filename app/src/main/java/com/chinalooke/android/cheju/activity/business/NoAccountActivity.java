package com.chinalooke.android.cheju.activity.business;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.config.MyLeanCloudApp;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoAccountActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.lv_no_accont)
    ListView mLvNoAccont;
    @Bind(R.id.tv_no)
    TextView mTvNo;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    private int mSkip;
    private boolean isLoading = false;
    private AVObject mShop;
    private boolean isFirst = true;
    private List<AVObject> mOrders = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_account);
        ButterKnife.bind(this);
        mShop = MyLeanCloudApp.getAVObject();
        initEvent();
    }

    private void initEvent() {
        mSr.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSkip = 0;
                mOrders.clear();
                initData();
                mSr.setRefreshing(false);
            }
        });

        mLvNoAccont.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLvNoAccont != null && mLvNoAccont.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    mSr.setEnabled(enable);
                }
                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                    loadMore();
                }
            }
        });

        mLvNoAccont.setOnItemClickListener(this);
    }

    private void loadMore() {
        isLoading = true;
        initData();
    }

    private void initData() {
        AVQuery<AVObject> query = new AVQuery<>("Order");
        query.whereEqualTo("shopId", mShop.getObjectId());
        query.whereEqualTo("status", "2");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600); //设置缓存有效期
        query.skip(mSkip);
        query.limit(10);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                mProgressBar.setVisibility(View.GONE);
                if (e == null) {
                    if ((list == null || list.size() == 0) && isFirst) {
                        mTvNo.setText("暂无未结算订单");
                        mTvNo.setVisibility(View.VISIBLE);
                    } else if (list != null) {
                        mOrders.addAll(list);
                        mSkip += 10;
                        isFirst = false;
                    }
                } else {
                    if (NetUtil.is_Network_Available(getApplicationContext())) {
                        mTvNo.setText("网络未连接，请检查网络连接");
                        mTvNo.setVisibility(View.VISIBLE);
                    } else {
                        mTvNo.setText("获取数据出错");
                        mTvNo.setVisibility(View.VISIBLE);
                    }
                }
                isLoading = false;
            }
        });
    }

    @OnClick(R.id.iv_wirte_back)
    public void onClick() {
        finish();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mOrders.size();
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
                convertView = View.inflate(NoAccountActivity.this, R.layout.item_noaccount_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            AVObject order = mOrders.get(position);
            viewHolder.mTvTime.setText(MyUtills.getTimeAll(order.getCreatedAt()));
            viewHolder.mTvScore.setText(order.getString("price"));
            viewHolder.mTvHao.setText(order.getObjectId());
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tv_hao)
            TextView mTvHao;
            @Bind(R.id.tv_score)
            TextView mTvScore;
            @Bind(R.id.tv_time)
            TextView mTvTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
