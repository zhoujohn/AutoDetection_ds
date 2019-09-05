package com.common.mvplib.mvp;

import com.common.baselib.bean.BaseResponse;
import com.common.mvplib.widget.loadingview.EmptyType;

/**
 * ================================================
 * 包名：com.common.mvplib.mvp
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:53
 * 描述：
 * ================================================
 */
public interface IView {

    void showLoading(String loadingMsg);

    void showEmpty(EmptyType emptyType);

    void onLoadSuccess();

    void onLoadingError(BaseResponse error, boolean isRefresh);

    void showLoadingDialog(boolean canceledOnTouchOutside);

    void showLoadingDialog(String message, boolean canceledOnTouchOutside);

    void dissLoadingDialog();

}
