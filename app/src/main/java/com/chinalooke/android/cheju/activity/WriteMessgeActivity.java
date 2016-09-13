package com.chinalooke.android.cheju.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.fragment.WriteCheliangFragment;
import com.chinalooke.android.cheju.fragment.WriteChezhuFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WriteMessgeActivity extends FragmentActivity {


    @Bind(R.id.tv_write_chezhu)
    TextView mTvWriteChezhu;
    @Bind(R.id.tv_write_cheliang)
    TextView mTvWriteCheliang;
    @Bind(R.id.fl_write)
    FrameLayout mFlWrite;
    @Bind(R.id.tv_title2)
    TextView mTvTitle2;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private FragmentManager mFragmentManager;
    private Policy mPolicy;
    private WriteChezhuFragment mWriteChezhuFragment;
    private WriteCheliangFragment mWriteCheliangFragment;
    private FragmentTransaction mTransaction;
    private Fragment mContent = null;
    private boolean mDone = false;
    private Policy mDpolicy;

    public int getCurrentFragment() {
        return currentFragment;
    }

    private int currentFragment;

    public Policy getDpolicy() {
        return mDpolicy;
    }

    public boolean isDone() {
        return mDone;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_messge);
        ButterKnife.bind(this);

        mFragmentManager = getFragmentManager();
        mPolicy = new Policy();
        initData();
        initView();

    }


    public WriteCheliangFragment getWriteCheliangFragment() {
        return mWriteCheliangFragment;
    }

    private void initData() {
        mDpolicy = (Policy) getIntent().getSerializableExtra("dpolicy");
        if (mDpolicy != null) {
            mDone = true;
            mTvTitle2.setText("订单详情");
        }
        mWriteChezhuFragment = new WriteChezhuFragment();
        mWriteCheliangFragment = new WriteCheliangFragment();


        String company = getIntent().getExtras().getString("company");
        if (!TextUtils.isEmpty(company)) {
            mPolicy.setCompany(company);
        } else {
            mPolicy.setCompany("");
        }
    }

    public Policy getPolicy() {
        return mPolicy;
    }


    private void initView() {
        mTvWriteChezhu.setTextColor(getResources().getColor(R.color.selectcolor));
        mTvWriteCheliang.setTextColor(getResources().getColor(R.color.unselectcolor));
        mFragmentManager.beginTransaction().add(R.id.fl_write, mWriteChezhuFragment).commit();
    }


    @OnClick({R.id.iv_wirte_back, R.id.tv_write_chezhu, R.id.tv_write_cheliang})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wirte_back:
                finish();
                break;
            case R.id.tv_write_chezhu:
                mTvWriteChezhu.setTextColor(getResources().getColor(R.color.selectcolor));
                mTvWriteCheliang.setTextColor(getResources().getColor(R.color.unselectcolor));
                switchContent(mWriteCheliangFragment, mWriteChezhuFragment);
                break;
            case R.id.tv_write_cheliang:
                mWriteChezhuFragment.writeNumer();
                if (mWriteChezhuFragment.checkWrite()) {
                    mTvWriteCheliang.setTextColor(getResources().getColor(R.color.selectcolor));
                    mTvWriteChezhu.setTextColor(getResources().getColor(R.color.unselectcolor));
                    switchContent(mWriteChezhuFragment, mWriteCheliangFragment);
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (mContent == mWriteCheliangFragment) {
            switchContent(mWriteCheliangFragment, mWriteChezhuFragment);
        } else {
            finish();
        }
    }


    public void setPolicy(Policy policy) {
        mPolicy = policy;
    }

    public void switchContent(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fl_write, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    public void changeStatu() {
        mTvWriteCheliang.setTextColor(getResources().getColor(R.color.selectcolor));
        mTvWriteChezhu.setTextColor(getResources().getColor(R.color.unselectcolor));
    }
}
