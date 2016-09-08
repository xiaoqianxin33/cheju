package com.chinalooke.android.cheju.constant;

import com.chinalooke.android.cheju.R;

/**
 * Created by xiao on 2016/8/6.
 */
public class Constant {
    public static final int REQUSET = 0;

    public static final String START_LOCATION = "start_location";
    public static final String qrCode = "http://api.k780.com:88/?app=qr.get&data=?&level=L&size=6";

    public static final String BUSINESS = "商家";
    public static final String SALESMAN = "业务员";
    public static final String ADMIN = "管理员";
    public static final String USER = "普通用户";


    public static int[] mainIcon = {R.mipmap.buybx, R.mipmap.lipei, R.mipmap.youhui,
            R.mipmap.washcar, R.mipmap.luntai, R.mipmap.jiuyuan, R.mipmap.kuaixiu, R.mipmap.tuijian};

    public static int[] mainIconSelector = {R.drawable.blue_round_shape, R.drawable.green_round_shape,
            R.drawable.orange_round_shape, R.drawable.red_round_shape, R.drawable.qing_round_shape, R.drawable.purple_round_shape, R.drawable.yellow_round_shape, R.drawable.tuijian_round_shape};


    public static String[] mainTitleGridView = {"购买车险", "在线理赔", "积分兑换", "特惠洗车", "正品轮胎", "道路救援", "优质快修", "推荐APP"};

    public static String[] writeChezhuListView = {"手机号码", "身份证号", "车牌号码", "注册登记日期"};

    public static String[] writeCheliangListView = {"车主姓名", "完整车架号", "发动机号", "品牌型号"};

    public static int[] youhuiIcon = {R.mipmap.appcan_s, R.mipmap.appcan_s};

    public static String[] youhuiShopName = {"味多美", "呷哺"};

    public static String[] wodeListView = {"我的收藏", "我的积分", "积分兑换", "我的客户", "邮寄地址", "推荐车聚APP", "退出登录"};

    public static String[] policyListView = {"保单号", "车主姓名", "行使城市", "手机号码", "身份证号码", "车牌号码", "注册登记日期", "车架号", "发动机号", "品牌型号", "保单状态"};
    public static String[] policyCheckListView = {"保单号", "车主姓名", "行使城市", "手机号码", "身份证号码", "车牌号码", "注册登记日期", "车架号", "发动机号", "品牌型号", "保单状态", "保险公司", "价格"};

    public static String[] personListView = {"姓名", "身份证号", "手机号码", "地址", "推荐人", "邮箱"};
}
