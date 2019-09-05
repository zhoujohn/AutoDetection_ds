package com.common.mvplib.base.fragment;

import androidx.databinding.ViewDataBinding;
import android.os.Bundle;

import com.common.mvplib.mvp.AbsPresenter;

import androidx.annotation.Nullable;

/**
 * ================================================
 * 包名：com.common.mvplib.base.fragment
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:52
 * 描述：
 * ================================================
 */
public abstract class BaseMvpFragment<T extends AbsPresenter, BINDING extends ViewDataBinding> extends BaseFragment<BINDING> {

    protected T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = newPresenter();
        if (mPresenter != null) {
            mPresenter.attachView();
            getLifecycle().addObserver(mPresenter);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            getLifecycle().removeObserver(mPresenter);
        }
    }

    protected abstract T newPresenter();
}

