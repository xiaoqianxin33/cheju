package com.chinalooke.android.cheju.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.adapter.MyPagerAdapter;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.db.CacheDbHelper;
import com.chinalooke.android.cheju.fragment.OrderFragment;
import com.chinalooke.android.cheju.fragment.ServiceFragment;
import com.chinalooke.android.cheju.fragment.SyFragment;
import com.chinalooke.android.cheju.fragment.WodeFragment;
import com.chinalooke.android.cheju.utills.MyUtills;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {

    @Bind(R.id.rb_sy)
    RadioButton mRbSy;
    @Bind(R.id.rb_kf)
    RadioButton mRbKf;
    @Bind(R.id.rb_dd)
    RadioButton mRbDd;
    @Bind(R.id.rg_main)
    RadioGroup mRgMain;
    @Bind(R.id.viewpage_main)
    ViewPager mViewpageMain;


    @Bind(R.id.rb_wd)
    RadioButton mRbWd;
    private FragmentManager mFragmentManager;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private AVUser mCurrentUser;
    private CacheDbHelper mCacheDbHelper;


    public AVUser getCurrentUser() {
        return mCurrentUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mCurrentUser = AVUser.getCurrentUser();
        mCacheDbHelper = new CacheDbHelper(this, 1);
        setDrwableSize();
        initView();
        initEvent();


    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.finish();
        }

    };

    public CacheDbHelper getCacheDbHelper() {
        return mCacheDbHelper;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            mCurrentUser = currentUser;
        }
    }

    private void initView() {
        mFragmentManager = getSupportFragmentManager();
        mFragments.add(new SyFragment());
        mFragments.add(new ServiceFragment());
        mFragments.add(new OrderFragment());
        mFragments.add(new WodeFragment());
        mViewpageMain.setAdapter(new MyPagerAdapter(mFragmentManager, mFragments));
        mViewpageMain.setOffscreenPageLimit(0);
    }


    private void initEvent() {


        mRgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sy:
                        mViewpageMain.setCurrentItem(0);
                        break;

                    case R.id.rb_kf:
                        mViewpageMain.setCurrentItem(1);
                        break;

                    case R.id.rb_dd:
                        mViewpageMain.setCurrentItem(2);
                        break;

                    case R.id.rb_wd:
                        mViewpageMain.setCurrentItem(3);
                        break;
                }
            }
        });

        mViewpageMain.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mRgMain.check(R.id.rb_sy);
                        break;
                    case 1:
                        mRgMain.check(R.id.rb_kf);
                        break;
                    case 2:
                        mRgMain.check(R.id.rb_dd);
                        break;
                    case 3:
                        mRgMain.check(R.id.rb_wd);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(myReceiver, filter);
    }

    private void setDrwableSize() {

        Drawable drawableWeiHui = getResources().getDrawable(R.drawable.main_home_selector);
        drawableWeiHui.setBounds(0, 5, MyUtills.Dp2Px(this, 24), MyUtills.Dp2Px(this, 24));
        mRbSy.setCompoundDrawables(null, drawableWeiHui, null, null);

        Drawable drawabkf = getResources().getDrawable(R.drawable.main_service_selector);
        drawabkf.setBounds(0, 5, MyUtills.Dp2Px(this, 24), MyUtills.Dp2Px(this, 24));
        mRbKf.setCompoundDrawables(null, drawabkf, null, null);

        Drawable drawableWeiHui3 = getResources().getDrawable(R.drawable.main_order_selector);
        drawableWeiHui3.setBounds(0, 5, MyUtills.Dp2Px(this, 24), MyUtills.Dp2Px(this, 24));
        mRbDd.setCompoundDrawables(null, drawableWeiHui3, null, null);

        Drawable drawableWeiHui4 = getResources().getDrawable(R.drawable.main_wode_selector);
        drawableWeiHui4.setBounds(0, 5, MyUtills.Dp2Px(this, 24), MyUtills.Dp2Px(this, 24));
        mRbWd.setCompoundDrawables(null, drawableWeiHui4, null, null);

        mRgMain.check(R.id.rb_sy);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            MyUtills.showDialog(MainActivity.this, "提示", "确定退出车聚吗?");
        }
        return super.onKeyDown(keyCode, event);
    }


}
