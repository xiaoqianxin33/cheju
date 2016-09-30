package com.chinalooke.android.cheju.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.chinalooke.android.cheju.R;
import com.chinalooke.android.cheju.activity.LipeiActivity;
import com.chinalooke.android.cheju.activity.LoginActivity;
import com.chinalooke.android.cheju.activity.WriteMessgeActivity;
import com.chinalooke.android.cheju.activity.YouhuiJuanActivity;
import com.chinalooke.android.cheju.bean.Insurance;
import com.chinalooke.android.cheju.bean.PayResult;
import com.chinalooke.android.cheju.constant.Constant;
import com.chinalooke.android.cheju.utills.ImageTools;
import com.chinalooke.android.cheju.utills.MyUtills;
import com.chinalooke.android.cheju.utills.SignUtils;
import com.chinalooke.android.cheju.view.SyListView;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.chinalooke.android.cheju.constant.Constant.RSA_PRIVATE;
import static com.chinalooke.android.cheju.constant.Constant.SELLER;

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
    private AVUser mCurrentUser;
    private Drawable mDrawable;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(getActivity(), "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(getActivity(), "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(getActivity(), "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_sy, container, false);
        ButterKnife.bind(this, mView);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
        mCurrentUser = AVUser.getCurrentUser();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser = AVUser.getCurrentUser();
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

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + Constant.PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "";

        return orderInfo;
    }

    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    private void initEvent() {
        mGvSy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            int MIN_CLICK_DELAY_TIME = 1000;
            long lastClickTime = 0;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    lastClickTime = currentTime;
                    switch (position) {
                        case 0:
                            alipay();
//                            if (mCurrentUser != null) {
//                                Intent intent = new Intent();
//                                intent.setClass(getActivity(), WriteMessgeActivity.class);
//                                intent.putExtra("company", "");
//                                startActivity(intent);
//                            } else {
//                                Intent intent = new Intent();
//                                intent.setClass(getActivity(), LoginActivity.class);
//                                startActivity(intent);
//                            }

                            break;

                        case 1:
                            startActivity(new Intent(getActivity(), LipeiActivity.class));
                            break;

                        case 2:
                            startActivity(new Intent(getActivity(), YouhuiJuanActivity.class));
                            break;
                        case 7:
                            if (mCurrentUser == null) {
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            } else {
                                showShare();
                            }
                            break;
                        case 3:
                            Intent intent = new Intent(getActivity(), YouhuiJuanActivity.class);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                            break;
                        case 4:
                            Intent intent4 = new Intent(getActivity(), YouhuiJuanActivity.class);
                            intent4.putExtra("type", 2);
                            startActivity(intent4);

                            break;
                        case 5:
                            Intent intent5 = new Intent(getActivity(), YouhuiJuanActivity.class);
                            intent5.putExtra("type", 3);
                            startActivity(intent5);
                            break;
                        case 6:
                            Intent intent6 = new Intent(getActivity(), YouhuiJuanActivity.class);
                            intent6.putExtra("type", 4);
                            startActivity(intent6);
                            break;

                    }
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

    private static final int SDK_PAY_FLAG = 1;

    private void alipay() {
        String orderInfo = getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(getActivity());
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
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


        oks.setImagePath(Environment.getExternalStorageDirectory().getPath() + "arrow.png");

        oks.setUrl("http://sharesdk.cn");


// 启动分享GUI
        oks.show(getActivity());
    }

    private void initView() {
        setAd();
        mListviewMain.setVerticalScrollBarEnabled(false);
        mDrawable = ImageTools.setDrwableSize2(getActivity(), R.mipmap.placeholder, 500, 122);
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
                        imageView.setImageResource(R.mipmap.placeholder);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Picasso.with(getActivity()).load(url).resize(mWidth, MyUtills.Dp2Px(getActivity(), 122)).centerCrop().into(imageView);
                        mAdList.add(imageView);
                    }
                    if (mBanner != null)
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
