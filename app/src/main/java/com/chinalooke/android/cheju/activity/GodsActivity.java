package com.chinalooke.android.cheju.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.view.MyScrollView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;

public class GodsActivity extends AppCompatActivity implements MyScrollView.OnScrollListener {


    @Bind(R.id.tv_gods_cureent)
    TextView mTvGodsCureent;
    @Bind(R.id.tv_gods_price)
    TextView mTvGodsPrice;
    @Bind(R.id.scrollView)
    MyScrollView mScrollView;
    @Bind(R.id.tv_goods_name)
    TextView mTvGoodsName;
    @Bind(R.id.kanner_goods)
    BGABanner mKannerGoods;
    @Bind(R.id.iv1)
    ImageView mIv1;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.iv3)
    ImageView mIv3;
    @Bind(R.id.iv4)
    ImageView mIv4;
    @Bind(R.id.iv5)
    ImageView mIv5;
    @Bind(R.id.tv_score_goods)
    TextView mTvScoreGoods;
    @Bind(R.id.tv_sales)
    TextView mTvSales;
    @Bind(R.id.tv_goods_descript)
    TextView mTvGoodsDescript;
    @Bind(R.id.tv_gods_score)
    TextView mTvGodsScore;
    @Bind(R.id.iv_qrcode)
    ImageView mIvQrcode;


    private int screenWidth;
    private static View suspendView;
    private static WindowManager.LayoutParams suspendLayoutParams;
    private int buyLayoutHeight;
    private int myScrollViewTop;
    private int buyLayoutTop;
    private WindowManager mWindowManager;
    private LinearLayout mBuyLayout;

    private List<View> mAdList = new ArrayList<>();
    private Toast mToast;
    private AVObject mGood;
    private AVUser mCurrentUser;
    private String mShopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gods);
        ButterKnife.bind(this);
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        mBuyLayout = (LinearLayout) findViewById(R.id.buy);
        mCurrentUser = AVUser.getCurrentUser();
        initData();
        initView();
        initEvent();
    }

    private void initView() {
        mTvGoodsName.setText(mGood.getString("name"));
        mTvGodsCureent.setText("¥" + mGood.getString("currentPrice"));
        mTvGodsPrice.setText("¥" + mGood.getString("price"));
        mTvGodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTvSales.setText("已售" + mGood.getString("sales"));
        Number grade = mGood.getNumber("grade");
        setStar(grade);
        mTvScoreGoods.setText(grade + "分");
        mTvGoodsDescript.setText(mGood.getString("descript"));
        mTvGodsScore.setText("消耗积分:" + mGood.getString("score"));
    }

    private void setStar(Number grade) {
        mIv1.setEnabled(grade.intValue() > 0);
        mIv2.setEnabled(grade.intValue() > 1);
        mIv3.setEnabled(grade.intValue() > 2);
        mIv4.setEnabled(grade.intValue() > 3);
        mIv5.setEnabled(grade.intValue() > 4);
    }

    private void initData() {
        mShopId = getIntent().getStringExtra("shopid");
        mGood = getIntent().getParcelableExtra("goods");
        AVRelation<AVObject> images = mGood.getRelation("images");
        AVQuery<AVObject> query = images.getQuery();
        if (NetUtil.is_Network_Available(getApplicationContext())) {

            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        for (AVObject avObject : list) {
                            String url = avObject.getString("url");
                            ImageView imageView = new ImageView(GodsActivity.this);
                            Picasso.with(GodsActivity.this).load(url).resize(screenWidth, MyUtills.Dp2Px(getApplicationContext(), 160)).centerCrop()
                                    .placeholder(R.mipmap.placeholder).into(imageView);
                            mAdList.add(imageView);
                        }
                        mKannerGoods.setData(mAdList);
                    }
                }
            });
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }
    }

    private void initEvent() {
        mScrollView.setOnScrollListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            buyLayoutHeight = mBuyLayout.getHeight();
            buyLayoutTop = mBuyLayout.getTop();
            myScrollViewTop = mScrollView.getTop();
        }
    }

    @Override
    public void onScroll(int scrollY) {
        if (scrollY >= buyLayoutTop) {
            if (suspendView == null) {
                showSuspend();
            }
        } else if (scrollY <= buyLayoutTop + buyLayoutHeight) {
            if (suspendView != null) {
                removeSuspend();
            }
        }
    }

    private void showSuspend() {
        if (suspendView == null) {
            suspendView = LayoutInflater.from(this).inflate(R.layout.buy_layout, null);
            if (suspendLayoutParams == null) {
                suspendLayoutParams = new WindowManager.LayoutParams();
                suspendLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE; //悬浮窗的类型，一般设为2002，表示在所有应用程序之上，但在状态栏之下
                suspendLayoutParams.format = PixelFormat.RGBA_8888;
                suspendLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;  //悬浮窗的行为，比如说不可聚焦，非模态对话框等等
                suspendLayoutParams.gravity = Gravity.TOP;  //悬浮窗的对齐方式
                suspendLayoutParams.width = screenWidth;
                suspendLayoutParams.height = buyLayoutHeight;
                suspendLayoutParams.x = 0;  //悬浮窗X的位置
                suspendLayoutParams.y = myScrollViewTop;  ////悬浮窗Y的位置
            }
        }
        mWindowManager.addView(suspendView, suspendLayoutParams);
    }

    private void removeSuspend() {
        if (suspendView != null) {
            mWindowManager.removeView(suspendView);
            suspendView = null;
        }
    }

    @OnClick({R.id.btn_buy, R.id.iv_wirte_back, R.id.iv_collect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_buy:
                Intent intent = new Intent(GodsActivity.this, GoodsCountActivity.class);
                intent.putExtra("shopid", mShopId);
                Bundle bundle = new Bundle();
                bundle.putParcelable("goods", mGood);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.iv_wirte_back:
                finish();
                break;

        }
    }
}
