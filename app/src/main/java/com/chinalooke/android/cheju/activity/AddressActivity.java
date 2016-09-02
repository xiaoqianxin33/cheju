package com.chinalooke.android.cheju.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.fragment.AddAdressFragment;
import com.chinalooke.android.cheju.fragment.ShowAddressFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressActivity extends FragmentActivity {


    @Bind(R.id.btn_address)
    Button mBtnAddress;
    @Bind(R.id.tv_noaddress)
    TextView mTvNoaddress;
    //    @Bind(R.id.pb_address)
//    ProgressBar mPbAddress;
    private Fragment mContent;
    private FragmentManager mFragmentManager;
    private ShowAddressFragment mShowAddressFragment;
    private AddAdressFragment mAddAdressFragment;
    private List<AVObject> mAddresses = new ArrayList<>();
    private AVUser mCurrentUser;

    public ShowAddressFragment getShowAddressFragment() {
        return mShowAddressFragment;
    }

    public AddAdressFragment getAddAdressFragment() {
        return mAddAdressFragment;
    }

    public Button getBtnAddress() {
        return mBtnAddress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        mFragmentManager = getFragmentManager();
        mCurrentUser = AVUser.getCurrentUser();
        initData();
    }

    private void initData() {
        mShowAddressFragment = new ShowAddressFragment();
        mAddAdressFragment = new AddAdressFragment();
        AVObject todoFolder = AVObject.createWithoutData("_User", mCurrentUser.getObjectId());
        AVRelation<AVObject> relation = todoFolder.getRelation("address");
        AVQuery<AVObject> query = relation.getQuery();
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null && list.size() != 0) {
                    mAddresses = list;
                    mTvNoaddress.setVisibility(View.GONE);
//                    changeFragment(0);
                    FragmentTransaction transaction = mFragmentManager.beginTransaction();
                    transaction.add(R.id.fl_address, mShowAddressFragment).commit();
                } else {
                    mTvNoaddress.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    @OnClick({R.id.iv_address_back, R.id.fl_address_back, R.id.btn_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_address_back:
                finish();
                break;
            case R.id.fl_address_back:
                finish();
                break;
            case R.id.btn_address:
                mBtnAddress.setVisibility(View.GONE);
                changeFragment(1);
                break;
        }
    }

    public void changeFragment(int i) {
        switch (i) {
            case 1:
                switchContent(mShowAddressFragment, mAddAdressFragment);
                break;
            case 0:
                switchContent(mAddAdressFragment, mShowAddressFragment);
                break;
        }
    }

    public void switchContent(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fl_address, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    public List<AVObject> getAddresses() {
        return mAddresses;
    }
}
