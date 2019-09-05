package com.common.mvplib.widget.loadingview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.common.baselib.bean.BaseResponse;
import com.common.baselib.utils.RxToast;
import com.common.baselib.utils.StringUtils;
import com.common.mvplib.R;
import com.common.mvplib.listener.Action;

/**
 * ================================================
 * 包名：com.common.mvplib.widget.loadingview
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:56
 * 描述：
 * ================================================
 */
public class CommonLoadingView extends LinearLayout implements ILoading {

    private Activity mContext;

    public boolean isLoading = false;
    private boolean isFirstRequest = true;

    public ViewLoading vLoading;
    public IEmpty vEmpty;
    public ViewError vError;
    public RelativeLayout loadCon;

    /**
     * 空类型
     */
    public int emptyType;

    public CommonLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) context;
        LayoutInflater mInflater = mContext.getLayoutInflater();
        mInflater.inflate(getLayoutId(attrs), this, true);
        initView();
        dealAttributes(attrs);
        createEmptyView(context, attrs);
    }

    public void initView() {
        vLoading = (ViewLoading) findViewById(R.id.vLoading);
        vError = (ViewError) findViewById(R.id.vError);
        loadCon = (RelativeLayout) findViewById(R.id.loadCon);
    }

    private boolean isShowBackBgLoading;
    private boolean isShowBackBgEmpty;
    private boolean isShowBackBgError;

    public void dealAttributes(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CommonLoadingView);
        /**
         * 加载时不显示底部
         */
        if (ta.hasValue(R.styleable.CommonLoadingView_isShowBackBgLoading)) {
            isShowBackBgLoading = ta.getBoolean(R.styleable.CommonLoadingView_isShowBackBgLoading, false);
            vLoading.setIsShowOnLoading(isShowBackBgLoading);
        }
        if (ta.hasValue(R.styleable.CommonLoadingView_isShowBackBgEmpty)) {
            isShowBackBgEmpty = ta.getBoolean(R.styleable.CommonLoadingView_isShowBackBgLoading, false);
        }
        if (ta.hasValue(R.styleable.CommonLoadingView_isShowBackBgError)) {
            isShowBackBgError = ta.getBoolean(R.styleable.CommonLoadingView_isShowBackBgLoading, false);
            vError.setIsShowOnLoading(isShowBackBgError);
        }
        if (ta.hasValue(R.styleable.CommonLoadingView_loadEmptyType)) {
            emptyType = ta.getInteger(R.styleable.CommonLoadingView_loadEmptyType, 0);
        }
    }

    /**
     * 显示加载框
     */
    @Override
    public void showLoading() {
        if (isFirstRequest) {
            vLoading.setVisibility(View.VISIBLE);
            vEmpty.showState(false);
            vError.setVisibility(View.GONE);
            isLoading = true;
        }
    }

    /**
     * 隐藏加载框，加载结束
     */
    @Override
    public void disLoading() {
        vLoading.setVisibility(View.GONE);
        vEmpty.showState(false);
        vError.setVisibility(View.GONE);

        isLoading = false;
        isFirstRequest = false;
    }

    /**
     * 数据加载为空，显示全屏
     *
     * @param iconResId 显示的图片资源
     * @param message   显示的信息
     */
    public void showEmpty(int iconResId, String message, String subMsg) {
        isLoading = false;
        isFirstRequest = false;

        vLoading.setVisibility(View.GONE);
        vError.setVisibility(View.GONE);
        vEmpty.showState(true);
        vEmpty.showEmpty(iconResId, message, subMsg);
    }

    public void showEmpty(String message, String meaageSamll) {
        showEmpty(0, message, meaageSamll);
    }

    public void showEmpty(EmptyType emptyType) {
        showEmpty(emptyType.icon, emptyType.message, emptyType.messageSub);
    }

    @Override
    public void showError(BaseResponse errorBean) {
        vLoading.setVisibility(View.GONE);
        vEmpty.showState(false);
        vError.setVisibility(View.VISIBLE);

        vError.showError(errorBean);

        isLoading = false;
        isFirstRequest = false;
    }

    @Override
    public void createEmptyView(Context context, AttributeSet attrs) {
        vEmpty = new ViewEmpty(context, attrs);
        vEmpty.setIsShowOnLoading(isShowBackBgEmpty);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        loadCon.addView((View) vEmpty, params);
        vEmpty.showState(false);
    }

    @Override
    public void setRefreshCallback(Action.Action1<Integer> onRefresh) {
        vEmpty.setRefreshCallback(onRefresh);
        vError.setRefreshListener(onRefresh);
    }


    @Override
    public int getLayoutId(AttributeSet attrs) {
        return R.layout.view_net_loading;
    }


    /**
     * 弹出错误信息
     *
     * @param error
     */
    public static void showErrorToast(BaseResponse error) {
        try {
            String resultCode = error.error;
            String message = StringUtils.isNotNull(error.msg) ? error.msg : error.msg;
//            if (VolleyErrorHelper.ExceptionMap.containsKey(resultCode)) {
//                message = VolleyErrorHelper.ExceptionMap.get(resultCode);
//            }
            RxToast.error(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

