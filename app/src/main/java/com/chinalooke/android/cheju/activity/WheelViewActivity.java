package com.chinalooke.android.cheju.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.constant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

public class WheelViewActivity extends Activity implements OnWheelChangedListener {


    @Bind(R.id.id_province)
    WheelView mProvince;
    @Bind(R.id.id_city)
    WheelView mCity;
    @Bind(R.id.id_area)
    WheelView mArea;
    private JSONObject mJsonObj;
    private String[] mProvinceDatas;
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();
    private String mCurrentProviceName;
    private String mCurrentCityName;
    private String mCurrentAreaName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_view);
        ButterKnife.bind(this);

        initJsonData();
        initDatas();

        mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceDatas));
        // 添加change事件
        mProvince.addChangingListener(this);
        // 添加change事件
        mCity.addChangingListener(this);
        // 添加change事件
        mArea.addChangingListener(this);

        mProvince.setVisibleItems(5);
        mCity.setVisibleItems(5);
        mArea.setVisibleItems(5);
        updateCities();
        updateAreas();
    }

    private void updateAreas() {
        int pCurrent = mCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];

        String[] areas = mAreaDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
        mArea.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
        mArea.setCurrentItem(0);
    }

    private void updateCities() {
        int pCurrent = mProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        mCity.setCurrentItem(0);
        updateAreas();
    }

    private void initDatas() {

        try {
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            mProvinceDatas = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
                String province = jsonP.getString("p");// 省名字

                mProvinceDatas[i] = province;

                JSONArray jsonCs = null;
                try {

                    jsonCs = jsonP.getJSONArray("c");
                } catch (Exception e1) {
                    continue;
                }
                String[] mCitiesDatas = new String[jsonCs.length()];
                for (int j = 0; j < jsonCs.length(); j++) {
                    JSONObject jsonCity = jsonCs.getJSONObject(j);
                    String city = jsonCity.getString("n");// 市名字
                    mCitiesDatas[j] = city;
                    JSONArray jsonAreas = null;
                    try {

                        jsonAreas = jsonCity.getJSONArray("a");
                    } catch (Exception e) {
                        continue;
                    }

                    String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
                    for (int k = 0; k < jsonAreas.length(); k++) {
                        String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称
                        mAreasDatas[k] = area;
                    }
                    mAreaDatasMap.put(city, mAreasDatas);
                }

                mCitisDatasMap.put(province, mCitiesDatas);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonObj = null;

    }

    private void initJsonData() {

        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = getAssets().open("city.json");
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len, "gbk"));
            }
            is.close();
            mJsonObj = new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {

        if (wheel == mProvince) {
            updateCities();
        } else if (wheel == mCity) {
            updateAreas();
        } else if (wheel == mArea) {
            mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[newValue];
        }
    }


    public void showChoose(View view) {
        Intent intent = new Intent();
        intent.putExtra("location1", mCurrentProviceName + mCurrentCityName + mCurrentAreaName);
        if (!TextUtils.isEmpty(mCurrentCityName)) {
            String[] areas = mAreaDatasMap.get(mCurrentCityName);
            if (areas == null) {
                intent.putExtra("location", mCurrentProviceName);
            } else {

                intent.putExtra("location", mCurrentCityName);
            }
        }
        setResult(Constant.REQUSET, intent);

        finish();
    }

    @OnClick(R.id.tv_finish)
    public void onClick() {
        Intent intent = new Intent();
        intent.putExtra("location", "正在定位...");
        setResult(0, intent);
        finish();
    }
}

