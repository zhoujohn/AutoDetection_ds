package com.common.mvplib.config;

import android.view.View;
import android.widget.LinearLayout;

import com.common.mvplib.widget.CommonTopBar;
import com.common.mvplib.widget.loadingview.CommonLoadingView;

/**
 * ================================================
 * 包名：com.common.mvplib.config
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:54
 * 描述：
 * ================================================
 */
public interface IBaseLayoutInit {

    int getLayoutId();

    View setConfig(LayoutConfig.Builder builder, View baseView);

    LinearLayout getConView();

    CommonLoadingView getLoading();

    CommonTopBar getTopBar();
}
