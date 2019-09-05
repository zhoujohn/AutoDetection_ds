package com.common.mvplib.listener;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================
 * 包名：com.common.mvplib.listener
 * 创建人：sws
 * 创建时间：2019/5/27  下午 05:28
 * 描述：
 * ================================================
 */
public class Function {

    private Map<String, Function1> mFunMap;

    private Function() {
        mFunMap = new HashMap<>();
    }

    public static Function getInstance() {
        return Holder.instance;
    }

    public static class Holder {
        public static Function instance = new Function();
    }

    public void registerFunction(String key, Function1 function1) {
        mFunMap.put(key, function1);
    }

    public void unRegisterFunction(String key) {
        mFunMap.remove(key);
    }

    public Function1 getFunction(String key) {
        return mFunMap.get(key);
    }


    public interface Function1<T, R> {
        R call(T t);
    }
}
