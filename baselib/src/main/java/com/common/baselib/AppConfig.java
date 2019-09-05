package com.common.baselib;

import android.content.Context;

/**
 * ================================================
 * 包名：com.common.baselib
 * 创建人：sws
 * 创建时间：2019/5/27  下午 04:27
 * 描述：
 * ================================================
 */
public class AppConfig {

    public Context mAppContext;

    public static class Holder {
        public static AppConfig instance = new AppConfig();
    }

    public static AppConfig getInstance() {
        return Holder.instance;
    }


    public void init(Context context) {
        mAppContext = context;
    }

}
