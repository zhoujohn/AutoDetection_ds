package com.frt.autodetection;

import android.app.Application;
import android.content.Context;

import com.common.baselib.AppConfig;
import com.frt.autodetection.serial.SerialPortTerminal;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;

/**
 * ================================================
 * 包名：com.cts.hltravel.client
 * 创建人：sws
 * 创建时间：2019/5/28  下午 10:25
 * 描述：
 * ================================================
 */
//@InitContext
public class App extends Application {
    private static Context sContext;
    private SerialPortTerminal mSerialPortTerminal;
    //TODO  onCreate 走了两次 需要处理一下 DataInit
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        //初始化工具类
        AppConfig.getInstance().init(getApplicationContext());
        //初始化界面侧滑
        BGASwipeBackHelper.init(this, null);

        mSerialPortTerminal = SerialPortTerminal.getInstance();
    }

    public static Context getAppContext() {
        return sContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mSerialPortTerminal.releaseControllerServicesResources();
    }
}
