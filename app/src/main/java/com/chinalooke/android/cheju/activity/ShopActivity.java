package com.chinalooke.android.cheju.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.BusinessShop;
import com.chinalooke.android.cheju.bean.Goods;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.chinalooke.android.cheju.view.RevealBackgroundView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
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

    @Bind(R.id.tv_shop_address)
    TextView mTvShopAddress;
    @Bind(R.id.lv_gods)
    ListView mLvGods;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.tv_nogoods)
    TextView mTvNoGoods;
    @Bind(R.id.pb_load)
    ProgressBar mPb;
    @Bind(R.id.rl_phone)
    RelativeLayout mRlPhone;
    @Bind(R.id.tv_shop_name)
    TextView mTvShopName;
    @Bind(R.id.iv_collect)
    ImageView mIvCollect;
    private HashMap<AVObject, String> mAVObjectStringHashMap = new HashMap<>();
    private List<AVObject> mGood = new ArrayList<>();
    private List<AVObject> mGod = new ArrayList<>();
    private BusinessShop mShop;
    private ArrayList<String> mStrings = new ArrayList<>();

    private Dialog mDialog;
    private Goods mGoods;
    private AVObject mAvObject;
    private AVObject mShop1;
    private MyAdapt mMyAdapt;
    private Toast mToast;
    private boolean isCollect = false;
    private AVUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        ButterKnife.bind(this);
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
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
        mTvNoGoods.setVisibility(View.GONE);
        mCurrentUser = AVUser.getCurrentUser();
        initData();
        initView();
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
        mTvShopName.setText(mShop1.getString("ShopName"));
        mTvShopAddress.setText(mShop1.getString("ShopAddress"));
        mCollapsingToolbarLayout.setTitle(mShop1.getString("ShopName"));
        mMyAdapt = new MyAdapt();
        mLvGods.setAdapter(mMyAdapt);
        mLvGods.setOnItemClickListener(this);
        Picasso.with(this).load(mShop1.getAVFile("images").getUrl())
                .resize(MyUtills.Dp2Px(this, 200), MyUtills.Dp2Px(this, 200)).centerCrop()
                .placeholder(R.mipmap.placeholder).into(mIv);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            mAvObject = mGood.get(position);
            Intent intent = new Intent(this, GodsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("goods", mAvObject);
            intent.putExtra("shapid", mShop1.getObjectId());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return mGood.size();
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

    private void setDetails(final ViewHolder viewHolder, int positon) {
        final AVObject avObject = mGood.get(positon);
        AVRelation<AVObject> images = avObject.getRelation("images");
        AVQuery<AVObject> query = images.getQuery();
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        AVObject avObject1 = list.get(0);
                        String url = avObject1.getString("url");
                        Picasso.with(ShopActivity.this).load(url).resize(
                                MyUtills.Dp2Px(getApplicationContext(), 100), MyUtills.Dp2Px(getApplicationContext(), 100)
                        ).centerCrop().placeholder(R.mipmap.placeholder).into(viewHolder.mIvGods);
                    }
                }
            }
        });
        viewHolder.mTvCurrent.setText("¥" + avObject.getString("currentPrice"));
        viewHolder.mTvGodsTitle.setText(avObject.getString("name"));
        viewHolder.mTvGodsScore.setText("点劵:" + avObject.getString("score"));
        viewHolder.mTvPrice.setText("¥" + avObject.getString("price"));
        viewHolder.mTvSales.setText("已售" + avObject.getString("sales"));
        viewHolder.mTvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }


    static class ViewHolder {
        @Bind(R.id.iv_gods)
        ImageView mIvGods;
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
        mShop1 = getIntent().getParcelableExtra("Shop");
        if (mShop1 == null) {
            mShop1 = getIntent().getParcelableExtra("shop");
        }
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            AVRelation<AVObject> goods = mShop1.getRelation("goods");
            AVQuery<AVObject> query = goods.getQuery();
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    mPb.setVisibility(View.GONE);
                    if (e == null) {
                        mGood = list;
                        if (list.size() == 0) {
                            mTvNoGoods.setVisibility(View.VISIBLE);
                        }
                        if (mMyAdapt != null)
                            mMyAdapt.notifyDataSetChanged();
                    }
                }
            });
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }
        mShop = (BusinessShop) getIntent().getSerializableExtra("shop");

        isCollec();
    }

    private void isCollec() {
        if (mCurrentUser != null) {
            AVObject todoFolder = AVObject.createWithoutData("_User", mCurrentUser.getObjectId());
            AVRelation<AVObject> relation = todoFolder.getRelation("collectShop");
            AVQuery<AVObject> query = relation.getQuery();
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        if (list.size() != 0) {
                            for (AVObject shop : list) {
                                String objectId = shop.getObjectId();
                                if (objectId.equals(mShop1.getObjectId())) {
                                    mIvCollect.setImageResource(R.mipmap.select);
                                    isCollect = true;
                                }
                            }
                        } else {
                            mIvCollect.setImageResource(R.mipmap.unselect);
                            isCollect = false;
                        }
                    }
                }
            });
        } else {
            mIvCollect.setImageResource(R.mipmap.unselect);
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

    @OnClick({R.id.iv_phone, R.id.rl_phone, R.id.iv_collect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_phone:
                callPhone();
                break;
            case R.id.iv_collect:
                if (mCurrentUser != null) {
                    if (isCollect) {
                        mIvCollect.setImageResource(R.mipmap.unselect);
                        isCollect = false;
                    } else {
                        mIvCollect.setImageResource(R.mipmap.select);
                        isCollect = true;
                    }
                    saveCollect();
                } else {
                    mToast.setText("登录用户才可收藏");
                    mToast.show();
                }
                break;
            case R.id.rl_phone:
                callPhone();
                break;
        }
    }

    private void saveCollect() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            AVRelation<AVObject> relation = mCurrentUser.getRelation("collectShop");// 新建一个 AVRelation
            if (isCollect) {
                relation.add(mShop1);
            } else {
                relation.remove(mShop1);
            }
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        if (isCollect) {
                            mToast.setText("收藏成功");
                        } else {
                            mToast.setText("取消收藏成功");
                        }
                        mToast.show();
                    } else {
                        if (isCollect) {
                            mToast.setText("收藏失败");
                        } else {
                            mToast.setText("取消收藏失败");
                        }
                        mToast.show();
                    }
                }
            });
        } else {
            mToast.setText("网络不可用，收藏失败");
            mToast.show();
        }
    }

    private void callPhone() {
        Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mShop1.getString("ShopPhone")));
        startActivity(intentPhone);
    }
}
