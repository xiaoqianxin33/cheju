package com.chinalooke.android.cheju.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.view.XListView;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TakePhotoActivity extends AppCompatActivity {


    @Bind(R.id.xlv_detail)
    XListView mXlvDetail;
    @Bind(R.id.ll_policy_detail)
    LinearLayout mLlPolicyDetail;
    private Policy mPolicy;
    private ArrayList<String> mStrings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mPolicy = (Policy) getIntent().getSerializableExtra("policy");

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
            return null;
        }
    }
}