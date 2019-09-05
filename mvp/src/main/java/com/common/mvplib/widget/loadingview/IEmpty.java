package com.common.mvplib.widget.loadingview;

import android.util.AttributeSet;

import com.common.mvplib.listener.Action;

/**
 * ================================================
 * 包名：com.common.mvplib.widget.loadingview
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:57
 * 描述：
 * ================================================
 */
public interface IEmpty {


    public abstract void initView();

    /**
     * 获取布局id
     */
    public abstract int getLayoutId();

    /**
     * 获取布局id
     */
    public abstract void dealAttribute(AttributeSet attrs);

    /**
     * 数据为空
     *
     * @param emptyType
     */
    public abstract void showEmpty(EmptyType emptyType);

    /**
     * 数据为空
     *
     * @param iconResId
     * @param message
     * @param subMsg
     */
    public abstract void showEmpty(int iconResId, String message, String subMsg);

    void showState(boolean isShow);

    void setIsShowOnLoading(boolean isShow);

    void setRefreshCallback(Action.Action1<Integer> action1);


}
