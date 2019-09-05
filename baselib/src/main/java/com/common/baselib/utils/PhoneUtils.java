package com.common.baselib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.common.baselib.AppConfig;

/**
 * 项目名称：HLTravel
 * 创建人：SWS
 * 创建时间：2019/5/13  下午 09:30
 * 描述：
 */
public class PhoneUtils {


    public static String packageName() {
        PackageManager manager = AppConfig.getInstance().mAppContext.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(AppConfig.getInstance().mAppContext.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    /**
     * 获取手机IMEI
     *
     * @return
     */
    public static final String getIMEI() {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) AppConfig.getInstance().mAppContext.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 获取手机IMSI
     */
    public static String getIMSI() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) AppConfig.getInstance().mAppContext.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMSI号
            @SuppressLint("MissingPermission") String imsi = telephonyManager.getSubscriberId();
            if (null == imsi) {
                imsi = "";
            }
            return imsi;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
