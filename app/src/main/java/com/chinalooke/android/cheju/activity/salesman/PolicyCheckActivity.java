package com.chinalooke.android.cheju.activity.salesman;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.Policy;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.view.SyListView;
import com.chinalooke.android.cheju.utills.LeanCloudTools;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.UIutils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PolicyCheckActivity extends AppCompatActivity {

    @Bind(R.id.btn_policy_check)
    Button mBtnPolicyCheck;
    @Bind(R.id.lv_check)
    SyListView mLvCheck;
    @Bind(R.id.et_check_policyid)
    EditText mEtCheckPolicyid;
    @Bind(R.id.et_pridict_price)
    EditText mEtPridictPrice;
    @Bind(R.id.iv_policy)
    PhotoView mIvPolicy;
    @Bind(R.id.bg)
    ImageView mBg;
    @Bind(R.id.img)
    PhotoView mPhotoView;
    @Bind(R.id.parent)
    FrameLayout mParent;
    @Bind(R.id.iv_policy_sy)
    PhotoView mIvPolicySy;
    private Policy mPolicy;
    private ArrayList<String> mStrings = new ArrayList<>();
    AlphaAnimation in = new AlphaAnimation(0, 1);
    AlphaAnimation out = new AlphaAnimation(1, 0);
    private Info mInfo;
    private Uri mUri;
    private String mImges;
    private String mBusinessimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_check);
        ButterKnife.bind(this);
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        in.setDuration(300);
        out.setDuration(300);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBg.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mPhotoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mIvPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInfo = mIvPolicy.getInfo();

                Picasso.with(getApplicationContext()).load(mUri).resize(1000, 1000).into(mPhotoView);
                mBg.startAnimation(in);
                mBg.setVisibility(View.VISIBLE);
                mParent.setVisibility(View.VISIBLE);
                mPhotoView.animaFrom(mInfo);
            }
        });

        mIvPolicySy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInfo = mIvPolicySy.getInfo();
                Picasso.with(getApplicationContext()).load(mUri).resize(1000, 1000).into(mPhotoView);
                mBg.startAnimation(in);
                mBg.setVisibility(View.VISIBLE);
                mParent.setVisibility(View.VISIBLE);
                mPhotoView.animaFrom(mInfo);
            }
        });

        mPhotoView.enable();


        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBg.startAnimation(out);
                mPhotoView.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mParent.setVisibility(View.GONE);
                    }
                });
            }
        });

    }

    private void initView() {
        mLvCheck.setHeaderDividersEnabled(false);
        mLvCheck.setVerticalScrollBarEnabled(false);
        mLvCheck.setAdapter(new MyAdapt());
        UIutils.setListViewHeightBasedOnChildren(mLvCheck);
        mImges = mPolicy.getForceimgs();
        mBusinessimage = mPolicy.getBusinessimage();
        mIvPolicy.disenable();
        mIvPolicy.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mIvPolicySy.disenable();
        mIvPolicySy.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (!TextUtils.isEmpty(mImges)) {
            mUri = Uri.parse(mImges);
            Picasso.with(this).load(mUri).into(mIvPolicy);
        }

        if (!TextUtils.isEmpty(mBusinessimage)) {
            Uri parse = Uri.parse(mBusinessimage);
            Picasso.with(this).load(parse).into(mIvPolicySy);
        }

    }

    private void initData() {
        mPolicy = (Policy) getIntent().getSerializableExtra("policy");
        String status = mPolicy.getStatus();
        if ("已完成".equals(status)) {
            mBtnPolicyCheck.setText("已完成审核");
            mBtnPolicyCheck.setEnabled(false);
        }
        mStrings.add(mPolicy.getObjectId());
        mStrings.add(mPolicy.getUserName());
        mStrings.add(mPolicy.getCity());
        mStrings.add(mPolicy.getPhone());
        mStrings.add(mPolicy.getIdNo());
        mStrings.add(mPolicy.getCarNo());
        mStrings.add(mPolicy.getRegDate());
        mStrings.add(mPolicy.getFrameNo());
        mStrings.add(mPolicy.getEngine());
        mStrings.add(mPolicy.getBrand());
        mStrings.add(mPolicy.getStatus());
        mStrings.add(mPolicy.getCompany());
        mStrings.add(mPolicy.getPrice());
    }

    @OnClick(R.id.btn_policy_check)
    public void onClick() {

        boolean checkMessage = checkMessage();
        if (checkMessage) {

            MyUtills.showSingerDialog(this, "提示", "确认保单通过审核吗", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveLeanCloud();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
    }

    private void saveLeanCloud() {
        LeanCloudTools.addAttr(mPolicy.getObjectId(), "Policy", "status", "已完成");
        mBtnPolicyCheck.setText("已完成审核");
        mBtnPolicyCheck.setEnabled(false);
    }

    private boolean checkMessage() {

        boolean b = true;
        String company = mEtPridictPrice.getText().toString();

        if (TextUtils.isEmpty(company)) {
            b = false;
            MyUtills.showToast(getApplicationContext(), "请录入保险公司名");
        }

        String policyID = mEtCheckPolicyid.getText().toString();

        if (TextUtils.isEmpty(policyID)) {
            b = false;
            MyUtills.showToast(getApplicationContext(), "请录入保单号");
        } else if (!mPolicy.getObjectId().equals(policyID)) {
            b = false;
            MyUtills.showToast(getApplicationContext(), "请录入正确的保单号");
        }

        return b;


    }

    @OnClick({R.id.btn_ninety, R.id.btn_eighty, R.id.fl_check})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ninety:
                rotate(90);
                break;
            case R.id.btn_eighty:
                rotate(180);
                break;
            case R.id.fl_check:
                finish();
                break;
        }
    }

    private void rotate(int i) {
        BitmapDrawable drawable = (BitmapDrawable) mPhotoView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Matrix matrix = new Matrix();
        matrix.postRotate(i);
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        mPhotoView.setImageBitmap(rotated);
    }

    class MyAdapt extends BaseAdapter {
        @Override
        public int getCount() {
            return mStrings.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder ;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_policy_check_listview, null);
                viewHolder.mTvItemPolicListView = (TextView) convertView.findViewById(R.id.tv_item_polic_listview);
                viewHolder.mTvItemPolicyCheckListView = (TextView) convertView.findViewById(R.id.tv_item_policy_check_listview);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.mTvItemPolicListView.setText(Constant.policyCheckListView[position]);
            viewHolder.mTvItemPolicyCheckListView.setText(mStrings.get(position));
            return convertView;
        }


    }

    class ViewHolder {
        TextView mTvItemPolicListView;
        TextView mTvItemPolicyCheckListView;
    }

    @Override
    public void onBackPressed() {
        if (mParent.getVisibility() == View.VISIBLE) {
            mBg.startAnimation(out);
            mPhotoView.animaTo(mInfo, new Runnable() {
                @Override
                public void run() {
                    mParent.setVisibility(View.GONE);
                }
            });
        } else {
            super.onBackPressed();
        }
    }
}
