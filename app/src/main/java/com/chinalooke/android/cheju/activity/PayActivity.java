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
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bigkoo.pickerview.TimePickerView;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Address;
import com.chinalooke.android.cheju.bean.Order;
import com.chinalooke.android.cheju.bean.Policy;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Toast mToast;
    private Policy mDpolicy;
    private Address mAddress;
    private ProgressDialog mProgressDialog;
    private AVObject mOrder;

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
    }

    private void initView() {
        mTvAddress.setText(mAddress.getAddress());
    }

    @OnClick({R.id.iv_alipay, R.id.iv_weipay, R.id.iv_x, R.id.tv_time, R.id.iv_ali})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_alipay:
                if (check()) {
                    mRlPay.setVisibility(View.GONE);
                    mIvAli.setVisibility(View.VISIBLE);
                    initDialog("订单创建中");
                    saveOrder();

                }
                break;
            case R.id.tv_time:
                pvTime = new TimePickerView(this, TimePickerView.Type.ALL);
                pvTime.setOnTimeSelectListener(this);
                pvTime.show();
                break;
            case R.id.iv_weipay:
                break;
            case R.id.iv_ali:
                Intent intent = new Intent();
                intent.putExtra("statu", true);
                Bundle bundle = new Bundle();
                if (mOrder != null)
                    bundle.putParcelable("order", mOrder);
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

    private void saveOrder() {
        final AVObject order = new AVObject("Order");
        order.put("userId", AVUser.getCurrentUser().getObjectId());
        Date date = new Date();
        order.put("addDate", date);
        order.put("policyId", mDpolicy.getObjectId());
        order.put("price", Integer.parseInt(mDpolicy.getDiscountPrice()));
        order.put("status", 1);
        order.put("addressId", mAddress.getObjectId());
        order.put("payType", "alipay");
        order.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    mOrder = order;
                    showPay();
                } else {
                    mToast.setText(e.getMessage());
                    mToast.show();
                }
            }
        });

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
        mTvTime.setText(getTime(date));
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }
}
