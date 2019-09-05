package com.common.baselib.action;

import org.greenrobot.eventbus.EventBus;

/**
 * ================================================
 * 包名：com.common.baselib.action
 * 创建人：sws
 * 创建时间：2019/5/27  下午 04:23
 * 描述：
 * ================================================
 */
public class BaseAction<T> {

    public int type;
    public String code;
    public T data;

    public BaseAction(int type, T data, String code) {
        this.type = type;
        this.data = data;
        this.code = code;
    }

    public static <T> BaseAction of(int type, T data) {
        return new BaseAction(type, data, "");
    }

    public static <T> BaseAction of(int type, T data, String code) {
        return new BaseAction(type, data, code);
    }


    public static <U extends BaseAction<T>, T> void sendAction(U action) {
        EventBus.getDefault().post(action);
    }


}
