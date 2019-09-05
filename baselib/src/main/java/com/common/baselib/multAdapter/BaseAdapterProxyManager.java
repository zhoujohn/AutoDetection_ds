package com.common.baselib.multAdapter;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================
 * 包名：com.common.baselib.multAdapter
 * 创建人：sws
 * 创建时间：2019/5/27  下午 04:24
 * 描述：
 * ================================================
 */
public abstract class BaseAdapterProxyManager {

    public Activity mActivity;

    public static final int TYPE_DEFAULT = 0;

    private Map<Integer, BaseAdapterProxy> mProxyMap;
    public BaseMultiTypeAdapter rootAdapter;

    public BaseAdapterProxyManager(Activity mActivity, BaseMultiTypeAdapter rootAdapter) {
        this.mActivity = mActivity;
        this.rootAdapter = rootAdapter;
        this.mProxyMap = new HashMap<>();
        initDefaultProxy();
        initAdapterProxyMap();
    }

    public void registerAdapterProxy(int type, BaseAdapterProxy proxy) {
        mProxyMap.put(type, proxy);
    }

    public int matchType(Object bean) {
        if (bean == null) {
            return TYPE_DEFAULT;
        }
        if (mProxyMap.isEmpty()) {
            return TYPE_DEFAULT;
        }
        for (Map.Entry<Integer, BaseAdapterProxy> me : mProxyMap.entrySet()) {
            boolean isMatch = me.getValue().isMatchType(bean);
            if (isMatch) {
                return me.getKey();
            }
        }
        return TYPE_DEFAULT;
    }

    public BaseAdapterProxy getAdapterProxyByType(int type) {
        return mProxyMap.get(type);
    }

    private void initDefaultProxy() {
        mProxyMap.put(TYPE_DEFAULT, new DefaultAdapterProxy(mActivity, rootAdapter));
    }

    public abstract void initAdapterProxyMap();

    public BaseAdapterProxy findAdapterProxy(int type) {
        return mProxyMap.get(type);
    }

}
