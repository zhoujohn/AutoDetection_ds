package com.common.baselib.bean;

import java.io.Serializable;

/**
 * ================================================
 * 包名：com.common.baselib.bean
 * 创建人：sws
 * 创建时间：2019/5/27  下午 04:24
 * 描述：
 * ================================================
 */
public class BaseResponse implements Serializable {

    public String error;
    public String msg;

    public static BaseResponse createError(String error, String msg) {
        BaseResponse bean = new BaseResponse();
        bean.msg = msg;
        bean.error = error;
        return bean;
    }
}
