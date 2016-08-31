package com.chinalooke.android.cheju.activity.salesman;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.adapter.MyPagerAdapter;
import com.chinalooke.android.cheju.fragment.OrderFragment;
import com.chinalooke.android.cheju.fragment.ServiceFragment;
import com.chinalooke.android.cheju.fragment.SyFragment;
import com.chinalooke.android.cheju.fragment.WodeFragment;
import com.chinalooke.android.cheju.fragment.salesman.CustomerFragment;
import com.chinalooke.android.cheju.fragment.salesman.PerformanceFragment;
import com.chinalooke.android.cheju.fragment.salesman.PolicyFragment;
import com.chinalooke.android.cheju.utills.ImageTools;
import com.chinalooke.android.cheju.utills.MyUtills;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainSalesActivity extends FragmentActivity {

    @Bind(R.id.rb_yh)
    RadioButton mRbYh;

    public ViewPager getVpSalesMain() {
        return mVpSalesMain;
    }

    @Bind(R.id.rb_bdh)

    RadioButton mRbBdh;
    @Bind(R.id.rb_yj)
    RadioButton mRbYj;
    @Bind(R.id.rg_main)
    RadioGroup mRgMain;
    @Bind(R.id.vp_sales_main)
    ViewPager mVpSalesMain;
    private FragmentManager mFragmentManager;
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sales);
        ButterKnife.bind(this);
        initView();
        initEvent();


    }


    private void initEvent() {
        mRgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_yh:
                        mVpSalesMain.setCurrentItem(0);
                        break;

                    case R.id.rb_bdh:
                        mVpSalesMain.setCurrentItem(1);
                        break;

                    case R.id.rb_yj:
                        mVpSalesMain.setCurrentItem(2);
                        break;
                }
            }
        });

        mVpSalesMain.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mRgMain.check(R.id.rb_yh);
                        break;
                    case 1:
                        mRgMain.check(R.id.rb_bdh);
                        break;
                    case 2:
                        mRgMain.check(R.id.rb_yj);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initView() {
        mRbYh.setCompoundDrawables(null, ImageTools.setDrwableSize(this, R.drawable.main_home_salesman_selector), null, null);
        mRbYj.setCompoundDrawables(null, ImageTools.setDrwableSize(this, R.drawable.main_preformence_salesman_selector), null, null);
        mRbBdh.setCompoundDrawables(null, ImageTools.setDrwableSize(this, R.drawable.main_policy_salesman_selector), null, null);
        mRgMain.check(R.id.rb_yh);

        mFragmentManager = getSupportFragmentManager();
        mFragments.add(new CustomerFragment());
        mFragments.add(new PolicyFragment());
        mFragments.add(new PerformanceFragment());
        mVpSalesMain.setAdapter(new MyPagerAdapter(mFragmentManager, mFragments));
        mVpSalesMain.setOffscreenPageLimit(2);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MyUtills.showDialog(this, "提示", "确定退出车聚吗？");
        }
        return super.onKeyDown(keyCode, event);
    }
}
