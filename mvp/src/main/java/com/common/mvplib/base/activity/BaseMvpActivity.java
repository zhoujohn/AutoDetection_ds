package com.common.mvplib.base.activity;

import androidx.databinding.ViewDataBinding;
import android.os.Bundle;

import com.common.mvplib.mvp.AbsPresenter;

import androidx.annotation.Nullable;

/**
 * ================================================
 * 包名：com.common.mvplib.base.activity
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:51
 * 描述：
 * ================================================
 */
public abstract class BaseMvpActivity<P extends AbsPresenter, BINDING extends ViewDataBinding> extends BaseActivity<BINDING> {

    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = newPresenter();
        if (mPresenter != null) {
            mPresenter.attachView();
            getLifecycle().addObserver(mPresenter);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            getLifecycle().removeObserver(mPresenter);
        }
    }

    protected abstract P newPresenter();

}


