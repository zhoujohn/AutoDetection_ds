package com.common.baselib.widget.ptr.header;


import com.common.baselib.widget.ptr.base.PtrBase;
import com.common.baselib.widget.ptr.indicator.PtrIndicator;

/**
 * 头部状态变化的回调
 */
public interface IHeaderLayout {
    /**
     * 头部刷新重置
     *
     * @param frame
     */
    public void onHeaderReset(PtrBase frame);

    /**
     * 头部刷新准备
     *
     * @param frame
     */
    public void onHeaderRefreshPrepare(PtrBase frame);

    /**
     * 头部开始刷新
     */
    public void onHeaderRefresh(PtrBase frame);

    /**
     * 头部刷新完毕
     */
    public void onHeaderRefreshComplete(PtrBase frame);

    /**
     * 头部位置变化
     * @param frame
     * @param isUnderTouch
     * @param status
     * @param ptrIndicator
     */
    public void onHeaderPositionChange(PtrBase frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator);

}
