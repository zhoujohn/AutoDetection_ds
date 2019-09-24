package com.frt.autodetection.base;

import android.os.Bundle;

import com.common.mvplib.base.activity.BaseActivity;
import com.common.mvplib.mvp.AbsPresenter;
import com.frt.autodetection.serial.OnKeyEventReceiveListener;
import com.frt.autodetection.serial.SerialPortTerminal;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

/**
 * 项目名称：HLTravel
 * 创建人：SWS
 * 创建时间：2019/5/13  下午 05:04
 * 描述：
 */
public abstract class BaseConfigActivity<P extends AbsPresenter, BINDING extends ViewDataBinding> extends BaseActivity<BINDING> {


    protected P mPresenter;
    protected SerialPortTerminal mSerialPortTerminal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = newPresenter();
        if (mPresenter != null) {
            mPresenter.attachView();
            getLifecycle().addObserver(mPresenter);
        }
        super.onCreate(savedInstanceState);
        mSerialPortTerminal = SerialPortTerminal.getInstance();
        mSerialPortTerminal.registerEvent(this, setOnKeyEventReceiveListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            getLifecycle().removeObserver(mPresenter);
        }
        mSerialPortTerminal.unRegisterEvent(this, setOnKeyEventReceiveListener());
    }
    public abstract OnKeyEventReceiveListener setOnKeyEventReceiveListener();


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
