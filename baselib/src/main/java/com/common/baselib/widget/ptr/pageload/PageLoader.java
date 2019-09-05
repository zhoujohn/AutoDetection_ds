package com.common.baselib.widget.ptr.pageload;


import com.common.baselib.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分页加载helper
 * Created by wdf on 2016/10/14.
 */
public class PageLoader<T> implements IPageload<T> {

    /**
     * 每页请求条数
     */
    private int pageSize = 10;
    /**
     * 总共请求的数据
     */
    public List<T> dataList = new ArrayList<T>();
    /**
     * 处理数据回调
     */
    private OnRequestSuccessCallback dealRequestDataCallback;

    public PageLoader(OnRequestSuccessCallback dealRequestDataCallback, int pageSize) {
        this.dealRequestDataCallback = dealRequestDataCallback;
        this.pageSize = pageSize;
    }

    public PageLoader(OnRequestSuccessCallback dealRequestDataCallback) {
        this(dealRequestDataCallback, 10);
    }

    /**
     * 获取请求页码
     *
     * @return
     */
    @Override
    public int getPageIndex(boolean isRefresh) {
        if (isRefresh || dataList.isEmpty()) {
            return 1;
        } else {
            if (0 == dataList.size() % getPageSize()) {
                return dataList.size() / getPageSize() + 1;
            } else {
                //TODO 有余数说明 请求完毕
                return -1;
            }
        }
    }

    /**
     * 处理请求结果
     */
    @Override
    public void dealResult(boolean isRefresh, List<T> templist) {
        final boolean isShowLoadMore = !(CommonUtils.isListNull(templist) || templist.size() < getPageSize());
        if (isRefresh) {
            dataList.clear();
        }
        if (!CommonUtils.isListNull(templist)) {
            dataList.addAll(templist);
        }
        if (dealRequestDataCallback != null) {
            dealRequestDataCallback.onRequestSuccess(templist, dataList, isShowLoadMore);
        }
    }

    /**
     * 处理请求结果
     */
    public void dealResult(boolean isRefresh, List<T> templist, boolean hasNext) {
        if (isRefresh) {
            dataList.clear();
        }
        if (!CommonUtils.isListNull(templist)) {
            dataList.addAll(templist);
        }
        if (dealRequestDataCallback != null) {
            dealRequestDataCallback.onRequestSuccess(templist, dataList, hasNext);
        }
    }

    /**
     * 处理请求结果(向上累加)
     */
    public void dealResult(List<T> templist, boolean hasNext) {
        if (!CommonUtils.isListNull(templist)) {
            Collections.reverse(templist);
            dataList.addAll(0, templist);
        }
        if (dealRequestDataCallback != null) {
            dealRequestDataCallback.onRequestSuccess(templist, dataList, hasNext);
        }
    }

    public interface OnRequestSuccessCallback<T> {
        /**
         * 处理请求数据
         *
         * @param templist
         * @param dataList
         */
        public void onRequestSuccess(List<T> templist, List<T> dataList, boolean isShowLoadMore);
    }

    /**
     * 每次请求的条数
     */
    public int getPageSize() {
        return pageSize;
    }
}
