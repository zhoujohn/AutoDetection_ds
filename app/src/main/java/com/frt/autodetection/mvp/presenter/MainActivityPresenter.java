package com.frt.autodetection.mvp.presenter;

import android.app.Activity;

import com.common.mvplib.mvp.AbsPresenter;
import com.frt.autodetection.mvp.iview.IMainActivityView;

/**
 * ================================================
 * 包名：com.cts.hltravel.client.mvp.presenter
 * 创建人：sws
 * 创建时间：2019/5/31  下午 06:21
 * 描述：
 * ================================================
 */
public class MainActivityPresenter extends AbsPresenter<IMainActivityView> {


    public MainActivityPresenter(IMainActivityView view, Activity activity) {
        super(view, activity);
    }



}
