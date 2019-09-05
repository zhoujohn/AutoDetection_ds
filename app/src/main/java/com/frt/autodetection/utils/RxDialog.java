package com.frt.autodetection.utils;

import com.frt.autodetection.mvp.ui.widget.dialog.CalDialog;
import com.frt.autodetection.mvp.ui.widget.dialog.SetDialog;

import androidx.fragment.app.FragmentActivity;

/**
 * ================================================
 * 包名：com.frt.autodetection.utils
 * 创建人：sws
 * 创建时间：2019/8/27  上午 11:48
 * 描述：
 * ================================================
 */
public class RxDialog {


    public static void showSetDialog(FragmentActivity activity, int level, int maxLevel, int minLevel, SetDialog.OnListener listener) {

        SetDialog.Builder builder = new SetDialog.Builder(activity);
        builder.setLevel(level)
                .setMaxLevel(maxLevel)
                .setMinLevel(minLevel)
                .setListener(listener)
                .show();
    }

    public static void showCalDialog(FragmentActivity activity, int type, CalDialog.OnListener listener) {

        CalDialog.Builder builder = new CalDialog.Builder(activity);
        builder.setType(type)
                .setListener(listener)
                .show();
    }
}
