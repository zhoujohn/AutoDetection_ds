/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frt.autodetection.mvp.ui.widget.calibration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.frt.autodetection.R;
import com.frt.autodetection.constant.AppInfo;
import com.frt.autodetection.utils.helper.SpHelper;

/*
 * Modified from version in AOSP.
 *
 * This class is used to display a highlighted cropping rectangle
 * overlayed on the image. There are two coordinate spaces in use. One is
 * image, another is screen. computeLayout() uses matrix to map from image
 * space to screen space.
 */
public class CalibrationView extends View implements View.OnTouchListener {
    public final String TAG = "CalibrationView";
    protected int screenWidth;
    protected int screenHeight;
    protected int lastX;
    protected int lastY;
    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;
    private int dragDirection;
    private static final int TOP = 0x15;
    private static final int LEFT = 0x16;
    private static final int BOTTOM = 0x17;
    private static final int RIGHT = 0x18;
    private static final int LEFT_TOP = 0x11;
    private static final int RIGHT_TOP = 0x12;
    private static final int LEFT_BOTTOM = 0x13;
    private static final int RIGHT_BOTTOM = 0x14;
    private static final int CENTER = 0x19;
    private int offset = 20;

    //----start
    protected Paint paint = new Paint();

    private float minWidth = 120;
    private float minHeight = 30;


    private float maxWidth = 240;
    private float maxHeight = 60;


    private float viewWidth = 120;
    private float viewHeight = 30;


    private RectF rect;
    private float aspectRatio;
    private int borderColor;
    private int lineColor;
    private boolean isShowLine;


    private float DEFAULT_VIEW_WIDTH = 120;
    private float DEFAULT_ASPECT_RATIO = 4;
    private float DEFAULT_VIEW_LEFT = 260;
    private float DEFAULT_VIEW_RIGHT = 380;
    private float DEFAULT_VIEW_TOP = 85;
    private float DEFAULT_MEASURE_HEIGHT = 200;
    private float DEFAULT_VIEW_BOTTOM = 115;
    private int DEFAULT_BORDER_COLOR = Color.RED;
    private int DEFAULT_Line_COLOR = Color.GREEN;

    private boolean isTouch;

    private boolean isFirstOnMeasure = true;
    private int setRoiType = 1;

    /**
     * 初始化获取屏幕宽高
     */
    protected void initScreenW_H(Context context, AttributeSet attrs) {
        screenHeight = 600;
        screenWidth = 1280;
        TypedValue outValue = new TypedValue();
        rect = new RectF();

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CalibrationView);
        try {
            //存储规则 "left,top,width,height,centerX"
            String cacheParam = SpHelper.getInstance().getString(AppInfo.MARK_POINT, "");
            if (cacheParam != null && !cacheParam.equals("")) {
                String[] cacheParamArr = cacheParam.split(",");
                viewWidth = Float.parseFloat(cacheParamArr[2]);
                viewHeight = Float.parseFloat(cacheParamArr[3]);
                Log.d(TAG, "initScreenW_H: 有缓存：viewWidth = " + viewWidth + ",viewHeight =" + viewHeight);
            } else {
                viewWidth = attributes.getDimension(R.styleable.CalibrationView_baseWidth, DEFAULT_VIEW_WIDTH);
                aspectRatio = attributes.getFloat(R.styleable.CalibrationView_aspectRatio, DEFAULT_ASPECT_RATIO);
                viewHeight = viewWidth / aspectRatio;
                Log.d(TAG, "initScreenW_H: 无缓存：viewWidth = " + viewWidth + ",viewHeight =" + viewHeight);
            }

            maxHeight = maxWidth / aspectRatio;

            borderColor = attributes.getColor(R.styleable.CalibrationView_borderColor, DEFAULT_BORDER_COLOR);
            lineColor = attributes.getColor(R.styleable.CalibrationView_lineColor, DEFAULT_Line_COLOR);
            isShowLine = attributes.getBoolean(R.styleable.CalibrationView_showLine, false);
        } finally {
            attributes.recycle();
        }
    }

    public CalibrationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        initScreenW_H(context, attrs);
    }

    public CalibrationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        initScreenW_H(context, attrs);
    }

    public CalibrationView(Context context) {
        super(context);
        setOnTouchListener(this);
        initScreenW_H(context, null);
    }

    private OnOffsetChangeListener mOnOffsetChangeListener;

    public void setOnOffsetChangeListener(OnOffsetChangeListener listener) {
        mOnOffsetChangeListener = listener;
    }

    public interface OnOffsetChangeListener {
        void OnOffsetChange(int value, float left, float top, float width, float height,int type);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //存储规则 "left,top,width,height"
        String cacheParam = SpHelper.getInstance().getString(AppInfo.MARK_POINT, "");
        if (cacheParam != null && !cacheParam.equals("") && isFirstOnMeasure) {
            String[] cacheParamArr = cacheParam.split(",");
            rect.left = Float.parseFloat(cacheParamArr[0]);
            rect.top = Float.parseFloat(cacheParamArr[1]);
            Log.d(TAG, "onMeasure: 有缓存 且第一次 isFirstOnMeasure");
            isFirstOnMeasure = false;
        } else {
            if (isFirstOnMeasure) {
                //Log.d(TAG, "rect value isisis: " + rect.left + ",right is:" + rect.right + ",width is:" + viewWidth);
                rect.left = getMeasuredWidth() / 2 - viewWidth / 2;
                rect.top = DEFAULT_VIEW_TOP;//getMeasuredHeight() / 2 - viewHeight / 2;
                isFirstOnMeasure = false;
                //Log.d(TAG, "rect value isis: " + getMeasuredHeight() + "width is:"+getMeasuredWidth());
            }
        }
        rect.right = rect.left + viewWidth;
        rect.bottom = rect.top + viewHeight;
        //Log.d(TAG, "rect value isis: " + rect.left + ",top is:" + rect.top + ",width is:" + viewWidth + "height is:" + viewHeight);
    }

    public void setIsTouch(boolean isTouch) {
        this.isTouch = isTouch;
    }

    private void scaleRect(float scale) {
//        viewWidth = viewWidth * scale;
//        viewHeight = viewHeight * scale;
        if (scale > 1) {
            viewWidth += 4;
            viewHeight += 1;
        } else {
            viewWidth -= 4;
            viewHeight -= 1;
        }

        if (viewWidth >= maxWidth) {
            viewWidth = maxWidth;
            viewHeight = maxHeight;
        } else if (viewWidth <= minWidth) {
            viewWidth = minWidth;
            viewHeight = minHeight;
        }
        if (viewHeight >= maxHeight) {
            viewHeight = maxHeight;
        } else if (viewHeight <= minHeight) {
            viewHeight = minHeight;
        }

        rect.left = rect.centerX() - viewWidth / 2;
        rect.top = rect.centerY() - viewHeight / 2;
        rect.right = rect.left + viewWidth;
        rect.bottom = rect.top + viewHeight;
        Log.d(TAG, "rect value is: " + rect.left + ",top is:" + rect.top + ",width is:" + viewWidth + ",height is:" + viewHeight);
        invalidate();
        center(0, 0);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (mOnOffsetChangeListener != null) {
                        int value = (int) (rect.centerX() - getMeasuredWidth() / 2);
                        //Log.d("setRoiType = ", "handleMessage: "+setRoiType);
                        mOnOffsetChangeListener.OnOffsetChange(value, rect.left, rect.top, rect.width(), rect.height(),setRoiType);
                        setRoiType = 0;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(borderColor);
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(rect, paint);

        if (isShowLine) {
            paint.setStrokeWidth(1.0f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(lineColor);
            canvas.drawLine(rect.centerX(), 0, rect.centerX(), getHeight(), paint);
        }
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(1, 1000);

    }

    float last_x = -1;
    float last_y = -1;
    float baseValue;
    public long scaleTime;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isTouch)
            return false;
        // TODO Auto-generated method stub
        // return ArtFilterActivity.this.mGestureDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            baseValue = 0;
            float x = last_x = event.getRawX();
            float y = last_y = event.getRawY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getPointerCount() == 2) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
                if (baseValue == 0) {
                    baseValue = value;
                } else {
                    if (value - baseValue >= 10 || value - baseValue <= -10) {
                        float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                        setRoiType = 1;
                        scaleRect(scale);
                        scaleTime = System.currentTimeMillis();
                    }
                }
                //bug  两指缩放抬起后 会导致一次 滑动。 利用时间间隔做的判断
            } else if (event.getPointerCount() == 1 && System.currentTimeMillis() - scaleTime > 500) {

                float x = event.getRawX();
                float y = event.getRawY();
                x -= last_x;
                y -= last_y;
                if (x >= 10 || y >= 10 || x <= -10 || y <= -10){
                    setRoiType = 1;
                    center(x, y);
                }
                last_x = event.getRawX();
                last_y = event.getRawY();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {

        }
        return true;
    }


    /**
     * 触摸点为中心->>移动
     *
     * @param dx
     * @param dy
     */
    private void center(float dx, float dy) {
        rect.left = rect.left + dx;
        rect.top = rect.top + dy;
        rect.right = rect.right + dx;
        rect.bottom = rect.bottom + dy;
        if (rect.left < 0) {
            rect.left = 0;
            rect.right = rect.left + viewWidth;
        }
        if (rect.right > getMeasuredWidth()) {
            rect.right = getMeasuredWidth();
            rect.left = rect.right - viewWidth;
        }
        if (rect.top < 0) {
            rect.top = 0;
            rect.bottom = rect.top + viewHeight;
        }
        if (rect.bottom > DEFAULT_MEASURE_HEIGHT) { //getMeasuredHeight()) {
            rect.bottom = DEFAULT_MEASURE_HEIGHT; //getMeasuredHeight();
            rect.top = rect.bottom - viewHeight;
        }
        String markPoint = rect.left + "," + rect.top + "," + viewWidth + "," + viewHeight;
        Log.d(TAG, "center: 存储拖动过的 值：" + markPoint);
        SpHelper.getInstance().putString(AppInfo.MARK_POINT, markPoint);
        //Log.d(TAG, "rect value is: " + rect.left + ",right is:" + rect.right + ",width is:" + viewWidth);
        invalidate();
    }

    /**
     * 平移
     */
    public void translationBox(int devi) {
        //存储规则 "left,top,width,height"
        String cacheParam = SpHelper.getInstance().getString(AppInfo.MARK_POINT, "");


        if (cacheParam != null && !cacheParam.equals("")) {
            String[] cacheParamArr = cacheParam.split(",");
            rect.left = Float.parseFloat(cacheParamArr[0]);
            rect.right = rect.left + Float.parseFloat(cacheParamArr[2]);
            //Log.d(TAG, "reading rect value is: " + rect.left + ",right is:" + rect.right);
        } else {
            rect.left = DEFAULT_VIEW_LEFT;
            rect.right = DEFAULT_VIEW_RIGHT;
            rect.top = DEFAULT_VIEW_TOP;
            rect.bottom = DEFAULT_VIEW_BOTTOM;
        }

        float width = rect.right - rect.left;
        rect.left = devi - width/2;
        rect.right = rect.left + width;
        /*
        if (devi > 0) {
            rect.left = rect.left + devi;
            rect.right = rect.right + devi;
        } else {
            rect.left = rect.left - devi;translationBox
            rect.right = rect.right - devi;
        }*/
        if (rect.left < 0) {
            rect.left = 0;
            rect.right = rect.left + viewWidth;
        }
        if (rect.right > getMeasuredWidth()) {
            rect.right = getMeasuredWidth();
            rect.left = rect.right - viewWidth;
        }
        if (rect.top < 0) {
            rect.top = 0;
            rect.bottom = rect.top + viewHeight;
        }
        if (rect.bottom > DEFAULT_MEASURE_HEIGHT) { //getMeasuredHeight()) {
            rect.bottom = DEFAULT_MEASURE_HEIGHT; //getMeasuredHeight();
            rect.top = rect.bottom - viewHeight;
        }

        //Log.d(TAG, "rect value is: " + rect.left + ",top is:" + rect.top + ",width is:" + viewWidth);
        invalidate();
    }


    /**
     * 触摸点为上边缘
     *
     * @param v
     * @param dy
     */
    private void top(View v, int dy) {
        oriTop += dy;
        if (oriTop < -offset) {
            oriTop = -offset;
        }
        if (oriBottom - oriTop - 2 * offset < minHeight) {
//            oriTop = oriBottom - 2 * offset - minHeight;
        }
    }

    /**
     * 触摸点为下边缘
     *
     * @param v
     * @param dy
     */
    private void bottom(View v, int dy) {
        oriBottom += dy;
        if (oriBottom > screenHeight + offset) {
            oriBottom = screenHeight + offset;
        }
        if (oriBottom - oriTop - 2 * offset < minHeight) {
//            oriBottom = minHeight + oriTop + 2 * offset;
        }
    }

    /**
     * 触摸点为右边缘
     *
     * @param v
     * @param dx
     */
    private void right(View v, int dx) {
        oriRight += dx;
        if (oriRight > screenWidth + offset) {
            oriRight = screenWidth + offset;
        }
        if (oriRight - oriLeft - 2 * offset < minWidth) {
//            oriRight = oriLeft + 2 * offset + minWidth;
        }
    }

    /**
     * 触摸点为左边缘
     *
     * @param v
     * @param dx
     */
    private void left(View v, int dx) {
        oriLeft += dx;
        if (oriLeft < -offset) {
            oriLeft = -offset;
        }
        if (oriRight - oriLeft - 2 * offset < minWidth) {
//            oriLeft = oriRight - 2 * offset - minWidth;
        }
    }

    /**
     * 获取触摸点flag
     *
     * @param v
     * @param x
     * @param y
     * @return
     */
    protected int getDirection(View v, int x, int y) {
        int left = v.getLeft();
        int right = v.getRight();
        int bottom = v.getBottom();
        int top = v.getTop();
        if (x < 40 && y < 40) {
            return LEFT_TOP;
        }
        if (y < 40 && right - left - x < 40) {
            return RIGHT_TOP;
        }
        if (x < 40 && bottom - top - y < 40) {
            return LEFT_BOTTOM;
        }
        if (right - left - x < 40 && bottom - top - y < 40) {
            return RIGHT_BOTTOM;
        }
        if (x < 40) {
            return LEFT;
        }
        if (y < 40) {
            return TOP;
        }
        if (right - left - x < 40) {
            return RIGHT;
        }
        if (bottom - top - y < 40) {
            return BOTTOM;
        }
        return CENTER;
    }

}