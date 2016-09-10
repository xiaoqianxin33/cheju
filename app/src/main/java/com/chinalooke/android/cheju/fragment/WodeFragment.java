package com.chinalooke.android.cheju.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.AddressActivity;
import com.chinalooke.android.cheju.activity.CollectActivity;
import com.chinalooke.android.cheju.activity.CustomerActivity;
import com.chinalooke.android.cheju.activity.LoginActivity;
import com.chinalooke.android.cheju.activity.MainActivity;
import com.chinalooke.android.cheju.activity.PersonActivity;
import com.chinalooke.android.cheju.activity.ScoreActivity;
import com.chinalooke.android.cheju.activity.YouhuiJuanActivity;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.ImageTools;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
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
    @Bind(R.id.head)
    RoundedImageView mHead;
    @Bind(R.id.iv_qcord)
    ImageView mIvQcord;
    @Bind(R.id.iv_qcor)
    ImageView mIvQcor;
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
    private File mPhotoFile;
    private String mPhotoPath;
    private Bitmap mBitmap1;
    private Bitmap mPhotos;
    private ProgressDialog mProgressDialog;
    private Toast mToast;
    private List<AVUser> mAVUsers;
    private MainActivity mMainActivity;
    private AVUser mCurrentUser;
    private boolean isLogin = false;
    private List<AVUser> mArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wode, container, false);
        ButterKnife.bind(this, mView);
        mMainActivity = (MainActivity) getActivity();
        return mView;

    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser = AVUser.getCurrentUser();
        if (mCurrentUser != null) {
            isLogin = true;
            createQRcard();
            initData();
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
            initData();
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
        });
    }

    private void initData() {
        AVFile head = AVUser.getCurrentUser().getAVFile("head");
        if (head != null) {
            String url = head.getUrl();
            Picasso.with(getActivity()).load(url).placeholder(R.mipmap.zhanweitu).into(mHead);
        } else {
            Picasso.with(getActivity()).load(R.mipmap.zhanweitu).into(mHead);
        }
    }

    private void initView() {
        mLvWode.setAdapter(new MyAdapt());
        mTvUserphoneWode.setText(AVUser.getCurrentUser().getMobilePhoneNumber()
                .substring(0, 5) + "******");
    }

    @OnClick({R.id.iv_qcord, R.id.iv_qcor, R.id.tv_userphone_wode
            , R.id.iv_arrow, R.id.button_login, R.id.rl_wode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_userphone_wode:
                startActivity(new Intent(getActivity(), PersonActivity.class));
                break;
            case R.id.rl_wode:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.button_login:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.iv_arrow:
                startActivity(new Intent(getActivity(), PersonActivity.class));
                break;
            case R.id.head:
                showDialog();
                break;
            case R.id.iv_qcord:
                mIvQcor.setVisibility(View.VISIBLE);
                mIvQcor.setImageBitmap(mBitmap);
                break;
            case R.id.iv_qcor:
                mIvQcor.setVisibility(View.GONE);
                break;
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
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
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
            }
            return convertView;
        }
    }

    class ViewHolder {
        TextView mTextView;
        TextView mTextHong;
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

    private void showDialog() {

        Context dialogContext = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Light);

        String[] choiceItems = new String[2];
        choiceItems[0] = "相机拍摄";
        choiceItems[1] = "本地相册";

        ListAdapter adapter = new ArrayAdapter<String>(dialogContext, android.R.layout.simple_list_item_1, choiceItems);
        AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
        builder.setTitle("更换头像");


        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: //相机
                        getImageFromCamera();
                        break;
                    case 1: //从图库相册中选取
                        getImageFromAlbum();
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void getImageFromCamera() {

        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mPhotoFile = new File(Environment.getExternalStorageDirectory(), "image.jpg");

            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(mPhotoFile));

            startActivityForResult(intent, CAMERA_REQUEST_CODE_QZ);

        } else {
            Toast.makeText(getActivity(), "请确认已经插入SD卡",
                    Toast.LENGTH_LONG).show();
        }
    }


    public void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ALBUM_REQUEST_CODE_QZ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE_QZ) {
                String status = Environment.getExternalStorageState();
                if (status.equals(Environment.MEDIA_MOUNTED)) {
//                    Log.e("status", "status");
                    mBitmap1 = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpg");
                    if (mBitmap1 != null) {
                        mPhotoPath = Environment.getExternalStorageDirectory() + "/image.jpg";
                        try {
                            AVFile avFile = AVFile.parseFileWithAbsoluteLocalPath("image.jpg", Environment.getExternalStorageDirectory().toString());


                            saveHeadCloud(0, avFile, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (requestCode == ALBUM_REQUEST_CODE_QZ) {
                try {
                    Uri originalUri = data.getData();
                    AVFile avFile = AVFile.parseFileWithAbsoluteLocalPath("image.jpg",
                            ImageTools.getPath(getActivity(), originalUri));
                    if (originalUri == null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            mPhotos = (Bitmap) bundle.get("data");
                            Drawable drawable = new BitmapDrawable(mPhotos);
                            saveHeadCloud(1, avFile, null, mPhotos);
                        } else {
                            Toast.makeText(getActivity(), "err****", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Bitmap bitmapFromUri = ImageTools.getBitmapFromUri(originalUri, getActivity());
                        saveHeadCloud(1, avFile, originalUri, null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveHeadCloud(final int i, AVFile avFile, final Uri url, final Bitmap bitmap) {
        initDialog("正在更新头像");
        AVUser.getCurrentUser().put("head", avFile);
        AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    mToast.setText("头像更新成功");
                    mToast.show();
                    switch (i) {
                        case 0:
//                            Picasso.with(getActivity()).load(new File(mPhotoPath)).resize(100, 100).centerInside().into(mHead);
                            break;
                        case 1:
//                            Picasso.with(getActivity()).load(url).resize(100, 100).centerCrop().into(mHead);
                            break;
                        case 2:
//                            mHead.setImageBitmap(bitmap);
                            break;
                    }

                } else {
                    mToast.setText(e.getMessage());
                    mToast.show();
                }
            }
        });
    }

    private void initDialog(String message) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }


}