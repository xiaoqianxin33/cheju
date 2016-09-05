package com.chinalooke.android.cheju.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.BusinessShop;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.utills.PreferenceUtils;
import com.chinalooke.android.cheju.view.XListView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class YouhuiJuanActivity extends AppCompatActivity implements AMapLocationListener, AdapterView.OnItemClickListener, XListView.IXListViewListener {

    @Bind(R.id.tv_location_youhui)
    TextView mTvLocationYouhui;
    @Bind(R.id.lv_youhui)
    XListView mListView;
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    @Bind(R.id.scrollview)
    ScrollView mScrollview;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    private double mLongitude;
    private double mLatitude;
    private List<AVObject> mNearbyShops = new ArrayList<>();
    private AVGeoPoint mPoint;
    private DecimalFormat mDecimalFormat;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mNearbyShops.clear();
                initData();
            }
        }
    };
    private Toast mToast;
    private MyAdapt mMyAdapt;
    private long t1 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youhui_juan);
        ButterKnife.bind(this);
        mDecimalFormat = new DecimalFormat("0.0");
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
//        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(getTime());
        mMyAdapt = new MyAdapt();
        mListView.setAdapter(mMyAdapt);
        mListView.setOnItemClickListener(YouhuiJuanActivity.this);

        String location = PreferenceUtils.getPrefString(getApplicationContext(), "location", "");

        if (TextUtils.isEmpty(location)) {
            location();
        } else {
            String[] split = location.split(":");
            mLongitude = Double.parseDouble(split[0]);
            mLatitude = Double.parseDouble(split[1]);
            mTvLocationYouhui.setText(PreferenceUtils.getPrefString(this, "city", ""));
            mHandler.sendEmptyMessage(1);
        }

    }


    private void initData() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {

            AVQuery<AVObject> query = new AVQuery<>("BusinessShop");
            mPoint = new AVGeoPoint(mLatitude, mLongitude);
            query.limit(10);
            query.whereNear("location", mPoint);
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        if (list != null && list.size() != 0) {
                            mScrollview.setVisibility(View.VISIBLE);
                            mTvNone.setVisibility(View.GONE);
                            mNearbyShops.addAll(list);
                            mMyAdapt.notifyDataSetChanged();
                        } else {
                            mScrollview.setVisibility(View.GONE);
                            mTvNone.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mScrollview.setVisibility(View.GONE);
                        mTvNone.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            mScrollview.setVisibility(View.GONE);
            mTvNone.setText("网络错误");
            mTvNone.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                mTvLocationYouhui.setText("当前: " + aMapLocation.getCity());
                mLongitude = aMapLocation.getLongitude();
                mLatitude = aMapLocation.getLatitude();

                if (mLatitude != 0 && mLongitude != 0) {
                    PreferenceUtils.setPrefString(getApplicationContext(), "location", mLongitude + ":" + mLatitude);
                    PreferenceUtils.setPrefString(getApplicationContext(), "city", "当前: " + aMapLocation.getCity());

                    mHandler.sendEmptyMessage(1);
                }
            } else {
                mToast.setText("无法获取位置信息,请检查网络");
                mToast.show();
                mScrollview.setVisibility(View.GONE);
                mTvNone.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        AVObject avObject = mNearbyShops.get(position);

        Intent intent = new Intent();

        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;

        intent.putExtra(Constant.START_LOCATION, startingLocation);

        String shopGoods = avObject.getString("ShopGoods");


        BusinessShop businessShop = new BusinessShop();

        businessShop.setShopName(avObject.getString("ShopName"));
        businessShop.setShopPhone(avObject.getString("ShopPhone"));
        businessShop.setExpire(avObject.getDate("expire").toString());
        businessShop.setShopAddress(avObject.getString("ShopAddress"));
        Bundle bundle = new Bundle();
        bundle.putSerializable("shop", businessShop);
        intent.putExtras(bundle);
        intent.putExtra("shopGoods", shopGoods);

        intent.setClass(this, ShopActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    @OnClick({R.id.iv_wirte_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wirte_back:
                finish();
                break;

        }
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNearbyShops.clear();
                initData();
                onLoad();
            }
        }, 200);
    }

    @Override
    public void onLoadMore() {
        mListView.dropFooter();
    }

    private class MyAdapt extends BaseAdapter {
        @Override
        public int getCount() {

            if (mNearbyShops != null && mNearbyShops.size() != 0) {

                return mNearbyShops.size();
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
                convertView = View.inflate(YouhuiJuanActivity.this, R.layout.item_youhui_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            setDetail(viewHolder, position);
            return convertView;
        }


    }

    private void setDetail(ViewHolder viewHolder, int position) {
        AVObject avObject = mNearbyShops.get(position);

        viewHolder.mTvShopnameYouhui.setText(avObject.getString("ShopName"));
        viewHolder.mTvDiscountYouhui.setText("<" + mDecimalFormat.format(avObject.getAVGeoPoint("location").distanceInMilesTo(mPoint)) + "m");
        viewHolder.mTvLocationYouhuiListview.setText(avObject.getString("ShopAddress"));
        viewHolder.mTvDianshuYouhui.setText(avObject.getString("ShopPhone"));

    }


    class ViewHolder {
        @Bind(R.id.tv_shopname_youhui)
        TextView mTvShopnameYouhui;
        @Bind(R.id.tv_location_youhui_listview)
        TextView mTvLocationYouhuiListview;
        @Bind(R.id.tv_dianshu_youhui)
        TextView mTvDianshuYouhui;
        @Bind(R.id.tv_discount_youhui)
        TextView mTvDiscountYouhui;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }


    private void location() {

        mLocationClient = new AMapLocationClient(getApplicationContext());

        mLocationClient.setLocationListener(this);

        mLocationOption = new AMapLocationClientOption();

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mLocationOption.setInterval(5000);

        mLocationOption.setWifiActiveScan(false);

        mLocationOption.setOnceLocationLatest(true);

        mLocationClient.setLocationOption(mLocationOption);

        mLocationClient.startLocation();

    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(getTime());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            mListView.autoRefresh();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNearbyShops.clear();
    }
}
