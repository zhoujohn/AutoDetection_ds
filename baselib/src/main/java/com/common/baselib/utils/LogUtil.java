package com.common.baselib.utils;


import android.util.Log;

public class LogUtil {
    private static final String DEFAULT_TAG = "tag_callback";
    /**
     * 网络请求
     */
    public static final String NET_TAG = "tag_network";

    /**
     * log是否开启
     */
    public static boolean isLogShow = true;


    public static void log(String tag, int level, String msg, Throwable tr) {
        if (isLogShow) {
            switch (level) {
                case Log.VERBOSE:
                    if (tr == null) {
                        Log.v(tag, msg);
                    } else {
                        Log.v(tag, msg, tr);
                    }
                    break;
                case Log.INFO:
                    if (tr == null) {
                        Log.i(tag, msg);
                    } else {
                        Log.i(tag, msg, tr);
                    }
                    break;
                case Log.DEBUG:
                    if (tr == null) {
                        Log.d(tag, msg);
                    } else {
                        Log.d(tag, msg, tr);
                    }
                    break;
                case Log.WARN:
                    if (tr == null) {
                        Log.w(tag, msg);
                    } else {
                        Log.w(tag, msg, tr);
                    }
                    break;
                case Log.ERROR:
                    if (tr == null) {
                        Log.e(tag, msg, tr);
                    } else {
                        Log.e(tag, msg, tr);
                    }

                    break;
            }
        }

    }

    public static void log(String tag, int level, String msg) {
        log(tag, level, msg, null);
    }

    public static void log(String tag, String msg) {
        log(tag, Log.INFO, msg);
    }

    public static void log(String msg) {
        log(DEFAULT_TAG, Log.INFO, msg);
    }


    public static void setLogEnable(boolean isShow) {
        isLogShow = isShow;
    }
}
