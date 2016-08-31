package com.chinalooke.android.cheju.fragment.salesman;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import com.avos.avoscloud.feedback.FeedbackThread;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.salesman.MainSalesActivity;
import com.chinalooke.android.cheju.utills.LeanCloudTools;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.UIutils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerFragment extends Fragment implements AdapterView.OnItemClickListener {


    @Bind(R.id.lv_customer_lv)
    ListView mLvCustomer;
    @Bind(R.id.sr_customer)
    SwipeRefreshLayout mSrCustomer;
    private boolean isPrepared = false;
    List<AVUser> mAVUsers = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private MyAdapt mMyAdapt;
    private boolean isVisible = false;
    private boolean isLoading = false;
    private List<AVObject> mPolicys = new ArrayList<>();

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


    private void initData() {
        initCustomerData();

    }

    private void initPolicyData() {
        final int[] i = {0};

        for (int j = 0; j < mAVUsers.size(); j++) {
            AVQuery<AVObject> query = new AVQuery<>("Policy");

            query.whereEqualTo("phone", mAVUsers.get(j).getString("mobilePhoneNumber"));
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public synchronized void done(List<AVObject> list, AVException e) {
                    if (e == null && list.size() > 0) {
                        mPolicys.add(list.get(0));
                        i[0]++;
                        if (i[0] == mAVUsers.size()) {
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
//        userQuery.whereEqualTo("referrer", "23456");

        userQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (e == null)
                    mAVUsers = list;
                if (list == null || list.size() == 0)
                    mProgressDialog.dismiss();

                Collections.sort(mAVUsers, new Comparator<AVUser>() {
                    @Override
                    public int compare(AVUser lhs, AVUser rhs) {
                        String llevel = lhs.getString("level");
                        String rlevel = rhs.getString("level");
                        String ls = llevel.substring(llevel.length() - 1, llevel.length());
                        String rs = rlevel.substring(llevel.length() - 1, llevel.length());
                        if (Integer.parseInt(ls) < Integer.parseInt(rs)) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });

                mHandler.sendEmptyMessage(2);
            }
        });
    }


    private void initDialog() {
        mProgressDialog = UIutils.initLoadDialog(getActivity());
        mProgressDialog.show();
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
                initData();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_fragment_custom_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder) convertView.getTag();
            }

            setDtails(viewHolder, position);

            viewHolder.mTvDial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = mAVUsers.get(position).getUsername();
                    Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                    startActivity(intentPhone);
                }
            });

            return convertView;
        }


    }

    private void setDtails(ViewHolder viewHolder, int positon) {
        String level = mAVUsers.get(positon).getString("level");
        int i = Integer.parseInt(level.substring(level.length() - 1, level.length()));
        if (i >= 0 && i <= 3) {
            viewHolder.mTvCustomerName.setTextColor(Color.GRAY);
            viewHolder.mTvCustomerVIP.setTextColor(Color.GRAY);

        } else if (i > 3 && i < 6) {
            viewHolder.mTvCustomerName.setTextColor(Color.RED);
            viewHolder.mTvCustomerVIP.setTextColor(Color.RED);
        } else {
            viewHolder.mTvCustomerName.setTextColor(getActivity().getResources().getColor(R.color.gold));
            viewHolder.mTvCustomerVIP.setTextColor(getActivity().getResources().getColor(R.color.gold));
        }
//        AVObject mAvObject = new AVObject();
//        for (AVObject avObject : mPolicys) {
//            if (mAVUsers.get(positon).getUsername().equals(avObject.getString("phone"))) {
//                mAvObject = avObject;
//                break;
//            }
//        }
        viewHolder.mTvCustomerName.setText(mAVUsers.get(positon).getUsername());
        viewHolder.mTvCustomerScore.setText("积分值:   " + mAVUsers.get(positon).getString("score"));
        viewHolder.mTvCustomerVIP.setText(level);

        Picasso.with(getActivity()).load(R.mipmap.vip).into(viewHolder.mIvCustomer);
    }


   static  class ViewHolder {
        @Bind(R.id.iv_customer)
        ImageView mIvCustomer;
        @Bind(R.id.tv_customer_name)
        TextView mTvCustomerName;
        @Bind(R.id.tv_customer_vip)
        TextView mTvCustomerVIP;
        @Bind(R.id.tv_customer_score)
        TextView mTvCustomerScore;
        @Bind(R.id.tv_dial)
        TextView mTvDial;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            inVisible();
        }

    }

    private void inVisible() {
        mPolicys.clear();
        mAVUsers.clear();
    }

    private void onVisible() {
        lazyLoad();
    }

    private void lazyLoad() {

        if (!isPrepared || !isVisible)
            return;
        initDialog();
        initData();
        initEvent();

    }

}
