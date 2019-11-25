package org.opencv.utils.helper;

import com.common.baselib.utils.AbsSharePref;

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