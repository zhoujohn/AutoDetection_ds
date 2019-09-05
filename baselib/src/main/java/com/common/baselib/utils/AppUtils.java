package com.common.baselib.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.common.baselib.AppConfig;

public class AppUtils {

    public static String getVersionName() {
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

    public static int packageCode() {
        PackageManager manager = AppConfig.getInstance().mAppContext.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(AppConfig.getInstance().mAppContext.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }
}
