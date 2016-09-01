package com.chinalooke.android.cheju.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.MainActivity;
import com.chinalooke.android.cheju.activity.TakePhotoActivity;
import com.chinalooke.android.cheju.activity.WriteMessgeActivity;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.constant.SQLwords;
import com.chinalooke.android.cheju.utills.PreferenceUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiao on 2016/8/6.
 */
public class OrderFragment extends Fragment implements AdapterView.OnItemClickListener {


    @Bind(R.id.tv_order_chexian)
    TextView mTvOrderChexian;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    @Bind(R.id.lv_chexian_order)
    ListView mLvChexianOrder;
    @Bind(R.id.tv_order_youhuijua)
    TextView mTvOrderYouhuijua;
    @Bind(R.id.tv_nopolicy)
    TextView mTvNopolicy;
    private View mView;
    private int currentFragment;
    private Policy mPolicy;
    private MyOrderAdapt mMyOrderAdapt;
    private AVObject mAvObject;
    private ProgressDialog mProgressDialog;
    private boolean isLoading = false;
    private boolean isPrepared = false;
    private boolean isDone = false;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (1 == msg.what) {
                setDtail();
                mMyOrderAdapt = new MyOrderAdapt();
                setAdapt(0);
                mProgressDialog.dismiss();
                isDone = true;
            }
        }
    };
    private boolean isVisible;
    private AVUser mCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, mView);
        mPolicy = new Policy();
        isPrepared = true;
        mCurrentUser = ((MainActivity) getActivity()).getCurrentUser();
        lazyLoad();
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void initDialog() {
        if (mCurrentUser != null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("数据加载中");
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
            mTvNopolicy.setVisibility(View.GONE);
        } else {
            mTvNopolicy.setVisibility(View.VISIBLE);
        }

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
                initChexianData();
                mMyOrderAdapt.notifyDataSetChanged();
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
                    loadMore(currentFragment);
                }
            }
        });
    }

    private void initChexianData() {

        if (mCurrentUser != null) {
            AVQuery.doCloudQueryInBackground(SQLwords.requirPolicy, new CloudQueryCallback<AVCloudQueryResult>() {
                @Override
                public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                    if (e == null) {
                        List<AVObject> results = (List<AVObject>) avCloudQueryResult.getResults();
                        if (results == null || results.size() == 0) {
                            mProgressDialog.dismiss();
                            mTvNopolicy.setVisibility(View.VISIBLE);
                        } else {
                            mTvNopolicy.setVisibility(View.GONE);
                            mAvObject = results.get(0);
                            mHandler.sendEmptyMessage(1);
                        }
                    }
                }
            }, AVUser.getCurrentUser().getUsername());
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private void setAdapt(int i) {
        switch (i) {
            case 0:
                currentFragment = 0;
                mLvChexianOrder.setAdapter(mMyOrderAdapt);
                break;
            case 1:
                currentFragment = 1;
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String status = mPolicy.getStatus();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        if ("待算价".equals(status)) {
            intent.setClass(getActivity(), WriteMessgeActivity.class);
            if (currentFragment == 0)
                bundle.putSerializable("dpolicy", mPolicy);
        } else {
            intent.setClass(getActivity(), TakePhotoActivity.class);
            if (currentFragment == 0)
                bundle.putSerializable("policy", mPolicy);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick({R.id.tv_order_chexian, R.id.tv_order_youhuijua})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_order_chexian:
                mTvOrderChexian.setTextColor(getResources().getColor(R.color.selectcolor));
                mTvOrderYouhuijua.setTextColor(getResources().getColor(R.color.unselectcolor));
                setAdapt(currentFragment);
                break;
            case R.id.tv_order_youhuijua:
                mTvOrderChexian.setTextColor(getResources().getColor(R.color.unselectcolor));
                mTvOrderYouhuijua.setTextColor(getResources().getColor(R.color.selectcolor));
                break;
        }
    }


    class MyOrderAdapt extends BaseAdapter {
        @Override
        public int getCount() {
            return 1;
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_order_chexian_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            setOrderDatails(viewHolder);
            return convertView;
        }

    }

    class ViewHolder {
        @Bind(R.id.iv_order_chexian_listview)
        ImageView mIvOrderChexianListview;
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


    public void setOrderDatails(ViewHolder viewHolder) {

        viewHolder.mTvDateOrder.setText(mPolicy.getRegDate());
        viewHolder.mTvStatuOrder.setText(mPolicy.getStatus());
        viewHolder.mTvCompanyOrder.setText(mPolicy.getCompany());

        if (TextUtils.isEmpty(mPolicy.getPrice())) {
            viewHolder.mTvPriceOrderListview.setText("算价中");
        } else {
            viewHolder.mTvPriceOrderListview.setText(mPolicy.getPrice());
        }

        switch (mPolicy.getStatus()) {
            case "待算价":

                viewHolder.mIvOrderChexianListview.setBackgroundResource(R.mipmap.dd_sjz);

                break;
            case "已算价":

                viewHolder.mIvOrderChexianListview.setBackgroundResource(R.mipmap.dd_ycj);

                break;
            case "待审核":

                viewHolder.mIvOrderChexianListview.setBackgroundResource(R.mipmap.dd_yzf);

                break;
            case "已完成":

                viewHolder.mIvOrderChexianListview.setBackgroundResource(R.mipmap.dd_ywc);

                break;

        }
    }

    private void setDtail() {
        if (mAvObject != null) {
            mPolicy.setObjectId(mAvObject.getObjectId());
            mPolicy.setPolicyDate(mAvObject.getString("policyDate"));
            mPolicy.setCompany(mAvObject.getString("company"));
            mPolicy.setStatus(mAvObject.getString("status"));
            mPolicy.setPrice(mAvObject.getString("price"));
            mPolicy.setRegDate(mAvObject.getString("regDate"));
            mPolicy.setForceimgs(mAvObject.getString("forceimgs"));
            mPolicy.setBusinessimage(mAvObject.getString("businessimage"));
            mPolicy.setDetail(mAvObject.getString("detail"));
            mPolicy.setDiscountPrice(mAvObject.getString("discountPrice"));
            mPolicy.setCarNo(mAvObject.getString("CarNo"));
            mPolicy.setCity(mAvObject.getString("city"));
            mPolicy.setBrand(mAvObject.getString("brand"));
            mPolicy.setIdNo(mAvObject.getString("IdNo"));
            mPolicy.setEngine(mAvObject.getString("engine"));
            mPolicy.setFrameNo(mAvObject.getString("frameNo"));
            mPolicy.setUserName(mAvObject.getString("userName"));
            mPolicy.setType(mAvObject.getString("type"));
            mPolicy.setPhone(mAvObject.getString("phone"));
        }
    }

    private void loadMore(int currentFragment) {
        isLoading = true;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if (isVisibleToUser) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
        }


    }

    private void onVisible() {
        lazyLoad();
    }

    private void lazyLoad() {
        if (!isPrepared || !isVisible)
            return;
        initDialog();
        initChexianData();
        initEvent();
    }
}