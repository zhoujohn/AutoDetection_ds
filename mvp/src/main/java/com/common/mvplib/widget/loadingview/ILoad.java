package com.common.mvplib.widget.loadingview;

import android.util.AttributeSet;

/**
 * ================================================
 * 包名：com.common.mvplib.widget.loadingview
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:57
 * 描述：
 * ================================================
 */
public interface ILoad {

    /**
     * 获取布局id
     */
    int getLayoutId(AttributeSet attrs);

    /**
     * 加载
     */
    void showLoading();

}
