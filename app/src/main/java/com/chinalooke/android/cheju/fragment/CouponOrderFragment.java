package com.chinalooke.android.cheju.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.QRCodeActivity;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.utills.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CouponOrderFragment extends Fragment implements AdapterView.OnItemClickListener {


    @Bind(R.id.tv_nopolicy)
    TextView mTvNopolicy;
    @Bind(R.id.lv_chexian_order)
    ListView mLvChexianOrder;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    @Bind(R.id.pb_order)
    ProgressBar mPbOrder;
    private AVUser mCurrentUser;
    private int mSkip;
    private MyCouponAdapt mMyOrderAdapt;
    private boolean isLoading = false;
    private AVObject mAvObject;
    private Policy mPolicy;
    private AVObject mOrder;
    private Toast mToast;
    private List<AVObject> mCoupons = new ArrayList<>();
    private boolean isFirst = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupon_order, container, false);
        ButterKnife.bind(this, view);
        mCurrentUser = AVUser.getCurrentUser();
        mMyOrderAdapt = new MyCouponAdapt();
        mLvChexianOrder.setAdapter(mMyOrderAdapt);
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
    }

    private void initEvent() {
        mLvChexianOrder.setOnItemClickListener(this);
        mSr.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSkip = 0;
                mCoupons.clear();
                initData();
                mSr.setRefreshing(false);
            }
        });

        mLvChexianOrder.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLvChexianOrder != null && mLvChexianOrder.getChildCount() > 0) {
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
        initData();
        isLoading = true;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!isFirst) {
            initData();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        isFirst = false;
    }


    private void initData() {
        if (mCurrentUser != null) {
            if (NetUtil.is_Network_Available(getActivity())) {
                AVQuery<AVObject> query = new AVQuery<>("Order");
                query.whereEqualTo("userId", mCurrentUser.getObjectId());
                query.limit(10);
                query.skip(mSkip);
                query.whereEqualTo("type", "1");
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        mPbOrder.setVisibility(View.GONE);
                        if (e == null) {
                            if (list.size() != 0) {
                                mCoupons.addAll(list);
                                mTvNopolicy.setVisibility(View.GONE);
                                if (mMyOrderAdapt != null)
                                    mMyOrderAdapt.notifyDataSetChanged();
                                mSkip += 10;
                                isLoading = false;
                            } else {
                                if (mSkip == 0)
                                    mTvNopolicy.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

            } else {
                mPbOrder.setVisibility(View.GONE);
                mTvNopolicy.setText("网络未连接");
                mTvNopolicy.setVisibility(View.VISIBLE);
            }
        } else {
            mPbOrder.setVisibility(View.GONE);
            mTvNopolicy.setText("请登录");
            mTvNopolicy.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AVObject avObject = mCoupons.get(position);
        Intent intent = new Intent(getActivity(), QRCodeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("order", avObject);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    class MyCouponAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return mCoupons.size();
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
                convertView = View.inflate(getActivity(), R.layout.item_order_coupon_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            setCouponDetail(viewHolder, position);
            return convertView;
        }


    }

    static class ViewHolder {
        @Bind(R.id.tv_company_order)
        TextView mTvCompanyOrder;
        @Bind(R.id.tv_date_order)
        TextView mTvDateOrder;
        @Bind(R.id.tv_statu_order)
        TextView mTvStatuOrder;
        @Bind(R.id.tv_price_order_listview)
        TextView mTvPriceOrderListview;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void setCouponDetail(final ViewHolder viewHolder2, int positon) {
        AVObject avObject = mCoupons.get(positon);
        viewHolder2.mTvDateOrder.setText(StringUtils.getTime(avObject.getDate("addDate")));
        viewHolder2.mTvPriceOrderListview.setText(avObject.getString("price"));
        viewHolder2.mTvStatuOrder.setText(avObject.getNumber("count") + "");
        String goodsId = avObject.getString("goodsId");
        AVQuery<AVObject> query = new AVQuery<>("Goods");
        query.whereEqualTo("objectId", goodsId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null && list.size() != 0) {
                    AVObject avObject1 = list.get(0);
                    viewHolder2.mTvCompanyOrder.setText(avObject1.getString("name"));
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
