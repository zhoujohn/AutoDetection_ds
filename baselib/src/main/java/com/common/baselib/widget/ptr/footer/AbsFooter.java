package com.common.baselib.widget.ptr.footer;


import android.content.Context;
import android.widget.LinearLayout;


/**
 * PullToRefresh absFooter
 */
public abstract class AbsFooter extends LinearLayout implements IFooterLayout {

    /**
     * 加载失败的回调
     */
    public OnLoadFailListener onLoadFailListener;

    public AbsFooter(Context context, OnLoadFailListener onLoadFailListener) {
        super(context);
        this.onLoadFailListener = onLoadFailListener;
    }

    public interface OnLoadFailListener {
        public void onLoadFail();
    }

    public void setLoadingInVisiable() {

    }

    public void setLoadingEnable() {
    }
}
