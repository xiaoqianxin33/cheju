package com.chinalooke.android.cheju.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.AddressActivity;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.NetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ShowAddressFragment extends Fragment {


    @Bind(R.id.lv_address)
    ListView mLvAddress;
    private AddressActivity mAddressActivity;
    private List<AVObject> mAdresse;
    private Map<Integer, Boolean> isSelected;
    private List beSelectedData = new ArrayList();
    private MyAdapt mMyAdapt;
    private AVUser mCurrentUser;
    private Toast mToast;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_adress, container, false);
        ButterKnife.bind(this, view);
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAddressActivity = (AddressActivity) getActivity();
        mCurrentUser = AVUser.getCurrentUser();
        initData();

    }

    public void refreshData() {
        initData();
    }

    private void initData() {

        AVRelation<AVObject> relation = mCurrentUser.getRelation("address");
        AVQuery<AVObject> query = relation.getQuery();
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null && list.size() != 0) {
                    mAdresse = list;
                    if (mMyAdapt != null)
                        mMyAdapt.notifyDataSetChanged();
                    initView();
                } else {
                    mToast.setText(e.getMessage());
                    mToast.show();
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
        if (mMyAdapt != null)
            mMyAdapt.notifyDataSetChanged();
    }

    private void initView() {

        if (isSelected != null)
            isSelected = null;
        isSelected = new HashMap<Integer, Boolean>();

        if (mAdresse.size() == 1) {
            AVObject avObject = mAdresse.get(0);
            boolean aDefault = avObject.getBoolean("default");
            if (!aDefault) {
                avObject.put("default", true);
                avObject.saveInBackground();
            }
            isSelected.put(0, true);
        } else {
            for (int i = 0; i < mAdresse.size(); i++) {
                AVObject avObject = mAdresse.get(i);
                isSelected.put(i, avObject.getBoolean("default"));
            }
        }

        if (beSelectedData.size() > 0) {
            beSelectedData.clear();
        }
        mMyAdapt = new MyAdapt(mAdresse);
        mLvAddress.setAdapter(mMyAdapt);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class MyAdapt extends BaseAdapter {

        private List cs;

        public MyAdapt(List data) {
            this.cs = data;
        }


        @Override
        public int getCount() {
            return mAdresse.size();
        }

        @Override
        public Object getItem(int position) {
            return mAdresse.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_address_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            setDetail(viewHolder, position);
            viewHolder.mCbCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean cu = !isSelected.get(position);
                    for (Integer p : isSelected.keySet()) {
                        isSelected.put(p, false);
                    }
                    isSelected.put(position, cu);
                    MyAdapt.this.notifyDataSetChanged();
                    beSelectedData.clear();
                    if (cu) beSelectedData.add(cs.get(position));
                }
            });
            viewHolder.mCbCheck.setChecked(isSelected.get(position));
            return convertView;
        }
    }

    private void setDetail(ViewHolder viewHolder, int position) {
        final AVObject avObject = mAdresse.get(position);
        viewHolder.mTvAddress.setText(avObject.getString("address"));
        viewHolder.mTvName.setText(avObject.getString("name"));
        viewHolder.mTvPhone.setText(avObject.getString("phone"));
        viewHolder.mCbCheck.setChecked(avObject.getBoolean("default"));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtills.showSingerDialog(getActivity(), "提示", "确定删除此地址吗？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (NetUtil.is_Network_Available(getActivity())) {

                            initDialog("删除中");
                            AVQuery<AVObject> avQuery = new AVQuery<>("Address");
                            avQuery.getInBackground(avObject.getObjectId(), new GetCallback<AVObject>() {
                                @Override
                                public void done(AVObject avObject, AVException e) {
                                    if (e == null) {
                                        avObject.deleteInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                mProgressDialog.dismiss();
                                                if (e == null) {
                                                    mToast.setText("删除成功");
                                                    mToast.show();
                                                    initData();
                                                } else {
                                                    mToast.setText(e.getMessage());
                                                    mToast.show();
                                                }
                                            }
                                        });
                                    } else {
                                        mProgressDialog.dismiss();
                                        mToast.setText("删除错误");
                                        mToast.show();
                                    }
                                }
                            });
                        } else {
                            mToast.setText("网络不可用，请检查网络连接");
                            mToast.show();
                        }


                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        };

        viewHolder.mTvDelete.setOnClickListener(onClickListener);
        viewHolder.mIvDelete.setOnClickListener(onClickListener);

        View.OnClickListener onClickListener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddressActivity.switchContent(mAddressActivity.getShowAddressFragment(), mAddressActivity.getAddAdressFragment());
                mAddressActivity.getBtnAddress().setVisibility(View.GONE);
                mAddressActivity.setBianAvobject(avObject);
            }
        };
        viewHolder.mTvBianji.setOnClickListener(onClickListener1);
        viewHolder.mTvBianji.setOnClickListener(onClickListener1);
        boolean aDefault = avObject.getBoolean("default");
        viewHolder.mCbCheck.setText(aDefault ? "默认地址" : "设为默认");

        viewHolder.mCbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setText("默认地址");
                } else {
                    buttonView.setText("设为默认");
                }
                avObject.put("default", isChecked);
                avObject.saveInBackground();
            }
        });
    }

    static class ViewHolder {
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_phone)
        TextView mTvPhone;
        @Bind(R.id.tv_address)
        TextView mTvAddress;
        @Bind(R.id.tv_delete)
        TextView mTvDelete;
        @Bind(R.id.iv_delete)
        ImageView mIvDelete;
        @Bind(R.id.tv_bianji)
        TextView mTvBianji;
        @Bind(R.id.iv_bianji)
        ImageView mIvBianji;
        @Bind(R.id.cb_check)
        CheckBox mCbCheck;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void initDialog(String message) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }
}
