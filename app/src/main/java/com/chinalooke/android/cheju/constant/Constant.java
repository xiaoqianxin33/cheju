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
    public static final String ACTION = "com.chinalooke.android.cheju.MainActivity.SENDBROADCAST";

    public static final String GANHOST = "http://101.226.197.11"; //服务器地址ip（根据自己替换）

    /**
     * 支付宝
     */
    // 商户PID
    public static final String PARTNER = "2088421589511235";//自己填写自己项目的
    // 商户收款账号
    public static final String SELLER = "499877778@qq.com";//自己填写自己项目的
    // 商户私钥，pkcs8格式

    public static final String RSA_PRIVATE = "MIICXAIBAAKBgQCmOig+qdNqu29C7zUcdoY6pG6jZINl1JTx9wkM2RLWVwsCBJam7OQUxwcTC0LTrBqFdNCOesd6eEEeyBwbYhHiv4YscC/b9rGIqow6AQ87ydmSOADGoEjUKUwsT+RBxLZoYRF2FZ9hxEH7uuHnc/DFRbzzeVGEWUr6LTOgDdmo+wIDAQABAoGAYPdP/q3mbD6Ges87di0VxCOjfqDlnYNSl4t5tgry2CHShAzVLO7HZe9sCwnoo72jgvJPrq2kq6Z2plQpQTgGIgRKQx4Zmtv8c/pLCK0vwOHwi/2erfZMK08MAhqaW+WVT3Iicewn3LFwlPIS5Yg9V7282Q2XlcmL5OnHjRIwiRECQQDcuxLR0HegnpeWXTtM1SDkaGI3mhbPJ4XyUDljRgucmS6k2QhSrCIt17ly/QyUFH51GpCC3TrBA0f9aUJmH0jVAkEAwMmhf25KoAhcndpcRtwkVxGojCYOiVFmrMpO4yjjibmT2DLG1POJj0ga6l/UJMzBM/M4GnZ2E9sedHAe4r0SjwJAYIce78puxjsUp5kV+b+qprhPW2lzWBw8o38Z2aQkJ9OFZmnTDfRg7hNM8lBfU9KyipuXXuNIcmfw7408kFRWEQJARCM/hCV8FKG/8j6qHJVT9r4T/yy4OIpfqrR6O0lJWnUPrvsLUVqeBNksQigKd9MyeeT9sESsKQpp8idqkyLWOwJBAM/Bs5BKmp9BOAKETITdNCiN9/6uVx2aLNdpcvM8Lqeax0pypYQHxEnnvYy4B+W8J7v9A6dYB+LZTon2FQob25M=";
    public static final String aliPay_notifyURL = GANHOST + "/service/alipay/orderComplete";//支付宝支付成功的回调
    //支付宝公密
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
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

    public static int[] wodeListViewIcon = {R.mipmap.shoucang, R.mipmap.score, R.mipmap.exchange, R.mipmap.customer, R.mipmap.address, R.mipmap.recommend, R.mipmap.loginout};
    public static String[] policyListView = {"保单号", "车主姓名", "行使城市", "手机号码", "身份证号码", "车牌号码", "注册登记日期", "车架号", "发动机号", "品牌型号", "保单状态"};
    public static String[] policyCheckListView = {"保单号", "车主姓名", "行使城市", "手机号码", "身份证号码", "车牌号码", "注册登记日期", "车架号", "发动机号", "品牌型号", "保单状态", "保险公司", "价格"};

    public static String[] personListView = {"姓名", "身份证号", "手机号码", "地址", "推荐人", "邮箱"};
}
