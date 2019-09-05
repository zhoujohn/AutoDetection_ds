package com.common.baselib.utils;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.common.baselib.AppConfig;


public class ToastUtils {
    public static void showShortToast(Context context, String str) {
        showShortToast(str);
    }

    public static void showShortToast(Context context, int strId) {
        showShortToast(strId);
    }

    public static void showShortToast(String str) {
        //TODO  BUG  Caused by: java.lang.ExceptionInInitializerError
        showToast(null, str, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(int strId) {
        String str = AppConfig.getInstance().mAppContext.getResources().getString(strId);
        showShortToast(str);
    }

    public static void showLongToast(String str) {
        showLongToast(AppConfig.getInstance().mAppContext, str);
    }

    public static void showLongToast(int strId) {
        String str = AppConfig.getInstance().mAppContext.getResources().getString(strId);
        showLongToast(str);
    }

    public static void showLongToast(Context context, String str) {
        showToast(context, str, Toast.LENGTH_LONG);
    }

    public static Toast toast;

    static {
        Looper.prepare();
        toast = Toast.makeText(AppConfig.getInstance().mAppContext, "", Toast.LENGTH_SHORT);
        Looper.loop();
    }

    private synchronized static void showToast(Context context, String str, int duration) {
        if (!TextUtils.isEmpty(str)) {
            if (toast != null) {
                toast.setDuration(duration);
                toast.setText(str);
                toast.show();
            } else {
                toast = Toast.makeText(AppConfig.getInstance().mAppContext, str, duration);
                toast.show();
            }
        }
    }
}
