package com.frt.autodetection.utils;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.frt.autodetection.R;

import androidx.annotation.NonNull;

/**
 * ================================================
 * 包名：com.frt.autodetection.utils
 * 创建人：sws
 * 创建时间：2019/9/10  下午 02:23
 * 描述：
 * ================================================
 */
public class  PaintUtil {

    // -------------------------------------------------------------
    //                        public method
    // -------------------------------------------------------------

    /**
     * create the paint object for drawing the crop window border.
     */
    public static Paint newBoarderPaint(float size, int color){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        paint.setStyle(Paint.Style.STROKE);

        return paint;
    }

    /**
     * creates the paint object for drawing the translucent overlay outside the crop window.
     *
     */
    public static Paint newSurroundingAreaOverlayPaint() {

        final Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setAntiAlias(true);

        return paint;
    }

    /**
     * create the paint object for drawing the crop window corner..
     */
    public static Paint newHandlerPaint(@NonNull Resources resources){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(resources.getColor(R.color.corner));
        paint.setStyle(Paint.Style.FILL);

        return paint;
    }

    /**
     * create the paint object for drawing the crop window corner..
     */
    public static Paint newGuideLinePaint(float size, int color){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(size);

        return paint;
    }
}