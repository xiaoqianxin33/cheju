package com.chinalooke.android.cheju.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ren.qinc.numberbutton.NumberButton;

public class GoodsCountActivity extends AppCompatActivity {

    @Bind(R.id.tv_goods_name)
    TextView mTvGoodsName;
    @Bind(R.id.tv_goods_price)
    TextView mTvGoodsPrice;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.nbtn)
    NumberButton mNbtn;
    @Bind(R.id.tv_count_price)
    TextView mTvCountPrice;
    @Bind(R.id.iv_qcord)
    ImageView mIvQcord;
    @Bind(R.id.btn_submit)
    Button mBtnSubmit;
    private AVObject mGoods;
    private String mShopId;
    private Toast mToast;
    private int mCount;
    private int mUnitPrice;
    private ProgressDialog mProgressDialog;
    private AVUser mCurrentUser;
    private int mPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_count);
        ButterKnife.bind(this);
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        mCurrentUser = AVUser.getCurrentUser();
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        mNbtn.setBuyMax(5)
                .setInventory(6)
                .setCurrentNumber(10)
                .setOnWarnListener(new NumberButton.OnWarnListener() {
                    @Override
                    public void onWarningForInventory(int inventory) {
                        mCount = inventory;
                        mTvCountPrice.setText("消耗积分:" + mUnitPrice * inventory);
                        mPrice = mUnitPrice * inventory;
                    }

                    @Override
                    public void onWarningForBuyMax(int buyMax) {
                        mToast.setText("超过最大购买数:" + buyMax);
                        mToast.show();
                    }
                });

    }

    private void initView() {
        mTvGoodsName.setText(mGoods.getString("title"));
        mTvGoodsPrice.setText("消耗积分:" + mGoods.getString("score"));
        mTvCountPrice.setText("消耗积分:" + mGoods.getString("score"));
    }

    private void initData() {
        mGoods = getIntent().getParcelableExtra("goods");
        mShopId = getIntent().getStringExtra("shopid");
        String score = mGoods.getString("score");
        mUnitPrice = Integer.parseInt(score);
    }

    @OnClick({R.id.iv_wirte_back, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wirte_back:
                finish();
                break;
            case R.id.btn_submit:
                MyUtills.showSingerDialog(this, "提示", "确定购买吗?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent();


                        if (NetUtil.is_Network_Available(getApplicationContext())) {
                            mProgressDialog = MyUtills.initDialog("购买中...", GoodsCountActivity.this);
                            mProgressDialog.show();
                            mCurrentUser.fetchIfNeededInBackground(new GetCallback<AVObject>() {
                                @Override
                                public void done(AVObject avObject, AVException e) {
                                    if (e == null) {
                                        saveLeanCloud();
                                    } else {
                                        mProgressDialog.dismiss();
                                        mToast.setText("积分查询失败，请检查网络连接");
                                        mToast.show();
                                    }
                                }
                            });

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


                break;
        }
    }

    private void saveLeanCloud() {
        Number oldScore = AVUser.getCurrentUser().getNumber("score");
        int i = oldScore.intValue();

        int i2 = mPrice;

        if (i < i2) {
            mToast.setText("对不起，积分余额不足");
            mToast.show();
            mProgressDialog.dismiss();
        } else {
            int i1 = i - mPrice;
            AVUser.getCurrentUser().put("score", i1);
            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        String message = mCurrentUser.getObjectId() + ":" + mShopId + ":" + mGoods.getObjectId()
                                + ":" + mCount + ":" + mPrice;
                        creatOrder(message);
                    } else {
                        mProgressDialog.dismiss();
                        mToast.setText("购买失败，请重试");
                        mToast.show();
                    }
                }
            });
        }

    }

    private void creatOrder(String message) {
        final AVObject order = new AVObject("order");
        order.put("userId", mCurrentUser.getObjectId());
        order.put("addDate", new Date());
        order.put("goodsId", mGoods.getObjectId());
        order.put("price", mPrice);
        order.put("status", 2);
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        ImageRequest imageRequest = new ImageRequest(
                "http://api.k780.com:88/?app=qr.get&data=" + message + "&level=L&size=6",
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        mIvQcord.setVisibility(View.VISIBLE);
                        mIvQcord.setImageBitmap(response);
                        order.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                mProgressDialog.dismiss();
                                if (e == null) {
                                    mToast.setText("优惠劵订单创建成功，请在订单表中查看");
                                    mToast.show();
                                    mBtnSubmit.setText("购买成功");
                                    mBtnSubmit.setEnabled(false);
                                } else {
                                    mToast.setText("订单创建失败，请重试");
                                    mToast.show();
                                }
                            }
                        });
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("二维码生成失败，请稍后再试");
                mToast.show();
            }
        });
        mQueue.add(imageRequest);
    }


}
