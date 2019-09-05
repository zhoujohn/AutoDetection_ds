package com.common.baselib.widget.ptr.footer;

/**
 * Created by wdf on 2016/10/12.
 */
public interface IFooterLayout {
    /**
     * 加载中
     */
    int STATE_LOADING = 0;
    /**
     * 正常状态
     */
    int STATE_NORMAL = 1;
    /**
     * 加载失败
     */
    int State_LOADING_FAIL = 2;

    /**
     * 设置高度
     *
     * @param hieght
     */
    public void setConHeight(int hieght);

    /**
     * 显示加载
     */
    public void showLoading();

    /**
     * 隐藏loading
     */
    public void hide();

    /**
     * 数据加载完毕，显示结果
     */
    public void showEndmsg(CharSequence endMsg);

    /**
     * 数据加载失败
     */
    public void onLoadingFail();

    /**
     * 数据加载失败
     */
    public int getLoadingStatus();

}
