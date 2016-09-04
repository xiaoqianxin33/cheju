package com.chinalooke.android.cheju.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Address;
import com.chinalooke.android.cheju.view.SyListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectAddressActivity extends AppCompatActivity {

    @Bind(R.id.lv_guanli)
    SyListView mLvGuanli;
    private List<AVObject> mMAddresses;
    private MyAdapt mMyAdapt;
    private Address mAddress;
    private AVUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);
        ButterKnife.bind(this);
        mAddress = new Address();
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        mLvGuanli.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AVObject avObject = mMAddresses.get(position);
                mAddress.setAddress(avObject.getString("address"));
                mAddress.setName(avObject.getString("name"));
                mAddress.setPhone(avObject.getString("phone"));
                mAddress.setObjectId(avObject.getObjectId());
                Bundle bundle = new Bundle();
                bundle.putSerializable("select", mAddress);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(1, intent);
                finish();
            }
        });
    }

    private void initView() {
        mMyAdapt = new MyAdapt();
        mLvGuanli.setAdapter(mMyAdapt);
    }

    private void initData() {
        mMAddresses = (List<AVObject>) getIntent().getSerializableExtra("addresses");
    }

    @OnClick({R.id.iv_address_back, R.id.fl_address_back, R.id.tv_guanli})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_address_back:
                finish();
                break;
            case R.id.fl_address_back:
                finish();
                break;
            case R.id.tv_guanli:
                Intent intent = new Intent(SelectAddressActivity.this, AddressActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentUser = AVUser.getCurrentUser();
        AVRelation<AVObject> relation = mCurrentUser.getRelation("address");
        AVQuery<AVObject> query = relation.getQuery();
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mMAddresses = list;
                    if (mMyAdapt != null)
                        mMyAdapt.notifyDataSetChanged();
                }
            }
        });

    }

    class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            if (mMAddresses != null) {
                return mMAddresses.size();
            } else {
                return 0;
            }
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
                convertView = View.inflate(SelectAddressActivity.this, R.layout.item_selectaddress_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            AVObject avObject = mMAddresses.get(position);
            viewHolder.mTvAddress.setText(avObject.getString("address"));
            viewHolder.mTvName.setText(avObject.getString("name"));
            viewHolder.mTvPhone.setText(avObject.getString("phone"));
            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_phone)
        TextView mTvPhone;
        @Bind(R.id.tv_address)
        TextView mTvAddress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
