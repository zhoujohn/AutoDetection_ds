package com.common.baselib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.common.baselib.AppConfig;
import com.google.gson.Gson;

/**
 * ================================================
 * 包名：com.common.baselib.utils
 * 创建人：sws
 * 创建时间：2019/5/27  下午 04:26
 * 描述：
 * ================================================
 */
public abstract class AbsSharePref {
    private synchronized SharedPreferences getSharedPreferences() {
        return AppConfig.getInstance().mAppContext.getSharedPreferences(getSharedPreferencesName(), Context.MODE_PRIVATE);
    }

    public abstract String getSharedPreferencesName();

    /**
     * 保存布尔值
     *
     * @param key
     * @param value
     */
    public void putBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }

    /**
     * 保存字符串
     *
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();

    }

    public void clear() {
        getSharedPreferences().edit().clear().apply();
    }

    /**
     * 保存long型
     *
     * @param key
     * @param value
     */
    public void putLong(String key, long value) {
        getSharedPreferences().edit().putLong(key, value).apply();
    }

    /**
     * 保存int型
     *
     * @param key
     * @param value
     */
    public void putInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }

    /**
     * 保存float型
     *
     * @param key
     * @param value
     */
    public void putFloat(String key, float value) {
        getSharedPreferences().edit().putFloat(key, value).apply();
    }

    /**
     * 获取字符值
     *
     * @param key
     * @param defValue
     * @return
     */
    public String getString(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }

    /**
     * 获取int值
     *
     * @param key
     * @param defValue
     * @return
     */
    public int getInt(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }

    /**
     * 获取long值
     *
     * @param key
     * @param defValue
     * @return
     */
    public long getLong(String key, long defValue) {
        return getSharedPreferences().getLong(key, defValue);
    }

    /**
     * 获取float值
     *
     * @param key
     * @param defValue
     * @return
     */
    public float getFloat(String key, float defValue) {
        return getSharedPreferences().getFloat(key, defValue);
    }

    /**
     * 获取布尔值
     *
     * @param key
     * @param defValue
     * @return
     */
    public boolean getBoolean(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }


    /**
     * 没有找到则自动赋默认值
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        return getString(key, "");
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }


    public void putObject(String key, Object object, String myId) {
        if (StringUtils.isNotNull(myId)) {
            key = key + "_" + myId;
        }
        try {
            String json = new Gson().toJson(object);
            String objBase64 = new String(Base64.encode(json.getBytes(), Base64.DEFAULT));
            getSharedPreferences().edit().putString(key, objBase64).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putObject(String key, Object object) {
        this.putObject(key, object, null);
    }

    public <T> T getObject(String key, Class<T> clazz, String myId) {
        T result = null;
        try {
            if (StringUtils.isNotNull(myId)) {
                key = key + "_" + myId;
            }
            String objBase64 = getSharedPreferences().getString(key, null);
            byte[] base64Bytes = Base64.decode(objBase64.getBytes(), Base64.DEFAULT);
            result = new Gson().fromJson(new String(base64Bytes), clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public <T> T getObject(String key, Class<T> clazz) {
        return this.getObject(key, clazz, null);
    }

}

