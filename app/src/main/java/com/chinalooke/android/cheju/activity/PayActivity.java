package com.chinalooke.android.cheju.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.bigkoo.pickerview.TimePickerView;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Address;
import com.chinalooke.android.cheju.bean.Order;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.utills.NetUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayActivity extends AppCompatActivity implements TimePickerView.OnTimeSelectListener {

    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_address)
    TextView mTvAddress;
    @Bind(R.id.iv_ali)
    ImageView mIvAli;
    @Bind(R.id.rl_pay)
    RelativeLayout mRlPay;
    private TimePickerView pvTime;
    private Toast mToast;
    private Policy mDpolicy;
    private Address mAddress;
    private ProgressDialog mProgressDialog;
    private AVObject mOrder1;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mIvAli.setImageResource(R.mipmap.zhifuchenggong);
                    break;
            }

        }
    };
    private Date mDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        initData();
        initView();
    }

    private void initData() {
        mDpolicy = (Policy) getIntent().getSerializableExtra("dpolicy");
        mAddress = (Address) getIntent().getSerializableExtra("address");
        String objectId = mDpolicy.getObjectId();
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            AVQuery<AVObject> query = new AVQuery("Order");
            query.whereEqualTo("policyId", objectId);
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        mOrder1 = list.get(0);
                    }
                }
            });
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }
    }

    private void initView() {
        mTvAddress.setText(mAddress.getAddress());
    }

    @OnClick({R.id.iv_alipay, R.id.iv_weipay, R.id.iv_x, R.id.tv_time, R.id.iv_ali})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_alipay:
                if (check()) {
                    initDialog("订单创建中");
                    saveOrder(0);
                }
                break;
            case R.id.tv_time:
                pvTime = new TimePickerView(this, TimePickerView.Type.ALL);
                pvTime.setOnTimeSelectListener(this);
                pvTime.show();
                break;
            case R.id.iv_weipay:
                if (check()) {
                    initDialog("订单创建中");
                    saveOrder(1);
                }
                break;
            case R.id.iv_ali:
                Intent intent = new Intent();
                intent.putExtra("statu", true);
                Bundle bundle = new Bundle();
                if (mOrder1 != null)
                    bundle.putParcelable("order", mOrder1);
                intent.putExtras(bundle);
                setResult(0, intent);
                finish();
                break;
            case R.id.iv_x:
                finish();
                break;
        }
    }

    private void initDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    private void saveOrder(int i) {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            if (i == 0) {
                mOrder1.put("payType", "alipay");
            } else if (i == 1) {
                mOrder1.put("payType", "weixin");
            }

            mOrder1.put("addressId", mAddress.getObjectId());
            mOrder1.put("sendDate", mDate);
            mOrder1.put("price", mDpolicy.getDiscountPrice());
            mOrder1.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    mProgressDialog.dismiss();
                    if (e == null) {
                        mRlPay.setVisibility(View.GONE);
                        mIvAli.setVisibility(View.VISIBLE);
                        showPay();
                    } else {
                        mToast.setText("订单创建失败");
                        mToast.show();
                    }
                }
            });


        } else {
            mProgressDialog.dismiss();
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }


    }


    private boolean check() {
        String time = mTvTime.getText().toString();
        if ("点击选择".equals(time)) {
            mToast.setText("请选择收货时间");
            mToast.show();
            return false;
        }
        return true;
    }

    private void showPay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    mHandler.sendEmptyMessage(0);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onTimeSelect(Date date) {
        mDate = date;
        mTvTime.setText(getTime(date));
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }
}
