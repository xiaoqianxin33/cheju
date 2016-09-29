package com.chinalooke.android.cheju.activity.business;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.config.MyLeanCloudApp;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SweepQcodeActivity extends AppCompatActivity {

    @Bind(R.id.et_price)
    TextView mEtPrice;
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.tv_score)
    TextView mTvScore;
    @Bind(R.id.button)
    Button mButton;
    private AVUser mCurrentUser;
    private Toast mToast;
    private AVObject mOrder;
    private ProgressDialog mProgressDialog;
    private AVObject mShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sweep_qcode);
        ButterKnife.bind(this);
        mCurrentUser = AVUser.getCurrentUser();
        mToast = MyLeanCloudApp.getToast();
        mShop = MyLeanCloudApp.getAVObject();
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            initData();
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }
    }

    private void initData() {
        String result = getIntent().getStringExtra("result");
        AVQuery<AVObject> query = new AVQuery<>("Order");
        query.whereEqualTo("privilegeCode", result);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null && list.size() != 0) {
                    mOrder = list.get(0);
                    mTvCount.setText(mOrder.getNumber("count") + "");
                    mTvScore.setText(mOrder.getString("price"));
                    String goodsId = mOrder.getString("goodsId");
                    AVQuery<AVObject> query1 = new AVQuery<>("Goods");
                    query1.whereEqualTo("objectId", goodsId);
                    query1.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            if (e == null && list.size() != 0) {
                                AVObject avObject = list.get(0);
                                String name = avObject.getString("name");
                                mEtPrice.setText(name);
                            }
                        }
                    });

                }
            }
        });
    }


    @OnClick({R.id.iv_wirte_back, R.id.button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wirte_back:
                finish();
                break;
            case R.id.button:
                mButton.setEnabled(false);
                MyUtills.showSingerDialog(SweepQcodeActivity.this, "提示", "确定要消费掉这个兑换码？", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (NetUtil.is_Network_Available(getApplicationContext())) {
                                    mProgressDialog = MyUtills.initDialog("", SweepQcodeActivity.this);
                                    mProgressDialog.show();
                                    saveLeanCould();
                                } else {
                                    mButton.setEnabled(true);
                                    mToast.setText("网络不可用，请检查网络连接");
                                    mToast.show();
                                }
                            }
                        }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mButton.setEnabled(true);
                    }
                });
                break;
        }
    }


    private void saveLeanCould() {
        String type = mOrder.getString("status");

        int i = Integer.parseInt(type);
        if (i == 2) {
            Number score = mCurrentUser.getNumber("score");
            String price = mOrder.getString("price");
            int parseInt = Integer.parseInt(price);
            int nPrice = parseInt + score.intValue();
            mOrder.put("status", 4 + "");
            mOrder.saveInBackground();
            mCurrentUser.put("score", nPrice);
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    mProgressDialog.dismiss();
                    if (e == null) {
                        saveStatistics();
                        MyUtills.showSingerDialog(SweepQcodeActivity.this, "提示", "本兑换券已消费！",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                    } else {
                        mButton.setEnabled(true);
                        mToast.setText("解析失败");
                        mToast.show();
                    }
                }
            });
        } else if (i == 4) {
            mProgressDialog.dismiss();
            mButton.setEnabled(true);
            mToast.setText("该优惠劵已使用");
            mToast.show();
        }
    }

    private void saveStatistics() {
        AVObject avObject = new AVObject("Statistics");
        avObject.put("userId", mOrder.getString("userId"));
        avObject.put("type", "user");
        avObject.put("score", Integer.parseInt(mOrder.getString("price")));
        if (mShop != null)
            avObject.put("shopId", mShop.getObjectId());
        avObject.saveInBackground();
    }
}
