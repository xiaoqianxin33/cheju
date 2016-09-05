package com.chinalooke.android.cheju.fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.avos.avoscloud.AVUser;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.WriteMessgeActivity;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.utills.IDCardUtil;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.lljjcoder.citypickerview.widget.CityPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiao on 2016/8/6.
 */
@SuppressLint("ValidFragment")
public class WriteChezhuFragment extends Fragment implements AMapLocationListener {


    @Bind(R.id.lv_write)
    ListView mLvWrite;
    @Bind(R.id.write)
    Button mWrite;
    @Bind(R.id.tv_write)
    TextView mTvWrite;
    @Bind(R.id.tv_location)
    TextView mTvLocation;
    @Bind(R.id.et_phone_writechezhu)
    EditText mEtPhoneWritechezhu;
    @Bind(R.id.et_id_writechezhu)
    EditText mEtIdWritechezhu;
    @Bind(R.id.et_carnum_writechezhu)
    EditText mEtCarnumWritechezhu;
    @Bind(R.id.ll_time)
    LinearLayout mLlTime;
    @Bind(R.id.tv_line)
    TextView mTvLine;
    @Bind(R.id.tv_xian2)
    TextView mTvLine2;
    @Bind(R.id.tv_line4)
    TextView mTvLine4;
    @Bind(R.id.ll_company)
    LinearLayout mLlCompany;
    @Bind(R.id.ll_type)
    LinearLayout mLlType;
    @Bind(R.id.iv_location)
    ImageView mIvLocation;
    //    private Activity mActivity;
    private FragmentManager mFragmentManager;
    private String mLocationMessage = "正在定位...";
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    private Policy mPolicy;
    private ArrayList<JSONObject> mCityCarNumerArrayList = new ArrayList<>();
    private JSONArray mJsonArray;
    private int mCurrentFragment;
    private Toast mToast;
    private WriteMessgeActivity mActivity;


    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (1 == msg.what) {

                try {

                    for (int i = 0; i < mJsonArray.length(); i++) {
                        JSONObject jsonObject = mJsonArray.getJSONObject(i);
                        mCityCarNumerArrayList.add(jsonObject);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    };
    private boolean mDone;
    private AVUser mCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write, container, false);
        ButterKnife.bind(this, view);
        mPolicy = ((WriteMessgeActivity) getActivity()).getPolicy();
        mFragmentManager = getFragmentManager();
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        mCurrentUser = AVUser.getCurrentUser();
        initView();
        initData();
        location();

        return view;
    }

    private void initDone() {
        mDone = ((WriteMessgeActivity) getActivity()).isDone();
        if (mDone) {
            Policy dpolicy = ((WriteMessgeActivity) getActivity()).getDpolicy();
            mEtIdWritechezhu.setText(dpolicy.getIdNo());
            mEtIdWritechezhu.setEnabled(false);
            mEtCarnumWritechezhu.setText(dpolicy.getCarNo());
            mEtCarnumWritechezhu.setEnabled(false);
            mTvLocation.setText(dpolicy.getCity());
            mTvLocation.setEnabled(false);
            mEtPhoneWritechezhu.setText(dpolicy.getPhone());
            mEtPhoneWritechezhu.setEnabled(false);
            mWrite.setVisibility(View.GONE);
            mIvLocation.setVisibility(View.GONE);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (WriteMessgeActivity) getActivity();
        initEvent();
        initDone();
        mCurrentFragment = ((WriteMessgeActivity) getActivity()).getCurrentFragment();

    }

    private void initEvent() {
        mTvLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if ((!TextUtils.isEmpty(s)) && (!"正在定位...".equals(s.toString()))) {
                    for (int i = 0; i < mCityCarNumerArrayList.size(); i++) {

                        try {
                            String city = mCityCarNumerArrayList.get(i).getString("city");


                            if (s.toString().equals(city)) {
                                mEtCarnumWritechezhu.setText(mCityCarNumerArrayList.get(i).getString("code"));
                                break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }
            }
        });

        mEtPhoneWritechezhu.addTextChangedListener(new TextChange(0));
        mEtCarnumWritechezhu.addTextChangedListener(new TextWatcher() {
            int index = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mEtCarnumWritechezhu.removeTextChangedListener(this);//解除文字改变事件
                index = mEtCarnumWritechezhu.getSelectionStart();//获取光标位置
                mEtCarnumWritechezhu.setText(s.toString().toUpperCase());
                mEtCarnumWritechezhu.setSelection(index);//重新设置光标位置
                mEtCarnumWritechezhu.addTextChangedListener(this);//重新绑定事件
                mPolicy.setCarNo(s.toString());
            }
        });
        mEtIdWritechezhu.addTextChangedListener(new TextChange(2));
    }


    private class TextChange implements TextWatcher {
        private int mInt;

        public TextChange(int i) {
            this.mInt = i;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mPolicy.setCity(s.toString());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (mInt) {
                case 0:
                    mPolicy.setPhone(s.toString());
                    break;
                case 1:
//                    mEtCarnumWritechezhu.removeTextChangedListener();
                    mPolicy.setCarNo(s.toString());
                    break;
                case 2:
                    mPolicy.setIdNo(s.toString());
                    break;
                case 3:
                    mPolicy.setCity(s.toString());
                    break;
            }
        }
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager assetManager = getActivity().getAssets();

                try {
                    InputStream is = assetManager.open("car_city.json");

                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuffer stringBuffer = new StringBuffer();
                    String str;
                    while ((str = br.readLine()) != null) {
                        stringBuffer.append(str);
                    }
                    mJsonArray = new JSONArray(stringBuffer.toString());
                    mHandler.sendEmptyMessage(1);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void location() {

        mLocationClient = new AMapLocationClient(getActivity());

        mLocationClient.setLocationListener(this);

        mLocationOption = new AMapLocationClientOption();

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mLocationOption.setInterval(5000);

        mLocationOption.setWifiActiveScan(false);

        mLocationOption.setOnceLocationLatest(true);

        mLocationClient.setLocationOption(mLocationOption);

        mLocationClient.startLocation();

    }


    private void initView() {
        mWrite.setText("下一步");
        mLlCompany.setVisibility(View.GONE);
        mLlType.setVisibility(View.GONE);
        mTvLine.setVisibility(View.GONE);
        mTvLine2.setVisibility(View.GONE);
        mTvWrite.setVisibility(View.GONE);
        mLvWrite.setVisibility(View.GONE);
        mLlTime.setVisibility(View.GONE);
        mTvLine4.setVisibility(View.GONE);
        if (mCurrentUser != null) {
            mEtPhoneWritechezhu.setText(AVUser.getCurrentUser().getMobilePhoneNumber());
            mPolicy.setPhone(AVUser.getCurrentUser().getMobilePhoneNumber());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
    }


    public boolean checkWrite() {
        boolean boo;
        if (TextUtils.isEmpty(mPolicy.getPhone())) {
            mToast.setText("请输入手机号码");
            mEtPhoneWritechezhu.setFocusable(true);
            mEtPhoneWritechezhu.setFocusableInTouchMode(true);
            mEtPhoneWritechezhu.requestFocus();
            mToast.show();
            return false;
        }

        boo = MyUtills.CheckPhoneNumer(mPolicy.getPhone());
        if (!boo) {
            mToast.setText("请输入正确的手机号码");
            mEtPhoneWritechezhu.setFocusable(true);
            mEtPhoneWritechezhu.setFocusableInTouchMode(true);
            mEtPhoneWritechezhu.requestFocus();
            mToast.show();
            return boo;
        }

        if (TextUtils.isEmpty(mPolicy.getIdNo())) {
            mToast.setText("请输入身份证号码");
            mToast.show();
            mEtIdWritechezhu.setFocusable(true);
            mEtIdWritechezhu.setFocusableInTouchMode(true);
            mEtIdWritechezhu.requestFocus();
            return false;
        }


        boo = IDCardUtil.IDCardValidate(mPolicy.getIdNo());
        if (!boo) {
            mToast.setText("请输入正确的身份证号码");
            mToast.show();
            mEtIdWritechezhu.setFocusable(true);
            mEtIdWritechezhu.setFocusableInTouchMode(true);
            mEtIdWritechezhu.requestFocus();
            return boo;
        }

        if (TextUtils.isEmpty(mPolicy.getCarNo())) {
            mToast.setText("请输入车牌号码");
            mToast.show();
            mEtCarnumWritechezhu.setFocusable(true);
            mEtCarnumWritechezhu.setFocusableInTouchMode(true);
            mEtCarnumWritechezhu.requestFocus();
            return false;
        }

        boo = MyUtills.checkCarNumer(mPolicy.getCarNo());
        if (!boo) {
            mToast.setText("请输入正确的车牌号码");
            mEtCarnumWritechezhu.setFocusable(true);
            mEtCarnumWritechezhu.setFocusableInTouchMode(true);
            mEtCarnumWritechezhu.requestFocus();
            mToast.show();
            return boo;
        }


        String s = mTvLocation.getText().toString();

        if ("正在定位...".equals(s)) {
            mToast.setText("请选择城市");
            mToast.show();
            return false;
        }
        return boo;
    }


    @OnClick({R.id.tv_location, R.id.iv_location, R.id.write})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_location:
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                selectLocation();
                break;
            case R.id.iv_location:
                InputMethodManager imm2 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(view.getWindowToken(), 0);
                selectLocation();
                break;
            case R.id.write:
                writeNumer();
                boolean b = checkWrite();
                if (b) {
                    saveCloundMessage();
                    mActivity.changeStatu();
                    mActivity.switchContent(this, mActivity.getWriteCheliangFragment());
                    mCurrentFragment = 1;
                }
        }
    }


    private void selectLocation() {
        CityPickerView cityPickerView = new CityPickerView(getActivity());
        cityPickerView.setTextColor(Color.BLACK);
        cityPickerView.setTextSize(20);
        cityPickerView.setVisibleItems(5);
        cityPickerView.setIsCyclic(false);
        cityPickerView.show();

        cityPickerView.setOnCityItemClickListener(new CityPickerView.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                mTvLocation.setText(citySelected[1].substring(0, citySelected[0].length() - 1));
            }
        });
    }

    private void saveCloundMessage() {
        ((WriteMessgeActivity) getActivity()).setPolicy(mPolicy);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                String city = aMapLocation.getCity().substring(0, aMapLocation.getCity().length() - 1);
                if (!mDone)
                    mTvLocation.setText(city);
            } else {
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    public void writeNumer() {
        mPolicy.setPhone(mEtPhoneWritechezhu.getText().toString());
        mPolicy.setIdNo(mEtIdWritechezhu.getText().toString());
        mPolicy.setCarNo(mEtCarnumWritechezhu.getText().toString());
        mPolicy.setCity(mTvLocation.getText().toString());
    }


}
