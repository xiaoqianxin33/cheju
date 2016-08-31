package com.chinalooke.android.cheju.fragment.salesman;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.salesman.PolicyCalculateActivity;
import com.chinalooke.android.cheju.activity.salesman.PolicyCheckActivity;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.fragment.BaseFragment;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.UIutils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PolicyFragment extends BaseFragment implements AdapterView.OnItemClickListener {


    @Bind(R.id.lv_customer_lv)
    ListView mLvCustomer;
    @Bind(R.id.sr_customer)
    SwipeRefreshLayout mSrCustomer;
    private boolean isPrepared = false;
    private ProgressDialog mProgressDialog;
    private List<AVUser> mAVUsers = new ArrayList<>();
    private ArrayList<AVObject> mPolicys = new ArrayList<>();


    private boolean isLoading = false;
    private MyAdapt mMyAdapt;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    mProgressDialog.dismiss();
                    mMyAdapt = new MyAdapt();
                    mLvCustomer.setAdapter(mMyAdapt);
                    mHandler.sendEmptyMessage(3);
                    break;

                case 2:
                    initPolicyData();
                    break;

                case 3:
                    mSrCustomer.setRefreshing(false);
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        ButterKnife.bind(this, view);
        isPrepared = true;
        lazyLoad();
        return view;
    }


    private void initDialog() {
        mProgressDialog = UIutils.initLoadDialog(getActivity());
        mProgressDialog.show();
    }


    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible)
            return;
        initDialog();
        initCustomerData();
        initEvent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initPolicyData() {
        final int[] i = {0};

        for (AVUser avUser : mAVUsers) {
            AVQuery<AVObject> query = new AVQuery<>("Policy");

            query.whereEqualTo("phone", avUser.getString("mobilePhoneNumber"));

            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        mPolicys.add(list.get(0));
                        i[0]++;
                        if (i[0] == mAVUsers.size()) {

                            Collections.sort(mPolicys, new Comparator<AVObject>() {
                                @Override
                                public int compare(AVObject lhs, AVObject rhs) {
                                    try {
                                        Date lhsTime = MyUtills.ConverToDate(lhs.getString("regDate"));
                                        Date rhsTime = MyUtills.ConverToDate(rhs.getString("regDate"));

                                        if (lhsTime.before(rhsTime)) {
                                            return 1;
                                        } else {
                                            return -1;
                                        }
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                    return 0;
                                }
                            });
                            mHandler.sendEmptyMessage(1);
                        }
                    }
                }
            });
        }
    }


    private void initCustomerData() {
        AVQuery<AVUser> userQuery = new AVQuery<>("_User");

        userQuery.whereEqualTo("referrer", AVUser.getCurrentUser().getMobilePhoneNumber());

        userQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (e == null)
                    mAVUsers = list;

                if (list == null || list.size() == 0)
                    mProgressDialog.dismiss();

                mHandler.sendEmptyMessage(2);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Policy policy = setMssage(position);
        String status = policy.getStatus();
        Intent intent = new Intent();
        if ("未算价".equals(status) || "已算价".equals(status))
            intent.setClass(getActivity(), PolicyCalculateActivity.class);
        if ("待审核".equals(status) || "已完成".equals(status))
            intent.setClass(getActivity(), PolicyCheckActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("policy", policy);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private Policy setMssage(int position) {
        AVObject avObject = mPolicys.get(position);
        Policy policy = new Policy();
        policy.setCity(avObject.getString("city"));
        policy.setPhone(avObject.getString("phone"));
        policy.setIdNo(avObject.getString("IdNo"));
        policy.setCarNo(avObject.getString("CarNo"));
        policy.setRegDate(avObject.getString("regDate"));
        policy.setUserName(avObject.getString("userName"));
        policy.setFrameNo(avObject.getString("frameNo"));
        policy.setEngine(avObject.getString("engine"));
        policy.setBrand(avObject.getString("brand"));
        policy.setPolicyDate(avObject.getString("policyDate"));
        policy.setStatus(avObject.getString("status"));
        policy.setPolicy_expire(avObject.getString("policy_expire"));
        policy.setPrice(avObject.getString("price"));
        policy.setForceimgs(avObject.getString("forceimgs"));
        policy.setBusinessimage(avObject.getString("businessimage"));
        policy.setCompany(avObject.getString("company"));
        policy.setObjectId(avObject.getObjectId());
        return policy;
    }

    class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return mAVUsers.size();
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_fragment_policy_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder) convertView.getTag();
            }

            setDtails(viewHolder, position);
            return convertView;
        }


    }


    class ViewHolder {
        @Bind(R.id.iv_policy_statu)
        ImageView mIvPolicyStatu;
        @Bind(R.id.tv_policy_name)
        TextView mTvPolicyName;
        @Bind(R.id.tv_policy_company)
        TextView mTvPolicyCompany;
        @Bind(R.id.tv_policy_id)
        TextView mTvPolicyID;
        @Bind(R.id.tv_data)
        TextView mTvData;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    private void setDtails(ViewHolder viewHolder, int positon) {
        viewHolder.mTvPolicyCompany.setText(mPolicys.get(positon).getString("company"));
        viewHolder.mTvPolicyName.setText(mPolicys.get(positon).getString("userName"));
        viewHolder.mTvPolicyID.setText(mPolicys.get(positon).getObjectId());
        String regDate = mPolicys.get(positon).getString("regDate");
        int i = regDate.indexOf("年");
        String date = regDate.substring(i + 1);
        viewHolder.mTvData.setText(date);
        try {
            Date regD = MyUtills.ConverToDate(regDate);
            long time = System.currentTimeMillis() - regD.getTime();
            if (time >= 432000000) {
                viewHolder.mTvPolicyName.setTextColor(Color.RED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        String status = mPolicys.get(positon).getString("status");

        switch (status) {
            case "未算价":
                Picasso.with(getActivity()).load(R.mipmap.dsj).into(viewHolder.mIvPolicyStatu);
                break;
            case "已算价":
                Picasso.with(getActivity()).load(R.mipmap.ysj).into(viewHolder.mIvPolicyStatu);
                break;
            case "待审核":
                Picasso.with(getActivity()).load(R.mipmap.dsh).into(viewHolder.mIvPolicyStatu);
                break;
            case "已完成":
                Picasso.with(getActivity()).load(R.mipmap.ysh).into(viewHolder.mIvPolicyStatu);
                break;
        }


    }

    @Override
    protected void onInvisible() {
        mPolicys.clear();
        mAVUsers.clear();
    }

    private void initEvent() {

        mLvCustomer.setOnItemClickListener(this);

        mSrCustomer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSrCustomer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPolicys.clear();
                initCustomerData();
                mSrCustomer.setRefreshing(false);
            }
        });

        mLvCustomer.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLvCustomer != null && mLvCustomer.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    mSrCustomer.setEnabled(enable);
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                    loadMore();
                }
            }
        });

    }

    private void loadMore() {

    }
}
