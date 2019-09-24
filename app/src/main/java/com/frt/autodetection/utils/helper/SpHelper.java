package com.frt.autodetection.utils.helper;

import com.common.baselib.utils.AbsSharePref;

/**
 * ================================================
 * 包名：com.frt.autodetection.utils.helper
 * 创建人：sws
 * 创建时间：2019/9/23  下午 02:12
 * 描述：
 * ================================================
 */
public class SpHelper extends AbsSharePref {

    public static SpHelper getInstance() {
        return SpHelper.Holder.instance;
    }


    public static class Holder {
        public static SpHelper instance = new SpHelper();
    }

    @Override
    public String getSharedPreferencesName() {
        return "AppData";
    }

}
