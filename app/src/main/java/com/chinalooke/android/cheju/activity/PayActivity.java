package com.chinalooke.android.cheju.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.bigkoo.pickerview.TimePickerView;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Address;
import com.chinalooke.android.cheju.bean.PayResult;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.utills.SignUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.chinalooke.android.cheju.constant.Constant.RSA_PRIVATE;
import static com.chinalooke.android.cheju.constant.Constant.SELLER;

public class PayActivity extends AppCompatActivity implements TimePickerView.OnTimeSelectListener {

    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_address)
    TextView mTvAddress;
    @Bind(R.id.rl_pay)
    RelativeLayout mRlPay;
    private TimePickerView pvTime;
    private Toast mToast;
    private Policy mDpolicy;
    private Address mAddress;
    private ProgressDialog mProgressDialog;
    private AVObject mOrder1;
    private static final int SDK_PAY_FLAG = 1;


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

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + Constant.PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "";

        return orderInfo;
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

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
                        alipay();
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

    private void alipay() {
        String orderInfo = getOrderInfo("车聚订单", "该测试商品的详细描述", mDpolicy.getDiscountPrice());
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
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
