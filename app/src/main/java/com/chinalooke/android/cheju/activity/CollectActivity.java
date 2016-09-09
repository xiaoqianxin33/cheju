package com.chinalooke.android.cheju.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CollectActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    @Bind(R.id.tv_none)
    TextView mTvNone;
    @Bind(R.id.pb_order)
    ProgressBar mPbOrder;
    @Bind(R.id.fl)
    FrameLayout mFl;
    @Bind(R.id.listview)
    ListView mListview;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    private AVUser mCurrentUser;
    private int mSkip;
    private List<AVObject> mShops = new ArrayList<>();
    private MyAdapt mMyAdapt;
    private boolean isLoading = false;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        mMyAdapt = new MyAdapt();
        mListview.setAdapter(mMyAdapt);
        mCurrentUser = AVUser.getCurrentUser();
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
                refresh();
                mSr.setRefreshing(false);
            }
        });

        mListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mListview != null && mListview.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    mSr.setEnabled(enable);
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                    loadMore();
                }
            }
        });

        mListview.setOnItemClickListener(this);
    }

    private void refresh() {
        mSkip = 0;
        mShops.clear();
        initData();
    }


    private void loadMore() {
        isLoading = true;
        initData();
    }

    @OnClick({R.id.iv_wirte_back})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_wirte_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AVObject avObject = mShops.get(position);
        Intent intent = new Intent(CollectActivity.this, ShopActivity.class);
        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;
        intent.putExtra(Constant.START_LOCATION, startingLocation);
        Bundle bundle = new Bundle();
        bundle.putParcelable("shop", avObject);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return mShops.size();
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
                convertView = View.inflate(getApplicationContext(), R.layout.item_collect_shop_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            AVObject avObject = mShops.get(position);
            viewHolder.mTvShopName.setText(avObject.getString("ShopName"));
            viewHolder.mTvDescript.setText(avObject.getString("descript"));
            viewHolder.mTvPrice.setText(avObject.getString("ShopAddress"));
            AVFile images = avObject.getAVFile("images");
            Picasso.with(getApplicationContext()).load(images.getUrl()).resize(
                    MyUtills.Dp2Px(getApplicationContext(), 50), MyUtills.Dp2Px(getApplicationContext(), 50)
            ).centerCrop().into(viewHolder.mIvShop);
            return convertView;
        }

    }

    static class ViewHolder {
        @Bind(R.id.tv_shop_name)
        TextView mTvShopName;
        @Bind(R.id.tv_descript_collect)
        TextView mTvDescript;
        @Bind(R.id.tv_price_collect)
        TextView mTvPrice;
        @Bind(R.id.iv_shop)
        ImageView mIvShop;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void initData() {

        if (NetUtil.is_Network_Available(getApplicationContext())) {
            mPbOrder.setVisibility(View.GONE);
            AVObject todoFolder = AVObject.createWithoutData("_User", mCurrentUser.getObjectId());
            AVRelation<AVObject> relation = todoFolder.getRelation("collectShop");
            AVQuery<AVObject> query = relation.getQuery();
            query.limit(10);
            query.skip(mSkip);
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (mPbOrder != null)
                        mPbOrder.setVisibility(View.GONE);
                    if (e == null) {
                        if (list.size() != 0) {
                            mFl.setVisibility(View.GONE);
                            mShops.addAll(list);
                            mSkip += 10;
                            isLoading = false;
                        }
                    } else {
                        if (mSkip == 0)
                            mTvNone.setVisibility(View.VISIBLE);
                    }
                    mMyAdapt.notifyDataSetChanged();
                    if (!isFirst && mShops.size() == 0) {
                        mFl.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            mPbOrder.setVisibility(View.GONE);
            mTvNone.setVisibility(View.VISIBLE);
            mTvNone.setText("网络不可用，无法获取数据");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFirst = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst) {
            mCurrentUser.fetchIfNeededInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    refresh();
                }
            });
        }
    }
}
