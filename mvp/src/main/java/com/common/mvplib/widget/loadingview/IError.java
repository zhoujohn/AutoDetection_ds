package com.common.mvplib.widget.loadingview;

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
public interface IError {

    /**
     * 获取布局id
     */
    int getLayoutId(AttributeSet attrs);
    /**
     * 出错
     */
    void showError(BaseResponse errorBean);

    void setRefreshListener(Action.Action1<Integer> onRefresh);
}
