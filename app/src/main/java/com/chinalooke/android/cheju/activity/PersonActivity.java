package com.chinalooke.android.cheju.activity;

import android.app.ProgressDialog;
import android.content.Entity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.bean.User;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.view.SyListView;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonActivity extends AppCompatActivity {

    @Bind(R.id.tv_title2)
    TextView mTvTitle2;
    @Bind(R.id.xlistview)
    SyListView mXlistview;
    @Bind(R.id.btn_persons)
    Button mBtnPersons;
    private AVUser mCurrentUser;
    private boolean mEdit = false;
    private MyAdapt mMyAdapt;
    private ProgressDialog mProgressDialog;
    private User mUser;
    private Toast mToast;
    private HashMap<String, String> mHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ButterKnife.bind(this);
        mUser = new User();
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        initData();
        initView();
    }

    private void initView() {
        mMyAdapt = new MyAdapt();
        mXlistview.setAdapter(mMyAdapt);
    }

    private void initData() {
        mCurrentUser = AVUser.getCurrentUser();
        mCurrentUser.fetchIfNeededInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {

            }
        });
    }

    @OnClick({R.id.iv_wirte_back, R.id.btn_persons})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wirte_back:
                finish();
                break;
            case R.id.btn_persons:
                if (!mEdit) {
                    mBtnPersons.setText("确定修改");
                    mMyAdapt.notifyDataSetChanged();
                    mEdit = true;
                } else {
                    initDialog("正在提交中");
                    saveCloud();
                }
                break;
        }
    }

    private void saveCloud() {
        mCurrentUser.put("addrssName", mHashMap.get("name"));
        mCurrentUser.put("IDNo", mHashMap.get("id"));
        mCurrentUser.put("phone", mHashMap.get("phone"));
//        mCurrentUser.put("phone",mUser.getPhone());
        mCurrentUser.put("referrer", mHashMap.get("referrer"));
        mCurrentUser.put("email", mHashMap.get("email"));
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                mProgressDialog.dismiss();
                if (e == null) {
                    mToast.setText("资料修改成功");
                    mToast.show();
                    mCurrentUser.setFetchWhenSave(true);
                    mBtnPersons.setText("修改个人资料");
                    mMyAdapt.notifyDataSetChanged();
                    mEdit = false;
                } else {
                    mToast.setText(e.getMessage());
                    mToast.show();
                }
            }
        });
    }


    class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return Constant.personListView.length;
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
                convertView = View.inflate(getApplicationContext(), R.layout.item_write_listview, null);
                viewHolder = new ViewHolder(convertView);
                viewHolder.mEtItemWriteListview.setTag(position);
                viewHolder.mEtItemWriteListview.addTextChangedListener(new MyTextWatcher(viewHolder));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                viewHolder.mEtItemWriteListview.setTag(position);
            }
            viewHolder.mTvItemWriteListview.setText(Constant.personListView[position]);
            setDetail(viewHolder, position);
            if (!mEdit) {
                viewHolder.mEtItemWriteListview.setEnabled(false);
                viewHolder.mEtItemWriteListview.setHint("");
            } else {
                viewHolder.mEtItemWriteListview.setEnabled(true);
                viewHolder.mEtItemWriteListview.setHint("请填写");
            }
            return convertView;
        }


    }

    private void setDetail(ViewHolder viewHolder, final int position) {
        switch (position) {
            case 0:
                viewHolder.mEtItemWriteListview.setText(mCurrentUser.getString("addressName"));
                break;
            case 1:
                viewHolder.mEtItemWriteListview.setText(mCurrentUser.getString("IDNo"));
                break;
            case 2:
                viewHolder.mEtItemWriteListview.setText(mCurrentUser.getString("phone"));
                break;
//                case 3:
//                    viewHolder.mEtItemWriteListview.setText(mCurrentUser.getString("addressName"));
//                    break;
            case 4:
                viewHolder.mEtItemWriteListview.setText(mCurrentUser.getString("referrer"));
                break;
            case 5:
                viewHolder.mEtItemWriteListview.setText(mCurrentUser.getString("email"));
                break;
        }

    }


    class MyTextWatcher implements TextWatcher {

        private ViewHolder mHolder;

        public MyTextWatcher(ViewHolder holder) {
            mHolder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {


        }

        @Override
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {


        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null) {
                int position = (Integer) mHolder.mEtItemWriteListview.getTag();
                switch (position) {
                    case 0:
                        mHashMap.put("name", s.toString());
                        break;
                    case 1:
                        mHashMap.put("id", s.toString());
                        break;
                    case 2:
                        mHashMap.put("phone", s.toString());
                        break;
                    case 3:
                        mHashMap.put("address", s.toString());
                        break;
                    case 4:
                        mHashMap.put("referrer", s.toString());
                        break;
                    case 5:
                        mHashMap.put("email", s.toString());
                        break;
                }

            }
        }
    }

    static class ViewHolder {
        @Bind(R.id.tv_item_write_listview)
        TextView mTvItemWriteListview;
        @Bind(R.id.et_item_write_listview)
        EditText mEtItemWriteListview;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    private void initDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }
}
