package com.common.mvplib.mvp;

import android.app.Activity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.common.baselib.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * ================================================
 * 包名：com.common.mvplib.mvp
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:53
 * 描述：
 * ================================================
 */
public abstract class AbsPresenter<T extends IView> implements LifecycleObserver {

    protected T mView;
    public Activity mActivity;
    private String mTagId;

    public AbsPresenter(T view, Activity activity) {
        this.mView = view;
        this.mActivity = activity;
    }

    /**
     * 绑定view，一般在初始化中调用该方法
     */
    public void attachView() {
        mTagId = getTagId();
        try {
            if (isRegisterEvent()) {
                if (!EventBus.getDefault().isRegistered(this)) {
                    EventBus.getDefault().register(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开view，一般在onDestroy中调用
     */
    public void detachView() {
        try {
            if (isRegisterEvent()) {
                EventBus.getDefault().unregister(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.mView = null;
        }
    }

    /**
     * 是否与View建立连接
     * 每次调用业务请求的时候都要出先调用方法检查是否与View建立连接
     */
    public boolean isViewAttached() {
        return mView != null;
    }


    /**
     * 是否需要注册EventBus
     *
     * @return
     */
    protected boolean isRegisterEvent() {
        return false;
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {

    }

    public String getTagId() {
        if (!StringUtils.isNull(mTagId)) {
            return mTagId;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getClass().getName())
                .append("_")
                .append(this.hashCode());
        return stringBuilder.toString();
    }

}

