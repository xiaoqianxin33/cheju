package com.chinalooke.android.cheju.activity.salesman;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.UIutils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PolicyCalculateActivity extends AppCompatActivity {

    @Bind(R.id.lv_policy_check)
    ListView mLvPolicyCheck;
    @Bind(R.id.et_recommend_company)
    EditText mEtRecommendCompany;
    @Bind(R.id.et_pridict_price)
    EditText mEtPridictPrice;
    @Bind(R.id.btn_policy_check)
    Button mBtnPolicyCheck;
    private Policy mPolicy;
    private List<String> mStrings = new ArrayList<>();
    private String mPrice;
    private String mCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_caculate);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initView() {
        mLvPolicyCheck.setHeaderDividersEnabled(false);
        mLvPolicyCheck.setVerticalScrollBarEnabled(false);
        mLvPolicyCheck.setAdapter(new MyAdapt());
        UIutils.setListViewHeightBasedOnChildren(mLvPolicyCheck);
        mEtRecommendCompany.setText(mPolicy.getCompany());
    }

    private void initData() {
        mPolicy = (Policy) getIntent().getSerializableExtra("policy");
        String status = mPolicy.getStatus();
        if ("已算价".equals(status)) {
            mEtPridictPrice.setText(mPolicy.getPrice());
            mBtnPolicyCheck.setText("已提交");
            mBtnPolicyCheck.setEnabled(false);
        }
        mStrings.add(mPolicy.getObjectId());
        mStrings.add(mPolicy.getUserName());
        mStrings.add(mPolicy.getCity());
        mStrings.add(mPolicy.getPhone());
        mStrings.add(mPolicy.getIdNo());
        mStrings.add(mPolicy.getCarNo());
        mStrings.add(mPolicy.getRegDate());
        mStrings.add(mPolicy.getFrameNo());
        mStrings.add(mPolicy.getEngine());
        mStrings.add(mPolicy.getBrand());
        mStrings.add(mPolicy.getStatus());


    }

    @OnClick(R.id.btn_policy_check)
    public void onClick() {
        boolean checkMessage = checkMessage();

        if (checkMessage) {
            MyUtills.showSingerDialog(this, "提示", "确定提交吗？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    saveLeanCloud();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


        }

    }

    private void saveLeanCloud() {
        AVObject policy = AVObject.createWithoutData("Policy", mPolicy.getObjectId());

        policy.put("company", mCompany);
        policy.put("price", mPrice);
        policy.put("status", "已算价");
        policy.put("userid", AVUser.getCurrentUser().getObjectId());
        policy.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    MyUtills.showNorDialog(PolicyCalculateActivity.this, "提示", "提交成功");
                    mBtnPolicyCheck.setText("已提交");
                    mBtnPolicyCheck.setEnabled(false);
                } else {
                    MyUtills.showNorDialog(PolicyCalculateActivity.this, "提示", "提交失败，请检查网络");
                }
            }
        });

    }

    private boolean checkMessage() {

        boolean b = true;

        mPrice = mEtPridictPrice.getText().toString();
        if (TextUtils.isEmpty(mPrice)) {
            MyUtills.showToast(this, "请填写预估价格");
            b = false;
        }

        mCompany = mEtRecommendCompany.getText().toString();

        if (TextUtils.isEmpty(mCompany)) {
            MyUtills.showToast(this, "请填写推荐保险公司");
            b = false;
        }

        return b;
    }


    class MyAdapt extends BaseAdapter {
        @Override
        public int getCount() {
            return mStrings.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder ;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_policy_check_listview, null);
                viewHolder.mTvItemPolicListView = (TextView) convertView.findViewById(R.id.tv_item_polic_listview);
                viewHolder.mTvItemPolicyCheckListView = (TextView) convertView.findViewById(R.id.tv_item_policy_check_listview);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.mTvItemPolicListView.setText(Constant.policyListView[position]);
            viewHolder.mTvItemPolicyCheckListView.setText(mStrings.get(position));
            return convertView;
        }


    }

    class ViewHolder {
        TextView mTvItemPolicListView;
        TextView mTvItemPolicyCheckListView;
    }

    public void finish(View view) {
        finish();
    }
}