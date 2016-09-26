package com.chinalooke.android.cheju.activity.business;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.bigkoo.pickerview.OptionsPickerView;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.config.MyLeanCloudApp;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ReleaseGoodsActivity extends AppCompatActivity implements BGASortableNinePhotoLayout.Delegate, EasyPermissions.PermissionCallbacks {

    @Bind(R.id.snpl_moment_add_photos)
    BGASortableNinePhotoLayout mPhotosSnpl;
    private static final int REQUEST_CODE_PHOTO_PREVIEW = 2;
    private static final int REQUEST_CODE_PERMISSION_PHOTO_PICKER = 1;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    @Bind(R.id.et_title)
    EditText mEtTitle;
    @Bind(R.id.et_descript)
    EditText mEtDescript;
    @Bind(R.id.et_price)
    EditText mEtPrice;
    @Bind(R.id.et_currentPrice)
    EditText mEtCurrentPrice;
    @Bind(R.id.et_score)
    EditText mEtScore;
    @Bind(R.id.et_mark)
    EditText mEtMark;
    @Bind(R.id.release)
    Button mRelease;
    @Bind(R.id.tv_type)
    TextView mTvType;
    private String mTitle;
    private Toast mToast;
    private String mDescript;
    private ArrayList<String> mPhotosSnplData;
    private String mPrice;
    private String mCurrentPrice;
    private String mSroce;
    private String mType;
    private List<String> mTypeList = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private AVObject mShop;
    private ArrayList<AVObject> mTypes = new ArrayList<>();
    private int mCount = 0;
    private int mTypeChose = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_goods);
        ButterKnife.bind(this);
        mToast = MyLeanCloudApp.getToast();
        mShop = MyLeanCloudApp.getAVObject();
        initData();
        initEvent();
    }

    private void initData() {
        int shopType = getIntent().getIntExtra("shopType", -1);
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            AVQuery<AVObject> query = new AVQuery<>("GoodsType");
            query.whereEqualTo("shopType", shopType);
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        for (AVObject avObject : list) {
                            String name = avObject.getString("name");
                            mTypes.add(avObject);
                            mTypeList.add(name);
                        }
                    }
                }
            });
        } else {
            mToast.setText("网络不可用，无法获取分类信息");
            mToast.show();
        }
    }

    private void initEvent() {
        mPhotosSnpl.init(this);
        mPhotosSnpl.setDelegate(this);
    }


    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
        choicePhotoWrapper();
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        mPhotosSnpl.removeItem(position);
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent(this, mPhotosSnpl.getMaxItemCount(), models, models, position, false), REQUEST_CODE_PHOTO_PREVIEW);
    }


    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_PHOTO_PICKER)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");

            startActivityForResult(BGAPhotoPickerActivity.newIntent(this, takePhotoDir, mPhotosSnpl.getMaxItemCount(), mPhotosSnpl.getData(), true), REQUEST_CODE_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片", REQUEST_CODE_PERMISSION_PHOTO_PICKER, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_CODE_PERMISSION_PHOTO_PICKER) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            mPhotosSnpl.setData(BGAPhotoPickerActivity.getSelectedImages(data));
        } else if (requestCode == REQUEST_CODE_PHOTO_PREVIEW) {
            mPhotosSnpl.setData(BGAPhotoPickerPreviewActivity.getSelectedImages(data));
        }

    }

    @OnClick({R.id.iv_wirte_back, R.id.tv_type, R.id.iv_type, R.id.release})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wirte_back:
                finish();
                break;
            case R.id.tv_type:
                InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(view.getWindowToken(), 0);
                showOption();
                break;
            case R.id.iv_type:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                showOption();
                break;
            case R.id.release:
                mRelease.setEnabled(false);
                boolean b = checkInput();
                if (b) {
                    if (NetUtil.is_Network_Available(getApplicationContext())) {
                        MyUtills.showSingerDialog(this, "提示", "确定提交吗？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mProgressDialog = MyUtills.initDialog("正在提交", ReleaseGoodsActivity.this);
                                mProgressDialog.show();
                                saveLeanCloud();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mRelease.setEnabled(true);
                            }
                        });
                    } else {
                        mToast.setText("网络不可用，请检查网络连接");
                        mToast.show();
                    }
                } else {
                    mRelease.setEnabled(true);
                }
                break;
        }
    }

    private void showOption() {
        OptionsPickerView pvOptions = new OptionsPickerView(this);
        pvOptions.setPicker((ArrayList) mTypeList);
        pvOptions.setTitle("选择分类");
        pvOptions.setCyclic(false);
        pvOptions.setSelectOptions(1);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mTvType.setText(mTypeList.get(options1));
                mTypeChose = options1;
            }
        });
        pvOptions.show();
    }

    private void saveLeanCloud() {
        final AVObject avObject = new AVObject("Goods");
        avObject.put("name", mTitle);
        avObject.put("descript", mDescript);
        avObject.put("price", mPrice);
        avObject.put("currentPrice", mCurrentPrice);
        avObject.put("mark", mEtMark.getText().toString() + "");
        avObject.put("GoodType", mTypes.get(mTypeChose).getObjectId());
        avObject.put("score", mSroce);
        for (String s : mPhotosSnplData) {
            try {
                final AVFile avFile = AVFile.withAbsoluteLocalPath(getName(), s);
                avFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        String objectId = avFile.getObjectId();
                        AVQuery<AVObject> query = new AVQuery<>("_File");
                        query.whereEqualTo("objectId", objectId);
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                AVObject avObject1 = list.get(0);
                                AVRelation<AVObject> goods = avObject.getRelation("images");
                                goods.add(avObject1);
                                avObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            AVRelation<AVObject> goods1 = mShop.getRelation("goods");
                                            goods1.add(avObject);
                                            mShop.saveInBackground();
                                        } else {
                                            mToast.setText("商品保存失败");
                                        }
                                    }
                                });
                                mCount++;
                                if (mCount == mPhotosSnplData.size()) {
                                    mProgressDialog.dismiss();
                                    MyUtills.showSingerDialog(ReleaseGoodsActivity.this, "提示", "上传成功！", new
                                            DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean checkInput() {
        mTitle = mEtTitle.getText().toString();
        if (TextUtils.isEmpty(mTitle)) {
            mToast.setText("请输入商品标题");
            mToast.show();
            mEtTitle.setFocusable(true);
            mEtTitle.setFocusableInTouchMode(true);
            mEtTitle.requestFocus();
            return false;
        }
        mDescript = mEtDescript.getText().toString();
        if (TextUtils.isEmpty(mDescript)) {
            mToast.setText("请输入商品描述");
            mToast.show();
            mEtDescript.setFocusable(true);
            mEtDescript.setFocusableInTouchMode(true);
            mEtDescript.requestFocus();
            return false;
        }

        mPhotosSnplData = mPhotosSnpl.getData();
        if (mPhotosSnplData == null || mPhotosSnplData.size() == 0) {
            mToast.setText("请添加商品图片");
            mToast.show();
            return false;
        }

        mPrice = mEtPrice.getText().toString();
        if (TextUtils.isEmpty(mPrice)) {
            mToast.setText("请输入商品原价");
            mToast.show();
            mEtPrice.setFocusable(true);
            mEtPrice.setFocusableInTouchMode(true);
            mEtPrice.requestFocus();
            return false;
        }
        mCurrentPrice = mEtCurrentPrice.getText().toString();
        if (TextUtils.isEmpty(mCurrentPrice)) {
            mToast.setText("请输入商品折后价");
            mToast.show();
            mEtCurrentPrice.setFocusable(true);
            mEtCurrentPrice.setFocusableInTouchMode(true);
            mEtCurrentPrice.requestFocus();
            return false;
        }
        mSroce = mEtScore.getText().toString();
        if (TextUtils.isEmpty(mSroce)) {
            mToast.setText("请输入积分");
            mToast.show();
            mEtScore.setFocusable(true);
            mEtScore.setFocusableInTouchMode(true);
            mEtScore.requestFocus();
            return false;
        }
        mType = mTvType.getText().toString();
        if ("请选择分类".equals(mType)) {
            mToast.setText("请选择商品分类");
            mToast.show();
            return false;
        }
        return true;
    }

    private String getName() {
        Date date = new Date();
        long time = date.getTime();
        String s = time + "";
        return s;
    }
}
