package com.chinalooke.android.cheju.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.AddressActivity;

import org.json.JSONException;
import org.json.JSONObject;

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
    private List<JSONObject> mJsonObjects;
    private Map<Integer, Boolean> isSelected;
    private List beSelectedData = new ArrayList();
    private MyAdapt mMyAdapt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_adress, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAddressActivity = (AddressActivity) getActivity();
        mJsonObjects = mAddressActivity.getJSONObjects();
        initView();
    }

    private void initView() {
        if (isSelected != null)
            isSelected = null;
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < mJsonObjects.size(); i++) {
            isSelected.put(i, false);
        }
        if (beSelectedData.size() > 0) {
            beSelectedData.clear();
        }
        mMyAdapt = new MyAdapt(mJsonObjects);
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
            return mJsonObjects.size();
        }

        @Override
        public Object getItem(int position) {
            return mJsonObjects.get(position);
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
//            viewHolder.mCbCheck.setChecked(isSelected.get(position));
            return convertView;
        }
    }

    private void setDetail(ViewHolder viewHolder, int position) {
        JSONObject jsonObject = mJsonObjects.get(position);
        try {
            viewHolder.mTvAddress.setText(jsonObject.getString("address"));
            viewHolder.mTvName.setText(jsonObject.getString("name"));
            viewHolder.mTvPhone.setText(jsonObject.getString("phone"));
            viewHolder.mCbCheck.setChecked(jsonObject.getBoolean("default"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static class ViewHolder {
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_phone)
        TextView mTvPhone;
        @Bind(R.id.tv_address)
        TextView mTvAddress;
        @Bind(R.id.cb_check)
        CheckBox mCbCheck;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
