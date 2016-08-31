package kankan.wheel.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiao on 2016/8/5.
 */
public class MyUtills {

    public static boolean CheckPhoneNumer(String str)

    {
        Pattern p = Pattern.compile("^1(3|4|5|7|8)\\d{9}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }










}
