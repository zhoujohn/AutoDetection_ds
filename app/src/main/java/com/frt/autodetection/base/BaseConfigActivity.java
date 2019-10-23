package com.frt.autodetection.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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
        //bind 服务成功后初始化 串口缺省设置
        mSerialPortTerminal.setOnSerialServiceBindListener(new SerialPortTerminal.OnSerialServiceBindListener() {
            @Override
            public void OnSerialServiceBind() {
                initParamData();
            }
        });
        //透明底部导航栏
        secondHide();
    }

    protected abstract void initParamData();

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

    //延时毫秒数
    private static final long COLLAPSE_SB_PERIOD = 100;
    //id
    private static final int COLLAPSE_STATUS_BAR = 1000;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COLLAPSE_STATUS_BAR:
                    collapse(BaseConfigActivity.this, true);
                    sendEmptyMessageDelayed(COLLAPSE_STATUS_BAR, COLLAPSE_SB_PERIOD);
                    break;
                default:
                    break;
            }
        }

    };

    public static void collapse(Activity activity, boolean enable) {
        Window window = activity.getWindow();
        if (enable) {
            WindowManager.LayoutParams attr = window.getAttributes();
            window.setAttributes(attr);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            int flags = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(flags | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);

            attr.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            WindowManager.LayoutParams attr = window.getAttributes();
            attr.flags &= (WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(attr);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public void secondHide() {
        int flags = getWindow().getDecorView().getSystemUiVisibility();
        getWindow().getDecorView().setSystemUiVisibility(flags | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        mHandler.sendEmptyMessageDelayed(COLLAPSE_STATUS_BAR, COLLAPSE_SB_PERIOD);

    }

}
