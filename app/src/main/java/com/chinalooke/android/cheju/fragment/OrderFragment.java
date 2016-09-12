package com.chinalooke.android.cheju.fragment;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.MainActivity;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiao on 2016/8/6.
 */
public class OrderFragment extends Fragment {


    @Bind(R.id.tv_order_chexian)
    TextView mTvOrderChexian;
    @Bind(R.id.tv_order_youhuijua)
    TextView mTvOrderYouhuijua;
    @Bind(R.id.fl_order)
    FrameLayout mFlOrder;
    private CarOrderFragment mCarOrderFragment;
    private CouponOrderFragment mCouponOrderFragment;
    private FragmentManager mFragmentManager;
    private Fragment mContent = null;
    private View mView;
    private AVUser mCurrentUser;
    private Toast mToast;


    private boolean isVisible = false;


    public boolean isVisibled() {
        return isVisible;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, mView);
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        mCurrentUser = ((MainActivity) getActivity()).getCurrentUser();
        mCarOrderFragment = new CarOrderFragment();
        mCouponOrderFragment = new CouponOrderFragment();
        mFragmentManager = getChildFragmentManager();
        return mView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BlackFragment blackFragment = new BlackFragment();
        switchContent(blackFragment, mCarOrderFragment);
    }


    public void switchContent(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fl_order, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);


        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @OnClick({R.id.tv_order_chexian, R.id.tv_order_youhuijua})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_order_chexian:
                mTvOrderChexian.setTextColor(getResources().getColor(R.color.selectcolor));
                mTvOrderYouhuijua.setTextColor(getResources().getColor(R.color.unselectcolor));
                switchContent(mCouponOrderFragment, mCarOrderFragment);
                break;
            case R.id.tv_order_youhuijua:
                mTvOrderChexian.setTextColor(getResources().getColor(R.color.unselectcolor));
                mTvOrderYouhuijua.setTextColor(getResources().getColor(R.color.selectcolor));
                switchContent(mCarOrderFragment, mCouponOrderFragment);
                break;
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }
}