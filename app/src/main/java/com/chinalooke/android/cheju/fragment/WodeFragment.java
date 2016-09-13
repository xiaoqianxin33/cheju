package com.chinalooke.android.cheju.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.AddressActivity;
import com.chinalooke.android.cheju.activity.CollectActivity;
import com.chinalooke.android.cheju.activity.CustomerActivity;
import com.chinalooke.android.cheju.activity.LoginActivity;
import com.chinalooke.android.cheju.activity.MainActivity;
import com.chinalooke.android.cheju.activity.PersonActivity;
import com.chinalooke.android.cheju.activity.ScoreActivity;
import com.chinalooke.android.cheju.activity.UserQcodeActivity;
import com.chinalooke.android.cheju.activity.YouhuiJuanActivity;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.ImageTools;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by xiao on 2016/8/6.
 */
public class WodeFragment extends Fragment {


    private static final int CAMERA_REQUEST_CODE_QZ = 0;
    private static final int ALBUM_REQUEST_CODE_QZ = 1;
    @Bind(R.id.tv_userphone_wode)
    TextView mTvUserphoneWode;
    @Bind(R.id.lv_wode)
    ListView mLvWode;
    @Bind(R.id.iv_qcord)
    ImageView mIvQcord;
    @Bind(R.id.ll_wode)
    LinearLayout mLlWode;
    @Bind(R.id.rl_wode)
    RelativeLayout mRlWode;
    @Bind(R.id.button_login)
    Button mButtonLogin;
    @Bind(R.id.tv_blank)
    TextView mTvBlank;
    private View mView;
    private Bitmap mBitmap;
    private Toast mToast;


    private AVUser mCurrentUser;
    private boolean isLogin = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wode, container, false);
        ButterKnife.bind(this, mView);
        return mView;

    }


    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser = AVUser.getCurrentUser();
        if (mCurrentUser != null) {
            isLogin = true;
            createQRcard();
            initEvent();
            initView();
            mTvBlank.setVisibility(View.GONE);
            mLvWode.setVisibility(View.VISIBLE);
            mLlWode.setVisibility(View.VISIBLE);
            mButtonLogin.setVisibility(View.GONE);
            mRlWode.setVisibility(View.GONE);
        } else {
            mTvBlank.setVisibility(View.VISIBLE);
            mRlWode.setVisibility(View.VISIBLE);
            mLvWode.setVisibility(View.GONE);
            mLlWode.setVisibility(View.GONE);
            mButtonLogin.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurrentUser = AVUser.getCurrentUser();
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        if (mCurrentUser != null) {
            isLogin = true;
            createQRcard();
            initEvent();
            initView();
            mTvBlank.setVisibility(View.GONE);
            mLvWode.setVisibility(View.VISIBLE);
            mLlWode.setVisibility(View.VISIBLE);
            mButtonLogin.setVisibility(View.GONE);
            mRlWode.setVisibility(View.GONE);
        } else {
            mTvBlank.setVisibility(View.VISIBLE);
            mRlWode.setVisibility(View.VISIBLE);
            mLvWode.setVisibility(View.GONE);
            mLlWode.setVisibility(View.GONE);
            mButtonLogin.setVisibility(View.VISIBLE);
        }

    }

    private void initEvent() {
        mLvWode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    lastClickTime = currentTime;
                    switch (position) {
                        case 6:
                            MyUtills.showSingerDialog(getActivity(), "提示"
                                    , "确定登出吗?", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            AVUser.logOut();
                                            startActivity(new Intent(getActivity(), LoginActivity.class));
                                            getActivity().finish();
                                            if (NetUtil.is_Network_Available(getActivity())) {

                                                EMClient.getInstance().logout(true, new EMCallBack() {

                                                    @Override
                                                    public void onSuccess() {
//                                                    MyUtills.showToast(getActivity(), "退出成功");
                                                    }

                                                    @Override
                                                    public void onProgress(int progress, String status) {
                                                    }

                                                    @Override
                                                    public void onError(int code, String message) {
                                                        Log.e("onError", message);
                                                    }
                                                });
                                            } else {
                                                MyUtills.showToast(getActivity(), "退出成功");
                                            }


                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            break;

                        case 5:
                            showShare();

                            break;
                        case 4:
                            startActivity(new Intent(getActivity(), AddressActivity.class));
                            break;

                        case 3:
                            startActivity(new Intent(getActivity(), CustomerActivity.class));
                            break;

                        case 1:
                            startActivity(new Intent(getActivity(), ScoreActivity.class));
                            break;
                        case 2:
                            Intent intent = new Intent(getActivity(), YouhuiJuanActivity.class);
                            intent.putExtra("wode", "wode");
                            startActivity(intent);
                            break;
                        case 0:
                            startActivity(new Intent(getActivity(), CollectActivity.class));
                            break;
                    }
                }
            }
        });
    }


    private void initView() {
        mLvWode.setAdapter(new MyAdapt());
        mTvUserphoneWode.setText(AVUser.getCurrentUser().getMobilePhoneNumber()
                .substring(0, 5) + "******");
    }

    private int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;


    @OnClick({R.id.iv_qcord, R.id.tv_userphone_wode
            , R.id.iv_arrow, R.id.button_login, R.id.rl_wode})
    public void onClick(View view) {

        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;

            switch (view.getId()) {
//            case R.id.tv_userphone_wode:
//                startActivity(new Intent(getActivity(), PersonActivity.class));
//                break;
                case R.id.rl_wode:
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                case R.id.button_login:
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
//            case R.id.iv_arrow:
//                startActivity(new Intent(getActivity(), PersonActivity.class));
//                break;
                case R.id.iv_qcord:
                    startActivity(new Intent(getActivity(), UserQcodeActivity.class));
                    break;
            }
        }
    }

    private class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            if (isLogin) {
                return Constant.wodeListView.length;
            } else {
                return 1;
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_wode_listview, null);
                viewHolder = new ViewHolder();
                viewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_wode_listview);
                viewHolder.mTextHong = (TextView) convertView.findViewById(R.id.tv_hongzi);
                viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.iv_icon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Picasso.with(getActivity()).load(Constant.wodeListViewIcon[position]).into(viewHolder.mImageView);

            if (isLogin) {
                viewHolder.mTextView.setText(Constant.wodeListView[position]);
                if (position == 5) {
                    viewHolder.mTextHong.setText("推荐购险，打折返利");
                } else {
                    viewHolder.mTextHong.setText("");
                }
            } else {
                viewHolder.mTextView.setText("推荐车聚APP");
                viewHolder.mTextHong.setText("推荐购险，打折返利");
                viewHolder.mImageView.setImageResource(R.mipmap.recommend);
            }
            return convertView;
        }
    }


    class ViewHolder {
        TextView mTextView;
        TextView mTextHong;
        ImageView mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void createQRcard() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBitmap = QRCodeEncoder.syncEncodeQRCode(AVUser.getCurrentUser().getObjectId(),
                        MyUtills.Dp2Px(getActivity(), 300));
                ImageTools.saveBitmap(mBitmap, "qdcode.png");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvQcord.setImageBitmap(mBitmap);

                    }
                });
            }
        }).start();
    }


    private void showShare() {
        ShareSDK.initSDK(getActivity());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("车聚分享");

        // text是分享文本，所有平台都需要这个字段
        oks.setText("欢迎来车聚");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        Log.e("Environment", Environment.getExternalStorageDirectory().getPath() + "/arrow.png");

        oks.setImagePath(Environment.getExternalStorageDirectory().getPath() + "arrow.png");

        oks.setUrl("http://sharesdk.cn");


// 启动分享GUI
        oks.show(getActivity());
    }


}