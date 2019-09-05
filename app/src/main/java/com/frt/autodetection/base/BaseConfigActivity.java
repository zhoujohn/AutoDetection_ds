package com.frt.autodetection.base;

import androidx.databinding.ViewDataBinding;
import android.os.Bundle;

import com.common.mvplib.base.activity.BaseActivity;
import com.common.mvplib.mvp.AbsPresenter;

import androidx.annotation.Nullable;

/**
 * 项目名称：HLTravel
 * 创建人：SWS
 * 创建时间：2019/5/13  下午 05:04
 * 描述：
 */
public abstract class BaseConfigActivity<P extends AbsPresenter, BINDING extends ViewDataBinding> extends BaseActivity<BINDING> {


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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
