package com.chinalooke.android.cheju.utills;

import java.util.Calendar;

import android.text.TextUtils;


/**
 * @author max
 * @name StringUtils
 * @description 文字工具，主要用于图片url处理
 * @date 2012-11-27
 */
public class StringUtils {
    /**
     * @return name 生成随机名字
     */
    public static String makeImgName(String dstName) {
        String name = "";

        String fileName = String.valueOf(Calendar.getInstance()
                .getTimeInMillis());
        String extName = dstName.substring(dstName.lastIndexOf("."));
        if (extName.equals(".JPG")) {
            extName = ".jpg";
        }
        if (extName.equals(".PNG")) {
            extName = ".png";
        }
        if (extName.equals(".BMP")) {
            extName = ".bmp";
        }
        if (extName.equals(".GIF")) {
            extName = ".gif";
        }
        if (extName.equals(".JPEG")) {
            extName = ".jpeg";
        }
        if (extName.equals(".Jpg")) {
            extName = ".jpg";
        }
        if (extName.equals(".Png")) {
            extName = ".png";
        }
        if (extName.equals(".Bmp")) {
            extName = ".bmp";
        }
        if (extName.equals(".Gif")) {
            extName = ".gif";
        }
        if (extName.equals(".Jpeg")) {
            extName = ".jpeg";
        }
        name = fileName + extName;
        return name;
    }

    /**
     * 截取url的最后一段做为图像文件名
     *
     * @param url
     * @return 图像文件名
     */
    public static String getFileName(String url) {
        try {
            if (url == null || "".equals(url)) {
                return url;
            }
            int start = url.lastIndexOf("/");
            int end = url.lastIndexOf(".");
            if (start == -1 || end == -1) {
                return url;
            }
            return url.substring(start + 1, end);
        } catch (StringIndexOutOfBoundsException e) {

        }
        return url;
    }

    /**
     * 截取url的最后一段做为图像文件的全名带文件类型
     *
     * @param url
     * @return 图像文件名
     */
    public static String getFileFullName(String url) {
        try {
            if (url == null || "".equals(url)) {
                return url;
            }
            int start = url.lastIndexOf("/");
            if (start == -1) {
                return url;
            }
            return url.substring(start + 1);
        } catch (StringIndexOutOfBoundsException e) {

        }
        return url;
    }

    /**
     * 由原图地址生成缩略图
     *
     * @param p_image
     * @return
     */
    public static String getThumbImage(String p_image) {
        if (TextUtils.isEmpty(p_image)) {
            return "";
        }
        String imgName = p_image.substring(p_image.lastIndexOf("/")
                + "/".length());
        String imgPath = p_image.substring(0, p_image.lastIndexOf("/") + 1);
        return imgPath + "thumb_" + imgName;
    }

    /**
     * 去除后缀名带来的文件浏览器的视图凌乱(特别是图片更需要如此类似处理，否则有的手机打开图库，全是我们的缓存图片)
     *
     * @param url
     * @return
     */
    public static String replaceUrlWithPlus(String url) {
        // 1. 处理特殊字符
        // 2. 去除后缀名带来的文件浏览器的视图凌乱(特别是图片更需要如此类似处理，否则有的手机打开图库，全是我们的缓存图片)
        if (url != null) {
            return url.replaceAll("http://(.)*?/", "")
                    .replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
        }
        return null;
    }
}
