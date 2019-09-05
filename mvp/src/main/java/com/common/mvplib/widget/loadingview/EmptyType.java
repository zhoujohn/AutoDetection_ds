package com.common.mvplib.widget.loadingview;

import com.common.mvplib.R;

/**
 * ================================================
 * 包名：com.common.mvplib.widget.loadingview
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:56
 * 描述：
 * ================================================
 */
public enum EmptyType {
    /**
     * default
     */
    OPT_DEFAULT(R.mipmap.default_icon);

    /**
     * 图标
     */
    public int icon;
    /**
     * 布局Id
     */
    public int layoutId;

    /**
     * 主信息
     */
    public String message;
    /**
     * 小信息
     */
    public String messageSub;

    private EmptyType(int icon, String message, String messageSub) {
        this.icon = icon;
        this.message = message;
        this.messageSub = messageSub;
    }

    private  EmptyType(int icon, String message) {
        this(icon, message, "");
    }

    private EmptyType(String message, String messageSub) {
        this(R.mipmap.default_icon, message, messageSub);
    }

    private EmptyType(int icon) {
        this(icon, "", "");
    }
}
