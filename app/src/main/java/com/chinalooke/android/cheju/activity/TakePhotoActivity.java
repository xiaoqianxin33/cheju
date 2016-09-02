package com.chinalooke.android.cheju.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.view.PayDialog;
import com.chinalooke.android.cheju.view.XListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TakePhotoActivity extends AppCompatActivity {


    @Bind(R.id.xlv_detail)
    XListView mXlvDetail;
    @Bind(R.id.ll_policy_detail)
    LinearLayout mLlPolicyDetail;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.tv_discountprice)
    TextView mTvDiscountprice;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_phone)
    TextView mTvPhone;
    @Bind(R.id.tv_address)
    TextView mTvAddress;
    private Policy mPolicy;
    private ArrayList<String> mStrings = new ArrayList<>();
    private AVUser mCurrentUser;
    private List<AVObject> mAddresses;
    private MyAdapt mMyAdapt;
    private ArrayList<String> mPrices = new ArrayList<>();
    private String mAdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        mXlvDetail.setPullRefreshEnable(false);
        mXlvDetail.setPullLoadEnable(false);
        mMyAdapt = new MyAdapt();
        mXlvDetail.setAdapter(mMyAdapt);
//        mTvPrice.setText(mPolicy.getPrice());
//        mTvDiscountprice.setText(mPolicy.getDiscountPrice());
        mTvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void initData() {
        mStrings.add("商业险");
        mStrings.add("交强险");
        mPrices.add("1900");
        mPrices.add("800");
        mPolicy = (Policy) getIntent().getSerializableExtra("policy");
        mCurrentUser = AVUser.getCurrentUser();
        AVRelation<AVObject> relation = mCurrentUser.getRelation("address");
        AVQuery<AVObject> query = relation.getQuery();
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mAddresses = list;
                    initAddress();
                }
            }
        });
    }

    private void initAddress() {
        if (mAddresses != null && mAddresses.size() != 0) {
            AVObject avObject = mAddresses.get(0);
            mTvName.setText("收货人:" + avObject.getString("name"));
            mTvPhone.setText(avObject.getString("phone"));
            mTvAddress.setText("收货地址：" + avObject.getString("address"));
            mAdress = "收货地址：" + avObject.getString("address");
        } else {
            mTvAddress.setText("点击添加收货地址");
        }
    }


    @OnClick({R.id.iv_check_back, R.id.fl_takes, R.id.tv_topolicy, R.id.btn_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_check_back:
                finish();
                break;
            case R.id.fl_takes:
                finish();
                break;
            case R.id.tv_topolicy:
                Intent intent = new Intent();
                intent.setClass(this, WriteMessgeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("dpolicy", mPolicy);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.btn_pay:
                Intent intent1 = new Intent(TakePhotoActivity.this, PayActivity.class);
                intent1.putExtra("address", mAdress);
                startActivityForResult(intent1, 0);
                break;
        }
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
                convertView = View.inflate(getApplicationContext(), R.layout.item_pricedetail_listview, null);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            boolean statu = data.getBooleanExtra("statu", false);
            if (statu) {
//                PayDialog.Builder builder = new PayDialog.Builder(this);
//                builder.setTitle("系统提示");
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        startActivity(new Intent(TakePhotoActivity.this, OrderActivity.class));
//                    }
//                });
//
//                builder.setNegativeButton("支付遇到问题",
//                        new android.content.DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//
//                builder.create().show();
                MyUtills.showPayDialog(this, "系统提示", "已完成支付？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(TakePhotoActivity.this, OrderActivity.class));
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

            }
        }

    }
}