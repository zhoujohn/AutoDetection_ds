package com.common.baselib.bean;

import java.io.Serializable;

/**
 * ================================================
 * 包名：com.common.baselib.bean
 * 创建人：sws
 * 创建时间：2019/5/27  下午 04:23
 * 描述：
 * ================================================
 */
public class BaseBean implements Serializable {

    public int code;
    public String message;
    public String msg;
    public String originResultString;

    public static BaseBean createError(int code, String msg) {
        BaseBean bean = new BaseBean();
        bean.msg = msg;
        bean.code = code;
        return bean;
    }
}
