package com.chinalooke.android.cheju.utills;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

/**
 * Created by xiao on 2016/8/9.
 */
public class LocationUtils {

    private static final double EARTH_RADIUS = 6378137.0;
    private static LocationManager mLocationManager;
    private static Activity mActivity;

    public LocationUtils(Activity activity, LocationManager locationManager) {
        this.mActivity = activity;
        this.mLocationManager = locationManager;
    }


    public void CheckGPSisOpen() {

        boolean isOpen = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isOpen) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivityForResult(intent, 0);
        }
    }


    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}


