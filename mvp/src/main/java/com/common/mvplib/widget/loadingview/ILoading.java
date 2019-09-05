package com.common.mvplib.widget.loadingview;

import android.content.Context;
import android.util.AttributeSet;

import com.common.baselib.bean.BaseResponse;
import com.common.mvplib.listener.Action;


/**
 * ================================================
 * 包名：com.common.mvplib.widget.loadingview
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:57
 * 描述：
 * ================================================
 */
public interface ILoading {

    /**
     * 获取布局id
     */
    int getLayoutId(AttributeSet attrs);

    /**
     * 显示加载框
     */
    void showLoading();

    /**
     * 隐藏加载框，加载结束
     */
    void disLoading();

    /**
     * 数据为空
     *
     * @param emptyType
     */
    void showEmpty(EmptyType emptyType);

    /**
     * 出错
     */
    void showError(BaseResponse errorBean);

    /**
     * 创建 emptyView
     * @param context
     * @param attrs
     * @return
     */
    void createEmptyView(Context context, AttributeSet attrs);

    void setRefreshCallback(Action.Action1<Integer> onRefresh);
}
