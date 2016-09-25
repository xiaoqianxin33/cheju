package com.chinalooke.android.cheju.activity.business;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.ShopActivity;
import com.chinalooke.android.cheju.config.MyLeanCloudApp;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.google.android.gms.internal.o;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyGoodsActivity extends AppCompatActivity {

    @Bind(R.id.lv_goods)
    ListView mLvGoods;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    @Bind(R.id.tv_no)
    TextView mTvNo;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    private AVUser mCurrentUser;
    private AVObject mAvObject;
    private boolean isFirst = true;
    private List<AVObject> mGoods = new ArrayList<>();
    private boolean isLoading = false;
    private int mSkip;
    private MyAdapter mMyAdapter;
    private ProgressDialog mProgressDialog;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_goods);
        ButterKnife.bind(this);
        mCurrentUser = AVUser.getCurrentUser();
        mToast = MyLeanCloudApp.getToast();
        mAvObject = getIntent().getParcelableExtra("shop");
        mMyAdapter = new MyAdapter();
        mLvGoods.setAdapter(mMyAdapter);
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
                mGoods.clear();
                initData();
                mSr.setRefreshing(false);
            }
        });

        mLvGoods.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLvGoods != null && mLvGoods.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    mSr.setEnabled(enable);
                }
                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        isLoading = true;
        initData();
    }

    private void initData() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {

            AVRelation<AVObject> goods = mAvObject.getRelation("goods");
            AVQuery<AVObject> query = goods.getQuery();
            query.limit(8);
            query.skip(mSkip);
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    mProgressBar.setVisibility(View.GONE);
                    if (e == null) {
                        mGoods.addAll(list);
                        mMyAdapter.notifyDataSetChanged();
                        isLoading = true;
                        mSkip += 8;
                    } else {
                        if (isFirst) {
                            mTvNo.setText("暂无商品");
                            mTvNo.setVisibility(View.VISIBLE);
                        }
                    }
                    isFirst = false;
                }
            });
        } else {
            mProgressBar.setVisibility(View.GONE);
            if (isFirst) {
                mTvNo.setText("网络不可用，请检查网络连接");
                mTvNo.setVisibility(View.VISIBLE);
            }

        }
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mGoods.size();
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
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_goods_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            setDetails(position, viewHolder);
            return convertView;
        }

    }

    private void setDetails(int position, final ViewHolder viewHolder) {
        final AVObject avObject = mGoods.get(position);
        AVRelation<AVObject> images = avObject.getRelation("images");
        AVQuery<AVObject> query = images.getQuery();
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        AVObject avObject1 = list.get(0);
                        String url = avObject1.getString("url");
                        Picasso.with(MyGoodsActivity.this).load(url).resize(
                                MyUtills.Dp2Px(getApplicationContext(), 100), MyUtills.Dp2Px(getApplicationContext(), 100)
                        ).centerCrop().placeholder(R.mipmap.placeholder).into(viewHolder.mIvGods);
                    }
                }
            }
        });

        String goodType = avObject.getString("GoodType");
        AVQuery<AVObject> query1 = new AVQuery<>("GoodsType");
        query1.whereEqualTo("objectId", goodType);
        query1.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    AVObject avObject1 = list.get(0);
                    Log.e("TBB", list.size() + "");
                    viewHolder.mTvFenlei.setText(avObject1.getString("name"));
                }
            }
        });

        viewHolder.mTvGodsTitle.setText(avObject.getString("name"));
        Date createdAt = avObject.getDate("createdAt");
        String timeAll = MyUtills.getTimeAll(createdAt);
        viewHolder.mTvTime.setText(timeAll);
        viewHolder.mTvScore.setText(avObject.getString("score") + "积分");
        viewHolder.mBtnShanchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtills.showSingerDialog(MyGoodsActivity.this, "提示", "确定删除此商品吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mProgressDialog = MyUtills.initDialog("", MyGoodsActivity.this);
                        mProgressDialog.show();
                        avObject.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                mProgressDialog.dismiss();
                                if (e == null) {
                                    mToast.setText("删除成功！");
                                    mToast.show();
                                    initData();
                                } else {
                                    mToast.setText("删除失败");
                                    mToast.show();
                                }
                            }
                        });

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    static class ViewHolder {
        @Bind(R.id.iv_gods)
        RoundedImageView mIvGods;
        @Bind(R.id.tv_gods_title)
        TextView mTvGodsTitle;
        @Bind(R.id.tv_fenlei)
        TextView mTvFenlei;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_score)
        TextView mTvScore;
        @Bind(R.id.btn_xiajia)
        Button mBtnXiajia;
        @Bind(R.id.btn_shanchu)
        Button mBtnShanchu;
        @Bind(R.id.btn_bianji)
        Button mBtnBianji;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @OnClick({R.id.iv_wirte_back, R.id.release})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wirte_back:
                finish();
                break;
            case R.id.release:
                Number type = mAvObject.getNumber("type");
                int i = type.intValue();
                Intent intent = new Intent(MyGoodsActivity.this, ReleaseGoodsActivity.class);
                intent.putExtra("shopType", i);
                startActivity(intent);
                break;
        }
    }
}
