package com.chinalooke.android.cheju.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.view.SyListView;

import java.util.ArrayList;

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
        mStrings.add("订单编号：");
        mStrings.add("支付方式：");
        mStrings.add("付款金额：");
        mStrings.add("订单创建时间：");
        mStrings.add("付款时间：");
        mStrings.add("送货时间：");
        mPrices.add("9282764458");
        mPrices.add("支付宝");
        mPrices.add("¥1300");
        mPrices.add("2016-09-02 20:38");
        mPrices.add("2016-09-02 20:38");
        mPrices.add("2016-09-02 20:38");
        mPolicy = (Policy) getIntent().getSerializableExtra("dpolicy");
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
