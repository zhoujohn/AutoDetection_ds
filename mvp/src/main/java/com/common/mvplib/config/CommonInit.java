package com.common.mvplib.config;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.common.baselib.utils.DensityUtils;
import com.common.baselib.widget.KeyboardLayout;
import com.common.mvplib.R;
import com.common.mvplib.widget.CommonTopBar;
import com.common.mvplib.widget.loadingview.CommonLoadingView;

/**
 * ================================================
 * 包名：com.common.mvplib.config
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:53
 * 描述：
 * ================================================
 */
public class CommonInit implements IBaseLayoutInit {

    LayoutConfig.Builder builder;

    public CommonInit(LayoutConfig.Builder builder) {
        this.builder = builder;
    }

    public CommonTopBar topBar;
    public CommonLoadingView loading;
    public KeyboardLayout baseCon;
    public LinearLayout ll_cons;

    @Override
    public int getLayoutId() {
        if (builder.isFitSystemWindow) {
            return R.layout.layout_base_fit_sys_window;
        } else {
            return R.layout.layout_base;
        }
    }

    @Override
    public View setConfig(LayoutConfig.Builder builder, View baseView) {

        baseCon = baseView.findViewById(R.id.baseCon);
        topBar = (CommonTopBar) baseView.findViewById(R.id.commonTopBar);
        loading = (CommonLoadingView) baseView.findViewById(R.id.loading);
        ll_cons = (LinearLayout) baseView.findViewById(R.id.ll_cons);

        if (builder.isShowConLoading) {
            RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) loading.getLayoutParams();
            pa.setMargins(0, DensityUtils.dip2px(builder.loadingMarginTop), 0, 0);
            loading.setLayoutParams(pa);

            loading.setRefreshCallback(builder.onRefresh);
        } else {
            baseCon.removeView(loading);
        }

        if (builder.isShowConTopBar) {
            topBar.title(builder.title)
                    .leftImgRes(builder.leftImgRes)
                    .rightImgRes(builder.rightImgRes)
                    .rightText(builder.rightText);

            RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
            pa.setMargins(0, DensityUtils.dip2px(builder.topBarMarginTop), 0, 0);
            topBar.setLayoutParams(pa);

        } else {
            baseCon.removeView(topBar);
        }

        baseCon.setOnKeyboardChangedListener(builder.onKeyboardChangedListener);
        baseCon.setBackgroundResource(builder.bgColorRes);
        topBar.isShowBottomLine(builder.isShowBottomLine);
        return baseView;
    }

    @Override
    public LinearLayout getConView() {
        return ll_cons;
    }

    @Override
    public CommonLoadingView getLoading() {
        return loading;
    }

    @Override
    public CommonTopBar getTopBar() {
        return topBar;
    }
}
