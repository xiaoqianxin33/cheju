package com.chinalooke.android.cheju.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.view.SyListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderActivity extends AppCompatActivity {

    @Bind(R.id.lv_order)
    SyListView mLvOrder;
    private ArrayList<String> mStrings = new ArrayList<>();
    private ArrayList<String> mPrices = new ArrayList<>();
    private MyAdapt mMyAdapt;
    private Policy mPolicy;
    private AVObject mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        mMyAdapt = new MyAdapt();
        mLvOrder.setAdapter(mMyAdapt);
    }

    private void initData() {
        mOrder = getIntent().getParcelableExtra("order");
        mPolicy = (Policy) getIntent().getSerializableExtra("dpolicy");
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
            mPrices.add("");
        }
        mPrices.add(mOrder.getNumber("price") + "");
        mPrices.add(getTime(mOrder.getDate("addDate")));
        Date sendDate = mOrder.getDate("sendDate");
        if (sendDate != null) {
            mPrices.add(getTime(sendDate));
        } else {
            mPrices.add("");
        }

        Number carriage = mOrder.getNumber("carriage");
        if (carriage != null) {
            mPrices.add(carriage + "");
        } else {
            mPrices.add("");
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


    @OnClick({R.id.iv_wirte_back, R.id.tv_title2, R.id.tv_topolicy_order})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_topolicy_order:
                Intent intent = new Intent();
                intent.setClass(OrderActivity.this, WriteMessgeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("dpolicy", mPolicy);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.iv_wirte_back:
                finish();
                break;
            case R.id.tv_title2:
                finish();
                break;
        }
    }
}
