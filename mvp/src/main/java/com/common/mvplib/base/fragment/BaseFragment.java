package com.common.mvplib.base.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.common.baselib.bean.BaseResponse;
import com.common.baselib.ui.dialog.LoadingHelper;
import com.common.mvplib.config.LayoutConfig;
import com.common.mvplib.mvp.IView;
import com.common.mvplib.widget.loadingview.CommonLoadingView;
import com.common.mvplib.widget.loadingview.EmptyType;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

/**
 * ================================================
 * 包名：com.common.mvplib.base.fragment
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:52
 * 描述：
 * ================================================
 */
public abstract class BaseFragment<BINDING extends ViewDataBinding> extends Fragment implements View.OnClickListener, IView {

    public BINDING mBinding;
    protected View view;
    public Activity mBaseContext;
    public Bundle savedInstanceState;
    protected volatile LoadingHelper mDialogLoadingHelper;
    public LayoutConfig layoutConfig;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mBaseContext = activity;
        layoutConfig = LayoutConfig.of(initLayoutConfig());
        layoutConfig.setBaseFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        view = layoutConfig.initContent();
        if (view != null) {
            dealLogicAfterInitView();
            setListener();
            initData();
        }
        return view;
    }

    public void setListener() {

    }

    /**
     * 初始化布局
     */
    public abstract int getLayoutId();

    public View initView() {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), getLayoutId(), null, false);
        return mBinding.getRoot();
    }

    /**
     * 初始化布局之后的操作
     */
    public abstract void dealLogicAfterInitView();

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 配置 topbar and loading
     *
     * @return
     */
    public abstract LayoutConfig initLayoutConfig();

    /**
     * 点击事件
     *
     * @param view
     */
    public void onclickEvent(View view) {
    }


    @Override
    public void onClick(View v) {
        onclickEvent(v);
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentShowOrResumeRefreshData();
    }

    /**
     * 首页四个fragment，当前显示的fragment是这个fragment时刷新数据
     */
    protected void fragmentShowOrResumeRefreshData() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            fragmentShowOrResumeRefreshData();
        }
    }

    public boolean backFragment() {
        return false;
    }


    public ViewDataBinding getBinding() {
        return DataBindingUtil.inflate(mBaseContext.getLayoutInflater(), getLayoutId(), null, false);
    }

    public void showLoading(String loadingMsg) {
        layoutConfig.loading.showLoading();
    }

    public void showEmpty(EmptyType emptyType) {
        layoutConfig.loading.showEmpty(emptyType);
    }

    public void onLoadSuccess() {
        layoutConfig.loading.disLoading();
    }

    public void onLoadingError(BaseResponse error, boolean isRefresh) {
        if (isRefresh) {
            layoutConfig.loading.showError(error);
        } else {
            CommonLoadingView.showErrorToast(error);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void showLoadingDialog(String message, boolean canceledOnTouchOutside, boolean cancleable, DialogInterface.OnDismissListener onDismissListener) {
        if (mDialogLoadingHelper == null) {
            mDialogLoadingHelper = new LoadingHelper();
        }
        try {
            mDialogLoadingHelper.showIfNotExist(this, message, canceledOnTouchOutside, cancleable, onDismissListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoadingDialog(boolean canceledOnTouchOutside) {
        showLoadingDialog("", canceledOnTouchOutside, true, null);
    }

    public void showLoadingDialog(String message, boolean canceledOnTouchOutside) {
        showLoadingDialog(message, canceledOnTouchOutside, true, null);
    }

    public void dissLoadingDialog() {
        if (mDialogLoadingHelper == null) {
            return;
        }
        try {
            mDialogLoadingHelper.dismissIfExist();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
