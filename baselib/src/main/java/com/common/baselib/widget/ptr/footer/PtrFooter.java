package com.common.baselib.widget.ptr.footer;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.baselib.R;
import com.common.baselib.utils.DensityUtils;


/**
 * PullToRefresh footer
 */
public class PtrFooter extends AbsFooter {
    /**
     * 加載狀態
     */
    public int loadingState = STATE_NORMAL;

    private Context mContext;

    private LinearLayout ll_loading;
    /**
     * 加载动画
     */
    private ImageView loading;
    /**
     * 加載提示
     */
    private TextView txtLoading;
    /**
     * 加載的狀態
     */
    private TextView txtStatusMsg;

    public RelativeLayout con_rl;

    public PtrFooter(Context context, OnLoadFailListener onLoadFailListener) {
        super(context, onLoadFailListener);
        initView(context);
    }

    public PtrFooter(Context context) {
        super(context, null);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        View.inflate(mContext, R.layout.ptr_footer, this);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        loading = (ImageView) findViewById(R.id.loading);
        txtLoading = (TextView) findViewById(R.id.txtLoading);
        txtStatusMsg = (TextView) findViewById(R.id.txtStatusMsg);
        con_rl = (RelativeLayout) findViewById(R.id.con_rl);

        con_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadingState == State_LOADING_FAIL) {
                    if (onLoadFailListener != null) {
                        onLoadFailListener.onLoadFail();
                    }
                }
            }
        });

        final AnimationDrawable mLoadingAinm = (AnimationDrawable) loading.getBackground();
        loading.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mLoadingAinm.start();
                return true;
            }
        });
    }

    @Override
    public void setConHeight(int hieght) {
        LayoutParams params = (LayoutParams) con_rl.getLayoutParams();
        params.height = hieght + DensityUtils.dip2px(45);
        con_rl.setLayoutParams(params);
    }

    /**
     * 显示加载
     */
    @Override
    public void showLoading() {
        setState(STATE_LOADING);
        ll_loading.setVisibility(View.VISIBLE);
        txtStatusMsg.setVisibility(View.GONE);
    }

    /**
     * 隐藏loading
     */
    @Override
    public void hide() {
        setState(STATE_NORMAL);
        ll_loading.setVisibility(View.GONE);
        txtStatusMsg.setVisibility(View.GONE);
    }

    @Override
    public void showEndmsg(CharSequence endMsg) {
        ll_loading.setVisibility(View.GONE);
        txtStatusMsg.setVisibility(View.VISIBLE);
        if (!(endMsg == null || endMsg.length() == 0)) {
            txtStatusMsg.setText(endMsg);
            if (endMsg instanceof SpannableString) {
                txtStatusMsg.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
                txtStatusMsg.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件
            }
        }
    }

    @Override
    public void onLoadingFail() {
        showEndmsg("加载失败，点击重试");
        setState(State_LOADING_FAIL);
    }

    @Override
    public int getLoadingStatus() {
        return loadingState;
    }

    /**
     * 设置当前加载状态
     *
     * @param state
     */
    public void setState(int state) {
        this.loadingState = state;
    }

    public void setLoadingEnable() {
        ll_loading.setVisibility(View.VISIBLE);
        txtStatusMsg.setVisibility(View.GONE);
    }
}
