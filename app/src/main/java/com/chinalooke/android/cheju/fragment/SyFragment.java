package com.chinalooke.android.cheju.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.LipeiActivity;
import com.chinalooke.android.cheju.activity.WriteMessgeActivity;
import com.chinalooke.android.cheju.activity.YouhuiJuanActivity;
import com.chinalooke.android.cheju.bean.Insurance;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.view.SyListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * Created by xiao on 2016/8/6.
 */
public class SyFragment extends Fragment {


    @Bind(R.id.gv_sy)
    GridView mGvSy;
    @Bind(R.id.listview_main)
    SyListView mListviewMain;
    @Bind(R.id.banner)
    BGABanner mBanner;
    private View mView;
    private List<View> mAdList = new ArrayList<>();
    private List<Insurance> mInsurances = new ArrayList<>();
    private int mWidth;
    private ListViewAdapt mListViewAdapt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_sy, container, false);
        ButterKnife.bind(this, mView);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
        return mView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, mView);
        mListViewAdapt = new ListViewAdapt();
        initData();
        initView();
        initEvent();

    }

    private void initData() {
        mInsurances.clear();
        AVQuery<AVObject> query = new AVQuery<>("Insurance");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null && list.size() != 0) {
                    for (AVObject avObject : list) {
                        Insurance insurance = new Insurance();
                        AVFile image = avObject.getAVFile("image");
                        insurance.setName(avObject.getString("name"));
                        insurance.setImage(image.getUrl());
                        insurance.setAddress(avObject.getString("address"));
                        mInsurances.add(insurance);
                        mListViewAdapt.notifyDataSetChanged();
                    }

                }
            }
        });
    }

    private void initEvent() {
        mGvSy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), WriteMessgeActivity.class);
                        intent.putExtra("company", "");
                        startActivity(intent);
                        break;

                    case 1:
                        startActivity(new Intent(getActivity(), LipeiActivity.class));
                        break;

                    case 2:
                        startActivity(new Intent(getActivity(), YouhuiJuanActivity.class));
                }
            }
        });

        mListviewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), WriteMessgeActivity.class);
                intent.putExtra("company", mInsurances.get(position).getName());
                startActivity(intent);
            }
        });

    }

    private void initView() {
        setAd();
        mListviewMain.setVerticalScrollBarEnabled(false);

        mListviewMain.setAdapter(mListViewAdapt);
        mGvSy.setAdapter(new MyGvAdapt(getContext()));

    }

    private void setAd() {
        mAdList.clear();
        AVQuery<AVObject> query = new AVQuery<>("Ad");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {

                    for (AVObject avObject : list) {
                        AVFile image = avObject.getAVFile("image");
                        String url = image.getUrl();
                        ImageView imageView = new ImageView(getActivity());
//                        imageView.setScaleType(ImageView.ScaleType.MATRIX);
                        Picasso.with(getActivity()).load(url).resize(mWidth, MyUtills.Dp2Px(getActivity(), 122))
                                .into(imageView);
                        mAdList.add(imageView);
                    }
                    mBanner.setData(mAdList);
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    public class MyGvAdapt extends BaseAdapter {

        private LayoutInflater mInflater;


        public MyGvAdapt(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return Constant.mainIcon.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_main_gridview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mTvItemBackGridview.setBackgroundResource(Constant.mainIconSelector[position]);
            Drawable drawable = setDrawableSize(Constant.mainIcon[position]);
            viewHolder.mIvItemMainGridview.setImageDrawable(drawable);
            viewHolder.mTvItemMainGridview.setText(Constant.mainTitleGridView[position]);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.iv_item_main_gridview)
            ImageView mIvItemMainGridview;
            @Bind(R.id.tv_item_main_gridview)
            TextView mTvItemMainGridview;
            @Bind(R.id.iv_back_main_gridview)
            ImageView mTvItemBackGridview;


            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    private Drawable setDrawableSize(int i) {
        Drawable drawable1 = getResources().getDrawable(i);
        drawable1.setBounds(5, 5, MyUtills.Dp2Px(getContext(), 26), MyUtills.Dp2Px(getContext(), 26));
        return drawable1;
    }

    class ListViewAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return mInsurances.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MainHolder viewHolder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_main_listview, null);
                viewHolder = new MainHolder();
                viewHolder.iv_holder = (ImageView) convertView.findViewById(R.id.iv_item_main_listview);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MainHolder) convertView.getTag();
            }

            Picasso.with(getActivity()).load(mInsurances.get(position).getImage()).into(viewHolder.iv_holder);
            return convertView;
        }
    }

    class MainHolder {
        private ImageView iv_holder;
    }


}
