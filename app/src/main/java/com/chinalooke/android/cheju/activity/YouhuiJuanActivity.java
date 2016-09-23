package com.chinalooke.android.cheju.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.BusinessShop;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.ImageTools;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.utills.PreferenceUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class YouhuiJuanActivity extends AppCompatActivity implements AMapLocationListener, AdapterView.OnItemClickListener {

    @Bind(R.id.tv_location_youhui)
    TextView mTvLocationYouhui;
    @Bind(R.id.lv_youhui)
    ListView mListView;
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    @Bind(R.id.scrollview)
    SwipeRefreshLayout mScrollview;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_score)
    TextView mTvScore;
    @Bind(R.id.spinner)
    Spinner mSpinner;
    private double mLongitude;
    private double mLatitude;
    private List<AVObject> mNearbyShops = new ArrayList<>();
    private AVGeoPoint mPoint;
    private DecimalFormat mDecimalFormat;
    private int mSkip;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mNearbyShops.clear();
            }
        }
    };
    private Toast mToast;
    private MyAdapt mMyAdapt;
    private long t1 = 0;
    private int mType;
    private boolean isLoading = false;
    private ProgressDialog mProgressDialog;
    private List<AVObject> good = new ArrayList<>();
    private Drawable mDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youhui_juan);
        ButterKnife.bind(this);

        mType = getIntent().getIntExtra("type", 0);

        mDecimalFormat = new DecimalFormat("0.0");
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

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
        initView();
        initEvent();

    }

    private void initView() {
        mDrawable = ImageTools.setDrwableSize2(this, R.mipmap.placeholder, 54, 54);

    }


    private void initEvent() {
        mScrollview.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mScrollview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSkip = 0;
                mNearbyShops.clear();
                initData();
                mScrollview.setRefreshing(false);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mListView != null && mListView.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    mScrollview.setEnabled(enable);
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        isLoading = true;
        initData();
    }


    private void initData() {
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            String score = currentUser.getNumber("score") + "";
            mTvScore.setText("可用积分:" + score);
        }

        if (NetUtil.is_Network_Available(getApplicationContext())) {

            AVQuery<AVObject> query = new AVQuery<>("BusinessShop");
            mPoint = new AVGeoPoint(mLatitude, mLongitude);
            Date now = new Date();
            query.whereLessThanOrEqualTo("createdAt", now);
            query.limit(8);
            query.skip(mSkip);
            query.whereNear("location", mPoint);
            switch (mType) {
                case 1:
                    query.whereEqualTo("type", 1);
                    mTvTitle.setText("特惠洗车");
                    break;
                case 2:
                    query.whereEqualTo("type", 2);
                    mTvTitle.setText("正品轮胎");
                    break;
                case 3:
                    query.whereEqualTo("type", 3);
                    mTvTitle.setText("道路救援");
                    break;
                case 4:
                    query.whereEqualTo("type", 4);
                    mTvTitle.setText("优质快修");
                    break;
            }
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        if (list != null && list.size() != 0) {
                            mScrollview.setVisibility(View.VISIBLE);
                            mTvNone.setVisibility(View.GONE);
                            mNearbyShops.addAll(list);
                            mMyAdapt.notifyDataSetChanged();
                            isLoading = false;
                            mSkip += 8;
                        }
                    } else {
                        if (mSkip == 0) {
                            mScrollview.setVisibility(View.GONE);
                            mTvNone.setVisibility(View.VISIBLE);
                        }
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

    private long lastClickTime = 0;


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        int MIN_CLICK_DELAY_TIME = 1000;
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;

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
            if (avObject.getDate("expire") != null)
                businessShop.setExpire(avObject.getDate("expire").toString());
            businessShop.setShopAddress(avObject.getString("ShopAddress"));
            Bundle bundle = new Bundle();
            bundle.putSerializable("shop", businessShop);
            bundle.putParcelable("Shop", avObject);
            intent.putExtras(bundle);
            intent.putExtra("shopGoods", shopGoods);

            intent.setClass(this, ShopActivity.class);
            startActivity(intent);
            this.overridePendingTransition(0, 0);
        }
    }

    @OnClick({R.id.iv_wirte_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wirte_back:
                finish();
                break;

        }

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
        AVFile images = avObject.getAVFile("images");
        if (images != null)
            Picasso.with(getApplicationContext()).load(images.getUrl()).resize(MyUtills.Dp2Px(getApplicationContext(), 120),
                    MyUtills.Dp2Px(getApplicationContext(), 100)).centerCrop().into(viewHolder.mIvShop);
        viewHolder.mTvShopnameYouhui.setText(avObject.getString("ShopName"));
        viewHolder.mTvDiscountYouhui.setText("<" + mDecimalFormat.format(avObject.getAVGeoPoint("location").distanceInMilesTo(mPoint)) + "m");
        viewHolder.mTvLocationYouhuiListview.setText(avObject.getString("ShopAddress"));
        viewHolder.mTvDianshuYouhui.setText(avObject.getString("descript"));

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
        @Bind(R.id.iv_shop)
        RoundedImageView mIvShop;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSkip = 0;
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

}
