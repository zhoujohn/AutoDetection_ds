package com.common.baselib.widget.ptr.pageload;

import java.util.List;

/**
 * Created by wdf on 2016/11/9.
 */
public interface IPageload<T> {
    /**
     * 处理请求数据
     * @param isRefresh
     * @param templist
     */
    public void dealResult(boolean isRefresh, List<T> templist);

    /**
     * 获取请求页码
     * @return
     */
    public int getPageIndex(boolean isRefresh);
}
