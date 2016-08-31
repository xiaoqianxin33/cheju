package com.chinalooke.android.cheju.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.BusinessShop;
import com.chinalooke.android.cheju.bean.Goods;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.view.RevealBackgroundView;
import com.chinalooke.android.cheju.view.SquaredImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener, AdapterView.OnItemClickListener {


    @Bind(R.id.revealBackgroundView)
    RevealBackgroundView mRevealBackgroundView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.tv_shop_name)
    TextView mTvShopName;
    @Bind(R.id.tv_shop_address)
    TextView mTvShopAddress;
    @Bind(R.id.lv_gods)
    ListView mLvGods;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.rl_phone)
    RelativeLayout mRlPhone;
    private HashMap<AVObject, String> mAVObjectStringHashMap = new HashMap<>();
    private JSONArray mJsonArray;
    private List<AVObject> mGod = new ArrayList<>();
    private List<JSONObject> mGods = new ArrayList<>();
    private BusinessShop mShop;
    private ArrayList<String> mStrings = new ArrayList<>();
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    initView();
                    break;
                case 2:
                    mDialog.dismiss();
                    break;

            }

        }
    };
    private Dialog mDialog;
    private Goods mGoods;
    private AVObject mAvObject;

    private void initProDialog() {
        mDialog = new Dialog(this, R.style.LodingDialog);
        mDialog.setContentView(R.layout.dialog_loading);
        mDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        ButterKnife.bind(this);
        initProDialog();
        mAppBarLayout.setVisibility(View.INVISIBLE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mCollapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.light_toolbar));
        mCollapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.light_toolbar));
        setStatusBarColor(getResources().getColor(R.color.light_toolbar));
        setupRevealBackground(savedInstanceState);
        mGoods = new Goods();
        initData();
    }

    private void setupRevealBackground(Bundle savedInstanceState) {

        mRevealBackgroundView.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(Constant.START_LOCATION);
            mRevealBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mRevealBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mRevealBackgroundView.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            mRevealBackgroundView.setToFinishedFrame();
        }
    }

    private void initView() {
        mTvShopName.setText(mShop.getShopName());
        mTvShopAddress.setText(mShop.getShopAddress());
        mCollapsingToolbarLayout.setTitle(mShop.getShopName());
        mLvGods.setAdapter(new MyAdapt());
        mLvGods.setOnItemClickListener(this);
        String[] strings = new String[mStrings.size()];
        for (int i = 0; i < mStrings.size(); i++) {
            strings[i] = mStrings.get(i);
        }

        Picasso.with(this).load(strings[0])
                .resize(MyUtills.Dp2Px(this, 200), MyUtills.Dp2Px(this, 200)).centerCrop().into(mIv);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAvObject = mGod.get(position);
        mGoods.setCurrentPrice(mAvObject.getString("currentPrice"));
        mGoods.setDescript(mAvObject.getString("descript"));
        mGoods.setGrade(mAvObject.getString("grade"));
        mGoods.setMark(mAvObject.getString("mark"));
        mGoods.setTitle(mAvObject.getString("name"));
        mGoods.setSales(mAvObject.getString("sales"));
        mGoods.setScore(mAvObject.getString("score"));
        mGoods.setImages(mAvObject.getString("images"));
        mGoods.setPrice(mAvObject.getString("price"));
        Intent intent = new Intent(this, GodsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("goods", mGoods);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return mGod.size();
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
                convertView = View.inflate(ShopActivity.this, R.layout.item_gods_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            setDetails(viewHolder, position);

            return convertView;
        }


    }

    private void setDetails(ViewHolder viewHolder, int positon) {
        AVObject avObject = mGod.get(positon);
        String s = mAVObjectStringHashMap.get(avObject);
        viewHolder.mTvCurrent.setText("¥" + avObject.getString("currentPrice"));
        viewHolder.mTvGodsTitle.setText(avObject.getString("name"));
        viewHolder.mTvGodsScore.setText("点劵:" + avObject.getString("score"));
        viewHolder.mTvPrice.setText("¥" + avObject.getString("price"));
        viewHolder.mTvSales.setText("已售" + avObject.getString("sales"));
        viewHolder.mTvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        Picasso.with(this).load(s).resize(MyUtills.Dp2Px(this, 80), MyUtills.Dp2Px(this, 80)).centerCrop().into(viewHolder.mIvGods);
        if (positon == mGod.size() - 1) {
            mHandler.sendEmptyMessage(2);
        }
    }


    static class ViewHolder {
        @Bind(R.id.iv_gods)
        SquaredImageView mIvGods;
        @Bind(R.id.tv_gods_title)
        TextView mTvGodsTitle;
        @Bind(R.id.tv_gods_score)
        TextView mTvGodsScore;
        @Bind(R.id.tv_current)
        TextView mTvCurrent;
        @Bind(R.id.tv_price)
        TextView mTvPrice;
        @Bind(R.id.tv_sales)
        TextView mTvSales;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void initData() {
        String shopGoods = getIntent().getExtras().getString("shopGoods");


        mShop = (BusinessShop) getIntent().getSerializableExtra("shop");


        if (shopGoods != null) {
            try {
                mJsonArray = new JSONArray(shopGoods);
                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject jsonObject = mJsonArray.getJSONObject(i);

                    mGods.add(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            mDialog.dismiss();
        }


        for (int i = 0; i < mGods.size(); i++) {
            JSONObject jsonObject = mGods.get(i);
            try {
                String id = jsonObject.getString("id");
                AVQuery<AVObject> query = new AVQuery<>("Goods");
                query.whereEqualTo("objectId", id);
                final int finalI = i;
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (list != null && list.size() > 0) {
                            AVObject avObject = list.get(0);

                            mGod.add(avObject);
                            String images = avObject.getString("images");

                            if (!TextUtils.isEmpty(images)) {
                                try {
                                    JSONArray jsonArray = new JSONArray(images);
                                    for (int y = 0; y < jsonArray.length(); y++) {
                                        JSONObject o = jsonArray.getJSONObject(y);
                                        String image = o.getString("id");
                                        mStrings.add(image);

                                        mAVObjectStringHashMap.put(avObject, image);
                                        if (finalI == mGods.size() - 1)
                                            mHandler.sendEmptyMessage(1);
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            mAppBarLayout.setVisibility(View.VISIBLE);
            setStatusBarColor(Color.TRANSPARENT);
        }
    }


    @TargetApi(21)
    private void setStatusBarColor(int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.slide_out_to_left_from_right);

    }

    @OnClick({R.id.iv_phone, R.id.rl_phone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_phone:
                callPhone();
                break;
            case R.id.rl_phone:
                callPhone();
                break;
        }
    }

    private void callPhone() {
        Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mShop.getShopPhone()));
        startActivity(intentPhone);
    }
}
