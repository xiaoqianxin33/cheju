package com.chinalooke.android.cheju.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Goods;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.view.MyScrollView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

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
    @Bind(R.id.iv_qcord)
    ImageView mIvQcord;


    private int screenWidth;
    private static View suspendView;
    private static WindowManager.LayoutParams suspendLayoutParams;
    private int buyLayoutHeight;
    private int myScrollViewTop;
    private int buyLayoutTop;
    private WindowManager mWindowManager;
    private LinearLayout mBuyLayout;
    private Goods mGoods;
    private List<String> mImages = new ArrayList<>();


    Handler mHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    initView();
                    initEvent();
                    break;

            }

        }
    };
    private List<View> mAdList = new ArrayList<>();
    private Toast mToast;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gods);
        ButterKnife.bind(this);
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        mBuyLayout = (LinearLayout) findViewById(R.id.buy);
        initData();
    }

    private void initView() {
        mTvGoodsName.setText(mGoods.getTitle());
        mTvGodsCureent.setText("¥" + mGoods.getCurrentPrice());
        mTvGodsPrice.setText("¥" + mGoods.getPrice());
        mTvGodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTvSales.setText("已售" + mGoods.getSales());
        int grade = Integer.parseInt(mGoods.getGrade());
        setStar(grade);
        mTvScoreGoods.setText(grade + "分");
        mTvGoodsDescript.setText(mGoods.getDescript());
        mTvGodsScore.setText("消耗积分:" + mGoods.getScore());
        for (String string : mImages) {
            ImageView imageView = new ImageView(this);
            Picasso.with(this).load(string).resize(screenWidth, MyUtills.Dp2Px(this, 160)).centerCrop().into(imageView);
            mAdList.add(imageView);
        }
        mKannerGoods.setData(mAdList);
    }

    private void setStar(int grade) {
        mIv1.setEnabled(grade > 0);
        mIv2.setEnabled(grade > 1);
        mIv3.setEnabled(grade > 2);
        mIv4.setEnabled(grade > 3);
        mIv5.setEnabled(grade > 4);
    }

    private void initData() {
        mGoods = (Goods) getIntent().getExtras().getSerializable("goods");
        String images = mGoods.getImages();

        try {
            JSONArray jsonArray = new JSONArray(images);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                if (id != null)
                    mImages.add(id);

                if (i == jsonArray.length() - 1)
                    mHandler.sendEmptyMessage(1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    @OnClick(R.id.btn_buy)
    public void onClick() {
        MyUtills.showSingerDialog(this, "提示", "确定购买吗?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (NetUtil.is_Network_Available(getApplicationContext())) {
                    mProgressDialog = MyUtills.initDialog("购买中...", GodsActivity.this);
                    mProgressDialog.show();
                    saveLeanCloud();
                } else {
                    mToast.setText("网络不可用，请检查网络连接");
                    mToast.show();
                }

            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

    }

    private void saveLeanCloud() {
        Number oldScore = AVUser.getCurrentUser().getNumber("score");
        int i = oldScore.intValue();

        int i2 = Integer.parseInt(mGoods.getScore());

        if (i < i2) {
            mToast.setText("对不起，余额不足");
            mToast.show();
            mProgressDialog.dismiss();
        } else {
            int i1 = i - Integer.parseInt(mGoods.getScore());
            AVUser.getCurrentUser().put("score", i1);
            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    mProgressDialog.dismiss();
                    if (e == null) {
                        createQRcard();
                    } else {
                        mToast.setText("购买失败，请重试");
                        mToast.show();
                    }
                }
            });
        }

    }

    private void createQRcard() {
        mIvQcord.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(mGoods.getTitle() + ":" + mGoods.getScore(),
                        MyUtills.Dp2Px(getApplicationContext(), 300));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvQcord.setImageBitmap(bitmap);
                        mToast.setText("购买成功，二维码可在我的优惠劵中查看");
                        mToast.show();
                    }
                });
            }
        }).start();
    }
}
