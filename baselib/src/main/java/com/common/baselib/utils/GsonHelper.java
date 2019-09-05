package com.common.baselib.utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GsonHelper {
    private static Gson INSTANCE;

    static {
        INSTANCE = new Gson();
    }

    public static <T> T parse(String strDataJson, Class<T> classOfT) {
        T data = null;
        if (null != strDataJson) {
            try {
                data = INSTANCE.fromJson(strDataJson, classOfT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static <T> T parseUnCatch(String strDataJson, Class<T> classOfT) {
        T data = null;
        if (null != strDataJson) {
            data = INSTANCE.fromJson(strDataJson, classOfT);
        }
        return data;
    }

    public static <T> T parse(JsonObject jsonObject, Class<T> classOfT) {
        T data = null;
        if (null != jsonObject) {
            try {
                data = INSTANCE.fromJson(jsonObject, classOfT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static <T> T parse(byte[] dataJson, Class<T> classOfT) {
        T data = null;
        String strDataJson = null;
        try {
            strDataJson = new String(dataJson, "utf-8");
            data = parse(strDataJson, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static <T> T parse(String strDataJson, Type type) {
        T data = null;
        if (null != strDataJson) {
            try {
                data = INSTANCE.fromJson(strDataJson, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static <T> List<T> parseArray(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        List<T> result = gson.fromJson(jsonData, new TypeToken<List<T>>() {
        }.getType());
        return result;
    }

    public static String toString(Object obj) {
        return INSTANCE.toJson(obj);
    }

    public static Gson getGson() {
        return INSTANCE;
    }

}
