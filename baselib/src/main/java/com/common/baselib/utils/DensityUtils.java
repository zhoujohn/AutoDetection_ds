package com.common.baselib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.baselib.AppConfig;

import java.lang.reflect.Method;

/**
 * 测量，测量单位转化
 */
public class DensityUtils {
    /**
     * px转DIP
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        final float scale = AppConfig.getInstance().mAppContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(float dpValue) {
        final float scale = AppConfig.getInstance().mAppContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = AppConfig.getInstance().mAppContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * 获取屏幕宽度
     *
     * @return
     * @date 2013年7月23日
     */
    public static int getWidth() {
        return AppConfig.getInstance().mAppContext.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     * @date 2013年7月23日
     */
    public static int getHeight() {
        return AppConfig.getInstance().mAppContext.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取底部蓝高度
     *
     * @return
     */
    public static int getNavigationBarHeight(Activity mActivity) {
        Resources resources = mActivity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        LogUtil.log("Navi height:" + height);
        return height;
    }

    /**
     * 底部蓝是否存在
     *
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;

    }

    public static int getTopBarHeight(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }


    /**
     * 设置图片包裹的宽高
     *
     * @param rl_pic
     * @param bitmap
     * @param context
     */
    public static void setPicParams(RelativeLayout rl_pic, Bitmap bitmap, Context context) {
        if (rl_pic != null) {
            int bitmapHeight = bitmap.getHeight();
            int bitmapWidth = bitmap.getWidth();

            int rlHeight = (int) (DensityUtils.getHeight() / 6f);
            RelativeLayout.LayoutParams rlpicParams = (RelativeLayout.LayoutParams) rl_pic.getLayoutParams();
            rlpicParams.height = rlHeight;

            if (bitmap.getHeight() / bitmap.getWidth() > 3) {
                rlpicParams.width = (int) (rlHeight * 0.7f);
            } else {
                rlpicParams.width = (int) (rlHeight * (bitmapWidth * 1f / bitmapHeight));
            }

            rl_pic.setLayoutParams(rlpicParams);
        }
    }

    /**
     * 设置图片包裹的宽高
     *
     * @param rl_pic
     * @param bitmap
     * @param context
     */
    public static void setVideoParams(RelativeLayout rl_pic, Bitmap bitmap, Context context) {
        if (rl_pic != null) {
            int bitmapHeight = bitmap.getHeight();
            int bitmapWidth = bitmap.getWidth();

            int rlHeight = (int) (DensityUtils.getHeight() / 4f);
            RelativeLayout.LayoutParams rlpicParams = (RelativeLayout.LayoutParams) rl_pic.getLayoutParams();
            rlpicParams.height = rlHeight;

            if (bitmap.getHeight() / bitmap.getWidth() > 3) {
                rlpicParams.width = (int) (rlHeight * 0.7f);
            } else {
                rlpicParams.width = (int) (rlHeight * (bitmapWidth * 1f / bitmapHeight));
            }

            rl_pic.setLayoutParams(rlpicParams);
        }
    }

    /**
     * 获取textView的宽度
     *
     * @param txt
     * @param content
     * @return
     */
    public float getTextViewWidth(TextView txt, String content) {
        Paint paint = txt.getPaint();
        return paint.measureText(content);
    }

    public static int getScreentHeight(Context context) {
        int heightPixels;
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        heightPixels = metrics.heightPixels;
        LogUtil.log("heightPixels <14:" + heightPixels);
        // includes window decorations (statusbar bar/navigation bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                heightPixels = (Integer) Display.class
                        .getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
            LogUtil.log("heightPixels 14-17:" + heightPixels);
        } else if (Build.VERSION.SDK_INT >= 17) {
            try {
                android.graphics.Point realSize = new android.graphics.Point();
                Display.class.getMethod("getRealSize",
                        android.graphics.Point.class).invoke(d, realSize);
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }
            LogUtil.log("heightPixels >17:" + heightPixels);
        }
        return heightPixels;
    }


    public static int getViewId(String idStr) {
        return AppConfig.getInstance().mAppContext.getResources().getIdentifier(idStr, "id", AppConfig.getInstance().mAppContext.getPackageName());
    }

    public static int getDrawableId(String idStr) {
        return AppConfig.getInstance().mAppContext.getResources().getIdentifier(idStr, "drawable", AppConfig.getInstance().mAppContext.getPackageName());
    }

    /**
     * 获取点击的Item的对应View，
     *
     * @param view
     * @return
     */
    public static ImageView getView(View view, Activity activity) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(activity);
        iv.setImageBitmap(cache);
        return iv;
    }

    public static int getTotalHeightofListView(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        if (mAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            if (i == 0) {

            } else {
                mView.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                //mView.measure(0, 0);
            }
            totalHeight += mView.getMeasuredHeight();
            LogUtil.log("数据" + i + ":" + String.valueOf(mView.getMeasuredHeight()));
        }
        int height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        LogUtil.log("listview总高度=" + height);
        return totalHeight;
    }

}
