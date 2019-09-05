package com.common.baselib.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.common.baselib.R;
import com.common.baselib.utils.CommonUtils;
import com.common.baselib.utils.DensityUtils;
import com.common.baselib.utils.LogUtil;
import com.common.baselib.utils.StringUtils;
import com.common.baselib.utils.date.DateUtil;
import com.common.baselib.utils.date.bean.DayBean;
import com.common.baselib.utils.date.bean.MouthBean;

import java.util.ArrayList;
import java.util.List;


/**
 * created by wdf 2019/3/18 0018
 *
 * @desc
 */

public class MouthView extends View {


    private final static int SIZE_DAY_ALIAS = DensityUtils.sp2px(8);
    private final static int SIZE_DAY = DensityUtils.sp2px(12);
    private final static int SIZE_PRICE = DensityUtils.sp2px(8);

    private final static int COLUMN_COUNT = 7;
    private int mPerHeight = DensityUtils.dip2px(50);
    private int mPerWidth = (DensityUtils.getWidth() - DensityUtils.dip2px(41)) / COLUMN_COUNT;
    private Context mContext;

    public Paint mPaintLines;
    public Paint mPaintText;
    public Paint mPaintSelect;

    public List<Point> mRecordList;
    public int mLineColor, mDayColor, mDayTodayBeforeColor, mTextAliasColor, mTextPriceColor, mSelectBgColor, mSelectTextColor;

    public int mWidth;
    public int mHeight;

    private int mRowCount;
    private List<DayBean> dayBeanList;
    private LinearGradient mSelectGradient;
    private int mSelectStartColor, mSelectEndColor;

    public MouthView(Context context) {
        super(context);
        init(context, null);
    }

    public MouthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        mLineColor = context.getResources().getColor(R.color.line_bg);
        mDayColor = context.getResources().getColor(R.color.color_242424);
        mTextAliasColor = context.getResources().getColor(R.color.red);
        mDayTodayBeforeColor = context.getResources().getColor(R.color.color_b1b1b1);
        mTextPriceColor = context.getResources().getColor(R.color.color_666666);
        mSelectBgColor = context.getResources().getColor(R.color.red);
        mSelectTextColor = context.getResources().getColor(R.color.white);

        mSelectStartColor = context.getResources().getColor(R.color.color_ff3c43);
        mSelectEndColor = context.getResources().getColor(R.color.color_ff784b);


        mPaintLines = new Paint();
        mPaintLines.setColor(mLineColor);
        mPaintLines.setAntiAlias(true);

        mPaintSelect = new Paint();
        mPaintSelect.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintSelect.setStrokeCap(Paint.Cap.ROUND);
        mPaintSelect.setAntiAlias(true);

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);

        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setTextAlign(Paint.Align.CENTER);

        mRecordList = new ArrayList<>();
    }

    public void setMouthInfo(MouthBean mouthBean) {
        this.dayBeanList = mouthBean.dateList;
        mRowCount = mouthBean.row;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int resultWidth = 0;
        int resultHeight = 0;

        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST || MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            resultWidth = DensityUtils.getWidth();
        } else {
            resultWidth = widthSize;
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST || MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            resultHeight = mRowCount * mPerHeight;
        } else {
            resultHeight = heightSize;
        }
        mWidth = resultWidth;
        mHeight = resultHeight;

        setMeasuredDimension(resultWidth, resultHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (CommonUtils.isListNull(dayBeanList)) {
            return;
        }
        for (int x = 0; x < dayBeanList.size(); x++) {
            drawDay(canvas, dayBeanList.get(x), x);
        }
        drawLines(canvas);
    }

    private RectF rectDayCon = new RectF();
    private RectF rectTextAlias = new RectF();
    private RectF rectTextPrice = new RectF();

    private void drawDay(Canvas canvas, DayBean bean, int index) {

        if (!bean.isShow()) {
            return;
        }

        boolean isSelect = bean.isSameDay(mSelectDay);


        int column = index % COLUMN_COUNT;
        int row = index / COLUMN_COUNT;

        rectDayCon.left = column * mPerWidth;
        rectDayCon.top = row * mPerHeight;
        rectDayCon.right = (column + 1) * mPerWidth;
        rectDayCon.bottom = (row + 1) * mPerHeight;


        if (isSelect) {
            mPaintText.setColor(mSelectBgColor);
            if (mSelectGradient == null) {
                mSelectGradient = new LinearGradient(rectDayCon.left, rectDayCon.top, rectDayCon.right, rectDayCon.bottom, new int[]{mSelectStartColor, mSelectEndColor}, null, Shader.TileMode.CLAMP);
            }
            mPaintSelect.setShader(mSelectGradient);
            canvas.drawRoundRect(rectDayCon, 20, 20, mPaintSelect);
        }

        mPaintText.setTextSize(SIZE_DAY);
        mPaintText.setColor(isSelect ? mSelectTextColor : mDayColor);
        Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
        int baseLineY = (int) (rectDayCon.centerY() - fontMetrics.top / 2 - fontMetrics.bottom / 2);

        if (bean.state == DateUtil.TODAY_BEFORE) {
            mPaintText.setColor(isSelect ? mSelectTextColor : mDayTodayBeforeColor);
        }
        canvas.drawText(String.valueOf(bean.day), rectDayCon.centerX(), baseLineY, mPaintText);


        if (bean.state == DateUtil.TODAY) {

            rectTextAlias.left = rectDayCon.left;
            rectTextAlias.right = rectDayCon.right;
            rectTextAlias.top = rectDayCon.top;
            rectTextAlias.bottom = rectTextAlias.top + (mPerHeight / 2 - SIZE_DAY / 2);

            mPaintText.setTextSize(SIZE_DAY_ALIAS);
            mPaintText.setColor(isSelect ? mSelectTextColor : mTextAliasColor);
            fontMetrics = mPaintText.getFontMetrics();
            baseLineY = (int) (rectTextAlias.centerY() - fontMetrics.top / 2 - fontMetrics.bottom / 2);
            canvas.drawText("今天", rectTextAlias.centerX(), baseLineY, mPaintText);
        }

        if (bean.isCanClick() && StringUtils.isNotNull(bean.price + "")) {
            rectTextPrice.left = rectDayCon.left;
            rectTextPrice.right = rectDayCon.right;
            rectTextPrice.top = rectDayCon.bottom - (mPerHeight / 2 - SIZE_DAY / 2);
            rectTextPrice.bottom = rectDayCon.bottom;

            mPaintText.setTextSize(SIZE_PRICE);
            mPaintText.setColor(isSelect ? mSelectTextColor : mTextPriceColor);
            fontMetrics = mPaintText.getFontMetrics();
            baseLineY = (int) (rectTextPrice.centerY() - fontMetrics.top / 2 - fontMetrics.bottom / 2);
            canvas.drawText(String.valueOf("¥" + bean.price), rectTextPrice.centerX(), baseLineY, mPaintText);
        }


    }

    private void drawLines(Canvas canvas) {
        for (int x = 0; x < mRowCount; x++) {
            canvas.drawLine(0, x * mPerHeight, mWidth, x * mPerHeight + 1, mPaintLines);
        }
    }


    private int mDownPoistion, mUpPoistion;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (CommonUtils.isListNull(dayBeanList)) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownPoistion = countPoistion(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mUpPoistion = countPoistion(event);
                if (mDownPoistion == mUpPoistion && mUpPoistion >= 0 && mUpPoistion < dayBeanList.size()) {
                    LogUtil.log("mUpPoistion:" + mUpPoistion);
                    DayBean selectDay = dayBeanList.get(mUpPoistion);
                    if (!selectDay.isCanClick()) {
                        return false;
                    }
                    if (mOnSelectDayListener != null) {
                        mOnSelectDayListener.onSelectDay(selectDay);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    private int countPoistion(MotionEvent event) {
        int left = (int) event.getX();
        int top = (int) event.getY();

        int row = top / mPerHeight;
        int column = left / mPerWidth;

        LogUtil.log("row:" + row);
        LogUtil.log("column:" + column);


        return row * COLUMN_COUNT + column;

    }

    public void setOnSelectDayListener(OnSelectDayListener onSelectDayListener) {
        this.mOnSelectDayListener = onSelectDayListener;
    }

    public void setSelectDay(DayBean dayBean) {
        this.mSelectDay = dayBean;
    }

    private OnSelectDayListener mOnSelectDayListener;
    private DayBean mSelectDay;


    public interface OnSelectDayListener {
        void onSelectDay(DayBean dayBean);
    }


}
