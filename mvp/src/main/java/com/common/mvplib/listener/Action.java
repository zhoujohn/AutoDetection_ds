package com.common.mvplib.listener;

/**
 * ================================================
 * 包名：com.common.mvplib.listener
 * 创建人：sws
 * 创建时间：2019/5/27  下午 05:28
 * 描述：
 * ================================================
 */
public class Action {

    public interface Action1<T> {
        void call(T t);
    }
}
