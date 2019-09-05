package com.common.mvplib.config;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.common.baselib.widget.KeyboardLayout;
import com.common.mvplib.base.activity.BaseActivity;
import com.common.mvplib.base.fragment.BaseFragment;
import com.common.mvplib.listener.Action;
import com.common.mvplib.widget.CommonTopBar;
import com.common.mvplib.widget.loadingview.CommonLoadingView;

/**
 * ================================================
 * 包名：com.common.mvplib.config
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:50
 * 描述：
 * ================================================
 */
public class LayoutConfig {
    public BaseActivity baseActivity;
    public BaseFragment baseFragment;

    public CommonTopBar topBar;
    public CommonLoadingView loading;
    public Builder builder;

    private int type = 0;

    public LayoutConfig(Builder builder) {
        this.builder = builder;
    }

    public void setBaseFragment(BaseFragment baseFragment) {
        this.baseFragment = baseFragment;
        type = 1;
    }

    public void setBaseActivity(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public View initContent() {
        Context context = (type == 0 ? baseActivity : baseFragment.mBaseContext);
        View contentView = (type == 0 ? baseActivity.initView() : baseFragment.initView());
        ViewGroup.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(params);

        View baseView = View.inflate(context, builder.init.getLayoutId(), null);
        builder.init.setConfig(builder, baseView);
        topBar = builder.init.getTopBar();
        loading = builder.init.getLoading();

        if (builder.isLoadingBottom) {
            builder.init.getConView().bringToFront();
        }
        builder.init.getConView().addView(contentView);
        return baseView;
    }

    public static final class Builder {

        public boolean isShowConTopBar;
        public boolean isFitSystemWindow = false;
        public boolean isShowConLoading;
        public boolean isShowBottomLine = true;
        public int loadingMarginTop = 0;
        public int topBarMarginTop = 0;
        public KeyboardLayout.OnKeyboardChangedListener onKeyboardChangedListener;

        public String title;
        public int rightImgRes;
        public int leftImgRes;
        public String rightText;

        public int bgColorRes;

        public boolean isLoadingBottom = false;

        public Action.Action1<Integer> onRefresh;

        public IBaseLayoutInit init = new CommonInit(this);

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder rightImgRes(int rightImgRes) {
            this.rightImgRes = rightImgRes;
            return this;
        }

        public Builder bgColorRes(int bgColorRes) {
            this.bgColorRes = bgColorRes;
            return this;
        }

        public Builder leftImgRes(int leftImgRes) {
            this.leftImgRes = leftImgRes;
            return this;
        }

        public Builder rightText(String rightText) {
            this.rightText = rightText;
            return this;
        }

        public Builder isConTopBar(boolean isConTopBar) {
            this.isShowConTopBar = isConTopBar;
            return this;
        }

        public Builder isConLoading(boolean isConLoading) {
            this.isShowConLoading = isConLoading;
            return this;
        }

        public Builder isShowBottomLine(boolean isShowBottomLine) {
            this.isShowBottomLine = isShowBottomLine;
            return this;
        }

        public Builder isLoadingBottom(boolean isLoadingBottom) {
            this.isLoadingBottom = isLoadingBottom;
            return this;
        }

        public Builder onRefresh(Action.Action1<Integer> onRefresh) {
            this.onRefresh = onRefresh;
            return this;
        }

        public Builder loadingMarginTop(int loadingMarginTop) {
            this.loadingMarginTop = loadingMarginTop;
            return this;
        }

        public Builder topBarMarginTop(int topBarMarginTop) {
            this.topBarMarginTop = topBarMarginTop;
            return this;
        }

        public Builder onKeyboardChangedListener(KeyboardLayout.OnKeyboardChangedListener onKeyboardChangedListener) {
            this.onKeyboardChangedListener = onKeyboardChangedListener;
            return this;
        }

        public Builder init(IBaseLayoutInit init) {
            this.init = init;
            return this;
        }

        public Builder isFitSystemWindow(boolean isFitSystemWindow) {
            this.isFitSystemWindow = isFitSystemWindow;
            return this;
        }

        public LayoutConfig build() { // 构建，返回一个新对象
            return new LayoutConfig(this);
        }
    }

    public static LayoutConfig of(LayoutConfig config) {
        return config != null
                ? config
                : new LayoutConfig.Builder().isConLoading(false).isConTopBar(false).build();
    }

    public static LayoutConfig initConfig(int loadingMarginTop) {
        return new LayoutConfig.Builder()
                .isConLoading(true)
                .isConTopBar(false)
                .loadingMarginTop(loadingMarginTop)
                .build();
    }

    public static LayoutConfig initConfig(boolean isConLoading, boolean isConTopBar, String title) {
        return new LayoutConfig.Builder()
                .isConLoading(isConLoading)
                .isConTopBar(isConTopBar)
                .title(title)
                .build();
    }

    public static LayoutConfig initConfig(
            boolean isConLoading, boolean isConTopBar, String title, int leftImgRes) {
        return new LayoutConfig.Builder()
                .isConLoading(isConLoading)
                .isConTopBar(isConTopBar)
                .title(title)
                .leftImgRes(leftImgRes)
                .build();
    }

    public static LayoutConfig initConfig(
            boolean isConLoading,
            boolean isConTopBar,
            String title,
            int leftImgRes,
            String rightText) {
        return new LayoutConfig.Builder()
                .isConLoading(isConLoading)
                .isConTopBar(isConTopBar)
                .title(title)
                .leftImgRes(leftImgRes)
                .rightText(rightText)
                .build();
    }

    public static LayoutConfig initConfig(
            boolean isConLoading,
            boolean isConTopBar,
            String title,
            int leftImgRes,
            int rightImgRes) {
        return new LayoutConfig.Builder()
                .isConLoading(isConLoading)
                .isConTopBar(isConTopBar)
                .title(title)
                .leftImgRes(leftImgRes)
                .rightImgRes(rightImgRes)
                .build();
    }

    /**
     * 好妈妈下面没有那一条线
     *
     * @param isConLoading
     * @param isConTopBar
     * @param title
     * @return
     */
    public static LayoutConfig initConfig(
            boolean isConLoading, boolean isConTopBar, String title, boolean isShowBottomLine) {
        return new Builder()
                .isConLoading(isConLoading)
                .isConTopBar(isConTopBar)
                .title(title)
                .isShowBottomLine(isShowBottomLine)
                .build();
    }

    public static LayoutConfig initConfig(
            boolean isConLoading,
            boolean isConTopBar,
            String title,
            int leftImgRes,
            boolean isShowBottomLine) {
        return new LayoutConfig.Builder()
                .isConLoading(isConLoading)
                .isConTopBar(isConTopBar)
                .title(title)
                .leftImgRes(leftImgRes)
                .isShowBottomLine(isShowBottomLine)
                .build();
    }

    public static LayoutConfig initConfig(
            boolean isConLoading,
            boolean isConTopBar,
            String title,
            int leftImgRes,
            String rightText,
            boolean isShowBottomLine) {
        return new LayoutConfig.Builder()
                .isConLoading(isConLoading)
                .isConTopBar(isConTopBar)
                .title(title)
                .leftImgRes(leftImgRes)
                .rightText(rightText)
                .isShowBottomLine(isShowBottomLine)
                .build();
    }

    public static LayoutConfig initConfig(
            boolean isConLoading,
            boolean isConTopBar,
            String title,
            int leftImgRes,
            int rightImgRes,
            boolean isShowBottomLine) {
        return new LayoutConfig.Builder()
                .isConLoading(isConLoading)
                .isConTopBar(isConTopBar)
                .title(title)
                .leftImgRes(leftImgRes)
                .rightImgRes(rightImgRes)
                .isShowBottomLine(isShowBottomLine)
                .build();
    }
}

