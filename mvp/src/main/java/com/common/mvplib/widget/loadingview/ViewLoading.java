package com.common.mvplib.widget.loadingview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.common.mvplib.R;

/**
 * ================================================
 * 包名：com.common.mvplib.widget.loadingview
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:58
 * 描述：
 * ================================================
 */
public class ViewLoading extends LinearLayout implements ILoad {

    public LinearLayout ll_loading;
    public ImageView iv_loading;

    private Activity mContext;

    public ViewLoading(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) context;
        LayoutInflater mInflater = mContext.getLayoutInflater();
        mInflater.inflate(getLayoutId(attrs), this, true);

        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        iv_loading = (ImageView) findViewById(R.id.iv_loading);

        final AnimationDrawable mLoadingAinm = (AnimationDrawable) iv_loading.getBackground();
        iv_loading.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mLoadingAinm.start();
                return true;
            }
        });

        setClickable(true);
    }

    /**
     * 显示加载框
     */
    @Override
    public void showLoading() {

    }

    @Override
    public int getLayoutId(AttributeSet attrs) {
        return R.layout.view_data_loading;
    }


    public void setIsShowOnLoading(boolean isShow) {
        ll_loading.setBackgroundColor(isShow ? getResources().getColor(R.color.transparent) : getResources().getColor(R.color.white));
    }
}
