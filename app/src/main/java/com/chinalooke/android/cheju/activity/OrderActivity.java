package com.chinalooke.android.cheju.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.utills.PreferenceUtils;
import com.chinalooke.android.cheju.view.SyListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderActivity extends AppCompatActivity {

    @Bind(R.id.lv_order)
    SyListView mLvOrder;
    @Bind(R.id.btn_take_goods)
    Button mBtnTakeGoods;
    @Bind(R.id.btn_refund)
    Button mBtnRefund;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_phone)
    TextView mTvPhone;
    @Bind(R.id.tv_address)
    TextView mTvAddress;
    @Bind(R.id.tv_order_statu)
    TextView mTvOrderStatu;
    @Bind(R.id.tv_topolicy_order)
    TextView mTvTopolicyOrder;
    private ArrayList<String> mStrings = new ArrayList<>();
    private ArrayList<String> mPrices = new ArrayList<>();
    private MyAdapt mMyAdapt;
    private AVObject mPolicy;
    private AVObject mOrder;
    private Policy mPolicy1;
    private Toast mToast;
    private ProgressDialog mProgressDialog;
    private AVUser mCurrentUser;
    private AVObject mAddress;
    private String mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        mCurrentUser = AVUser.getCurrentUser();
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        initData();
        initView();
    }

    private void initView() {
        mMyAdapt = new MyAdapt();
        mLvOrder.setAdapter(mMyAdapt);
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            getAddress();
        } else {
            mTvAddress.setText("无法获取地址");
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }

        switch (mStatus) {
            case "2":
                mTvOrderStatu.setText("已付款，待发货");
                break;
            case "3":
                mTvOrderStatu.setText("已发货");
                break;
            case "4":
                mTvOrderStatu.setText("已确认收货");
                mBtnTakeGoods.setText("已收货");
                mBtnTakeGoods.setEnabled(false);
                break;
            case "5":
                mTvOrderStatu.setText("退款申请中");
                mBtnRefund.setText("正在退款");
                mBtnRefund.setEnabled(false);
                break;
            case "6":
                mTvOrderStatu.setText("退款完成");
                mBtnRefund.setText("已退款");
                mBtnRefund.setEnabled(false);
                break;
        }

    }

    private void initData() {
        mOrder = getIntent().getParcelableExtra("order");
        mPolicy = getIntent().getParcelableExtra("dpolicy");
        mPolicy1 = (Policy) getIntent().getSerializableExtra("policy");
        mStatus = mOrder.getString("status");
        mStrings.add("订单编号：");
        mStrings.add("支付方式：");
        mStrings.add("付款金额：");
        mStrings.add("订单创建时间：");
        mStrings.add("送货时间：");
        mStrings.add("运费：");
        mPrices.add(mOrder.getObjectId());
        String payType = mOrder.getString("payType");
        if (!TextUtils.isEmpty(payType)) {
            if ("weixin".equals(payType)) {
                mPrices.add("微信支付");
            } else if ("alipay".equals(payType)) {
                mPrices.add("支付宝");
            }
        } else {
            mPrices.add(" ");
        }
        mPrices.add(mOrder.getString("price"));
        mPrices.add(getTime(mOrder.getDate("addDate")));
        Date sendDate = mOrder.getDate("sendDate");
        if (sendDate != null) {
            mPrices.add(getTime(sendDate));
        } else {
            mPrices.add(" ");
        }

        Number carriage = mOrder.getNumber("carriage");
        if (carriage != null) {
            mPrices.add(carriage + "");
        } else {
            mPrices.add(" ");
        }
    }

    private void getAddress() {
        if (mOrder != null) {
            String addressId = mOrder.getString("addressId");
            AVQuery<AVObject> query = new AVQuery<>("Address");
            query.whereEqualTo("objectId", addressId);
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        mAddress = list.get(0);
                        mTvName.setText("收货人 :" + mAddress.getString("name"));
                        mTvAddress.setText("收货地址 :" + mAddress.getString("address"));
                        mTvPhone.setText(mAddress.getString("phone"));
                    } else {
                        mToast.setText("获取地址出错，请检查网络");
                        mToast.show();
                    }
                }
            });
        }
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return mStrings.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_order_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.mTvDetailname.setText(mStrings.get(position));
            viewHolder.mTvDetailprice.setText(mPrices.get(position));
            return convertView;
        }


    }


    static class ViewHolder {
        @Bind(R.id.tv_detailname)
        TextView mTvDetailname;
        @Bind(R.id.tv_detailprice)
        TextView mTvDetailprice;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }


    @OnClick({R.id.iv_wirte_back, R.id.tv_topolicy_order, R.id.btn_take_goods
            , R.id.btn_refund})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_refund:


                break;
            case R.id.tv_topolicy_order:
                Intent intent = new Intent();
                intent.setClass(OrderActivity.this, WriteMessgeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("dpolicy", mPolicy1);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.iv_wirte_back:
                finish();
                break;
            case R.id.btn_take_goods:
                MyUtills.showSingerDialog(this, "提示", "确定收货吗?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (NetUtil.is_Network_Available(getApplicationContext())) {
                            mProgressDialog = MyUtills.initDialog("正在提交请求", OrderActivity.this);
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
                break;

        }
    }

    private void saveLeanCloud() {
        mOrder.put("status", "4");
        mPolicy.put("status", "4");
        mPolicy.saveInBackground();
        mOrder.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    pushMessage();
//                    Map<String, String> dicParameters = new HashMap<String, String>();
//                    dicParameters.put("username", mCurrentUser.getUsername());
//                    dicParameters.put("pay", mOrder.getString("price"));
//                    AVCloud.callFunctionInBackground("getScore", dicParameters, new FunctionCallback() {
//                        public void done(Object object, AVException e) {
//                            if (e == null) {
//
//                            } else {
//                                mProgressDialog.dismiss();
//                                mToast.setText("提交失败，请稍后重试");
//                                mToast.show();
//                            }
//                        }
//                    });

                } else {
                    mProgressDialog.dismiss();
                    mToast.setText("请稍后重试");
                    mToast.show();
                }
            }
        });
    }

    private void pushMessage() {
        String referrer = mCurrentUser.getString("referrer");
        AVQuery<AVObject> query = new AVQuery<>("_User");
        query.whereEqualTo("mobilePhoneNumber", referrer);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    final AVObject avObject = list.get(0);
                    String installationId = avObject.getString("installationId");
                    AVQuery pushQuery = AVInstallation.getQuery();
                    pushQuery.whereEqualTo("installationId", installationId);
                    pushQuery.whereEqualTo("channels", "private");
                    AVPush.sendMessageInBackground("message to installation", pushQuery, new SendCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                AVObject statics = new AVObject("Statistics");
                                statics.put("userId", mCurrentUser.getObjectId());
                                statics.put("date", new Date());
                                statics.put("type", "user");
                                statics.put("price", Integer.parseInt(mOrder.getString("price")));
                                statics.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        mProgressDialog.dismiss();
                                        if (e == null) {
                                            mToast.setText("提交成功");
                                            mToast.show();
                                            mBtnTakeGoods.setText("已收货");
                                            mBtnTakeGoods.setEnabled(false);
                                        } else {
                                            mToast.setText("提交失败");
                                            mToast.show();
                                        }
                                    }
                                });

                            } else {
                                mProgressDialog.dismiss();
                                mToast.setText("提交失败");
                                mToast.show();
                            }
                        }
                    });
                } else {
                    mProgressDialog.dismiss();
                    mToast.setText("提交失败");
                    mToast.show();
                }
            }
        });
    }
}
