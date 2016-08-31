package com.chinalooke.android.cheju.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.WriteMessgeActivity;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.IDCardUtil;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.PreferenceUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiao on 2016/8/6.
 */
public class WriteCheliangFragment extends Fragment {


    @Bind(R.id.lv_write)
    ListView mLvWrite;
    @Bind(R.id.write)
    Button mWrite;
    @Bind(R.id.ll_chezhu)
    LinearLayout mLlChezhu;
    @Bind(R.id.tv_xian)
    TextView mTvXian;
    @Bind(R.id.tv_location)
    TextView mTvLocation;
    @Bind(R.id.tv_write)
    TextView mTvWrite;
    @Bind(R.id.ll_item_write)
    LinearLayout mLlItemWrite;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.tv_company)
    TextView mTvCompany;
    @Bind(R.id.tv_type)
    TextView mTvType;
    @Bind(R.id.iv_date)
    ImageView mIvDate;
    @Bind(R.id.iv_company)
    ImageView mIvCompany;
    @Bind(R.id.iv_type)
    ImageView mIvType;
    private Policy mPolicy;

    private Toast mToast;
    private TimePickerView pvTime;
    private OptionsPickerView pvOptions;
    private ArrayList<String> mOption = new ArrayList<>();
    private ArrayList<String> mOptionType = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler();
    private Policy mDpolicy;
    private boolean mDone;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write, container, false);
        ButterKnife.bind(this, view);
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPolicy = ((WriteMessgeActivity) getActivity()).getPolicy();
        initDialog();
        initData();
        initView();
        initDone();

    }

    private void initDone() {
        mDone = ((WriteMessgeActivity) getActivity()).isDone();
        if (mDone) {
            mDpolicy = ((WriteMessgeActivity) getActivity()).getDpolicy();
            mWrite.setVisibility(View.GONE);
            mTvDate.setText(mDpolicy.getRegDate());
            mTvDate.setEnabled(false);
            mTvCompany.setText(mDpolicy.getCompany());
            mTvCompany.setEnabled(false);
            mTvType.setText(mDpolicy.getType());
            mTvType.setEnabled(false);
            mIvCompany.setVisibility(View.GONE);
            mIvType.setVisibility(View.GONE);
            mIvDate.setVisibility(View.GONE);
        }
    }


    private void initData() {
        AVQuery<AVObject> query = new AVQuery<>("Insurance");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list != null && list.size() != 0) {
                        for (AVObject avObject : list) {
                            mOption.add(avObject.getString("name"));
                        }
                    }
                } else {
                    mToast.setText("网络出错，请检查网络连接状态");
                    mToast.show();
                }
            }
        });

        AVQuery<AVObject> query2 = new AVQuery<>("Insurance_type");
        query2.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list != null && list.size() != 0) {
                        for (AVObject avObject : list) {
                            mOptionType.add(avObject.getString("name"));
                        }
                    }
                } else {
                    mToast.setText("网络出错，请检查网络连接状态");
                    mToast.show();
                }
            }
        });
    }

    private void initDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("保单提交中...");
        mProgressDialog.setCancelable(true);
    }

    private void initView() {
        mLlItemWrite.setVisibility(View.GONE);
        mLlChezhu.setVisibility(View.GONE);
        mTvXian.setVisibility(View.GONE);

        mWrite.setText("精准询价");
        mLvWrite.setAdapter(new MyAdapt());
        mTvDate.setText(MyUtills.getDate());
        if (!TextUtils.isEmpty(mPolicy.getCompany()))
            mTvCompany.setText(mPolicy.getCompany());
    }


    @OnClick({R.id.write, R.id.tv_date, R.id.iv_date, R.id.tv_company, R.id.iv_company
            , R.id.tv_type, R.id.iv_type})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_type:
                showOption(1);
                break;
            case R.id.iv_type:
                showOption(1);
                break;
            case R.id.tv_date:
                showDateDialog();
                break;
            case R.id.tv_company:
                showOption(0);
                break;
            case R.id.iv_company:
                showOption(0);
                break;
            case R.id.iv_date:
                showDateDialog();
                break;
            case R.id.write:
                setPolicy();
                boolean b = checkMessage();
                if (b) {
                    mProgressDialog.show();

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveCloud();
                        }
                    }, 500);
                }
                break;
        }
    }

    private void setPolicy() {
        mPolicy.setCompany(mTvCompany.getText().toString());
        mPolicy.setType(mTvType.getText().toString());
        mPolicy.setRegDate(mTvDate.getText().toString());
    }

    private void showOption(final int i) {
        pvOptions = new OptionsPickerView(getActivity());
        switch (i) {
            case 0:
                pvOptions.setPicker(mOption);
                pvOptions.setTitle("选择公司");
                break;
            case 1:
                pvOptions.setPicker(mOptionType);
                pvOptions.setTitle("选择险种");
                break;
        }

        pvOptions.setCyclic(false);
        pvOptions.setSelectOptions(1);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                switch (i) {
                    case 0:
                        mTvCompany.setText(mOption.get(options1));
                        break;
                    case 1:
                        mTvType.setText(mOptionType.get(options1));
                        break;
                }
            }
        });
        pvOptions.show();
    }

    private void showDateDialog() {
        pvTime = new TimePickerView(getActivity(), TimePickerView.Type.YEAR_MONTH_DAY);

        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                mTvDate.setText(getTime(date).substring(0, getTime(date).length() - 5));
            }
        });
        pvTime.show();
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }


    private class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return Constant.writeCheliangListView.length;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHodler viewHodler = new ViewHodler();
            convertView = getActivity().getLayoutInflater().inflate(R.layout.item_write_listview, null);
            viewHodler.mEditText = (EditText) convertView.findViewById(R.id.et_item_write_listview);
            viewHodler.mTextView = (TextView) convertView.findViewById(R.id.tv_item_write_listview);
            viewHodler.mTextView.setText(Constant.writeCheliangListView[position]);

            if (mDone)
                setDetail(position, viewHodler);

            viewHodler.mEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    writeNumer(position, s.toString());
                }
            });
            return convertView;
        }
    }

    private void setDetail(int position, ViewHodler viewHodler) {
        switch (position) {
            case 0:
                viewHodler.mEditText.setText(mDpolicy.getUserName());
                break;
            case 1:
                viewHodler.mEditText.setText(mDpolicy.getFrameNo());
                break;
            case 2:
                viewHodler.mEditText.setText(mDpolicy.getEngine());
                break;
            case 3:
                viewHodler.mEditText.setText(mDpolicy.getBrand());
                break;
        }
        viewHodler.mEditText.setEnabled(false);
    }

    private void writeNumer(int position, String s) {
        switch (position) {
            case 0:
                mPolicy.setUserName(s);
                break;
            case 1:
                mPolicy.setFrameNo(s);
                break;
            case 2:
                mPolicy.setEngine(s);
                break;
            case 3:
                mPolicy.setBrand(s);
                break;
            case 4:
                mPolicy.setPolicyDate(s);
                break;

        }

    }

    class ViewHodler {
        TextView mTextView;
        EditText mEditText;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private boolean checkMessage() {
        if (TextUtils.isEmpty(mPolicy.getPhone())) {
            mToast.setText("请输入手机号码");
            mToast.show();
            return false;
        }

        if (!MyUtills.CheckPhoneNumer(mPolicy.getPhone())) {
            mToast.setText("请输入正确的手机号码");
            mToast.show();
            return false;
        }

        if (TextUtils.isEmpty(mPolicy.getIdNo())) {
            mToast.setText("请输入身份证号码");
            mToast.show();
            return false;
        }


        if (!IDCardUtil.IDCardValidate(mPolicy.getIdNo())) {
            mToast.setText("请输入正确的身份证号码");
            mToast.show();
            return false;
        }

        if (TextUtils.isEmpty(mPolicy.getCarNo())) {
            mToast.setText("请输入车牌号码");
            mToast.show();
            return false;
        }

        if (!MyUtills.checkCarNumer(mPolicy.getCarNo())) {
            mToast.setText("请输入正确的车牌号码");
            mToast.show();
            return false;
        }

//        String s = mTvLocation.getText().toString();

        if ("正在定位...".equals(mPolicy.getCity())) {
            mToast.setText("请选择城市");
            mToast.show();
            return false;
        }

        if (TextUtils.isEmpty(mPolicy.getUserName())) {
            mToast.setText("车主姓名不能为空");
            mToast.show();
            return false;
        }
        if (TextUtils.isEmpty(mPolicy.getCompany()) || "点击选择".equals(mPolicy.getCompany())) {
            mToast.setText("请选择保险公司");
            mToast.show();
            return false;
        }
        if (TextUtils.isEmpty(mPolicy.getType()) || "点击选择".equals(mPolicy.getType())) {
            mToast.setText("请选择险种");
            mToast.show();
            return false;
        }
        if (TextUtils.isEmpty(mPolicy.getRegDate())) {
            mToast.setText("请选择登记时间");
            mToast.show();
            return false;
        }
        if (TextUtils.isEmpty(mPolicy.getFrameNo())) {
            mToast.setText("车架号不能为空");
            mToast.show();
            return false;
        }
        if (TextUtils.isEmpty(mPolicy.getEngine())) {
            mToast.setText("发动机号不能为空");
            mToast.show();
            return false;
        }
        if (TextUtils.isEmpty(mPolicy.getBrand())) {
            mToast.setText("品牌型号不能为空");
            mToast.show();
            return false;
        }

        return true;
    }

    private void saveCloud() {

        AVObject policy = AVObject.createWithoutData("Policy", mPolicy.getObjectId());
        policy.put("userName", mPolicy.getUserName());
        policy.put("frameNo", mPolicy.getFrameNo());
        policy.put("engine", mPolicy.getEngine());
        policy.put("brand", mPolicy.getBrand());
        policy.put("policyDate", mPolicy.getPolicyDate());
        policy.put("userid", PreferenceUtils.getPrefString(getActivity(), "userObjectID", ""));
        policy.put("status", "待算价");
        policy.put("city", mPolicy.getCity());
        policy.put("phone", mPolicy.getPhone());
        policy.put("IdNo", mPolicy.getIdNo());
        policy.put("CarNo", mPolicy.getCarNo());
        policy.put("company", mPolicy.getCompany());
        policy.put("type", mPolicy.getType());
        policy.put("regDate", mPolicy.getRegDate());
        policy.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    MyUtills.showNorDialog(getActivity(), "提交成功！", "正在算价中，请在订单中查看详情");
                    mWrite.setText("订单已提交");
                    mWrite.setEnabled(false);
                } else {
                    mToast.setText("网络错误，提交失败");
                    mToast.show();
                }

            }
        });
        AVUser.getCurrentUser().put("addressName", mPolicy.getUserName());
        AVUser.getCurrentUser().put("IDNo", mPolicy.getIdNo());
        AVUser.getCurrentUser().put("phone", mPolicy.getPhone());
        AVUser.getCurrentUser().saveInBackground();

    }


}
