package com.chinalooke.android.cheju.fragment;


import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.OrderActivity;
import com.chinalooke.android.cheju.activity.TakePhotoActivity;
import com.chinalooke.android.cheju.activity.WriteMessgeActivity;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.utills.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarOrderFragment extends Fragment implements AdapterView.OnItemClickListener {


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
    private List<AVObject> mPolicys = new ArrayList<>();
    private MyOrderAdapt mMyOrderAdapt;
    private boolean isLoading = false;
    private AVObject mAvObject;
    private ProgressDialog mDialog;
    private Policy mPolicy;
    private AVObject mOrder;
    private Toast mToast;

    public CarOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_order, container, false);
        ButterKnife.bind(this, view);
        mCurrentUser = AVUser.getCurrentUser();
        mMyOrderAdapt = new MyOrderAdapt();
        mLvChexianOrder.setAdapter(mMyOrderAdapt);
        mPolicy = new Policy();
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
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
                initData();
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
                    loadMore();
                }
            }
        });
    }


    private void loadMore() {
        if (mSkip != 0) {
            initData();
        }
        isLoading = true;
    }


    private void initData() {
        if (mCurrentUser != null) {
            if (NetUtil.is_Network_Available(getActivity())) {

                AVRelation<AVObject> relation = mCurrentUser.getRelation("policy");
                AVQuery<AVObject> query = relation.getQuery();
                query.limit(10);
                query.skip(mSkip);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        mPbOrder.setVisibility(View.GONE);
                        if (e == null) {
                            if (list == null || list.size() == 0) {
                                if (mSkip == 0)
                                    mTvNopolicy.setVisibility(View.VISIBLE);
                            } else {
                                mPolicys = list;
                                mTvNopolicy.setVisibility(View.GONE);
                                if (mMyOrderAdapt != null)
                                    mMyOrderAdapt.notifyDataSetChanged();
                                mSkip += 10;
                                isLoading = false;
                                mPbOrder.setVisibility(View.GONE);

                            }
                        } else {
                            mTvNopolicy.setVisibility(View.VISIBLE);
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
        mAvObject = mPolicys.get(position);
        int status = mAvObject.getInt("status");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        setDtail();
        if (status == 0) {
            intent.setClass(getActivity(), WriteMessgeActivity.class);
            bundle.putSerializable("dpolicy", mPolicy);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (status == 1) {
            intent.setClass(getActivity(), TakePhotoActivity.class);

            bundle.putSerializable("policy", mPolicy);
            bundle.putParcelable("avobject", mAvObject);
            intent.putExtras(bundle);
            startActivity(intent);

        } else if (status == 2 || status == 3) {
            mDialog = MyUtills.initDialog("加载中", getActivity());
            mDialog.show();
            bundle.putSerializable("policy", mPolicy);
            getOrder(bundle, intent);
        }

    }

    private void getOrder(final Bundle bundle, final Intent intent) {
        AVQuery<AVObject> query = new AVQuery<>("Order");
        query.whereEqualTo("policyId", mAvObject.getObjectId());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                mDialog.dismiss();
                if (e == null) {
                    mOrder = list.get(0);
                    bundle.putParcelable("order", mOrder);
                    intent.putExtras(bundle);
                    intent.setClass(getActivity(), OrderActivity.class);
                    startActivity(intent);

                } else {
                    mToast.setText("加载失败，请重试");
                    mToast.show();
                }
            }
        });
    }

    private void setDtail() {
        if (mAvObject != null) {
            mPolicy.setObjectId(mAvObject.getObjectId());
            mPolicy.setCompany(mAvObject.getString("company"));
            mPolicy.setStatus(mAvObject.getInt("status"));
            mPolicy.setPrice(mAvObject.getNumber("price") + "");
            mPolicy.setRegDate(mAvObject.getDate("regDate"));
            mPolicy.setDetail(mAvObject.getString("detail"));
            mPolicy.setDiscountPrice(mAvObject.getNumber("discountPrice") + "");
            mPolicy.setCarNo(mAvObject.getString("carNo"));
            mPolicy.setCity(mAvObject.getString("city"));
            mPolicy.setBrand(mAvObject.getString("brand"));
            mPolicy.setIdNo(mAvObject.getString("IDNo"));
            mPolicy.setEngine(mAvObject.getString("engine"));
            mPolicy.setFrameNo(mAvObject.getString("frameNo"));
            mPolicy.setUserName(mAvObject.getString("userName"));
            mPolicy.setType(mAvObject.getInt("type"));
            mPolicy.setPhone(mAvObject.getString("phone"));
        }
    }

    class MyOrderAdapt extends BaseAdapter {
        @Override
        public int getCount() {
            return mPolicys.size();
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

            setOrderDatails(viewHolder, position);
            return convertView;
        }

    }

    public void setOrderDatails(final ViewHolder viewHolder, final int positon) {
        final AVObject avObject = mPolicys.get(positon);
        viewHolder.mTvDateOrder.setText(StringUtils.getTime(avObject.getDate("regDate")));
        int statu = avObject.getInt("status");
        switch (statu) {
            case 0:
                viewHolder.mTvStatuOrder.setText("待算价");
                break;
            case 1:
                viewHolder.mTvStatuOrder.setText("已算价");
                break;
            case 2:
                viewHolder.mTvStatuOrder.setText("待审核");
                break;
            case 3:
                viewHolder.mTvStatuOrder.setText("已完成");
                break;
        }

        viewHolder.mTvCompanyOrder.setText(avObject.getString("company"));
        Number price = avObject.getNumber("price");

        if (price == 0) {
            viewHolder.mTvPriceOrderListview.setText("");
        } else {
            viewHolder.mTvPriceOrderListview.setText(price + "");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
