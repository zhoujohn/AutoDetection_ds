package com.common.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.baselib.R;
import com.common.baselib.utils.DensityUtils;
import com.common.baselib.utils.StringUtils;


/**
 * created by wdf 2019/3/22 0022
 *
 * @desc
 */
public class CustomLayout extends LinearLayout {


    private Context mContext;
    private Paint paint;
    private int colorLine;
    private int lineWidth;
    private boolean top, right, bottom, left;
    private boolean isAddText;
    private TextView tv;


    public CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        colorLine = context.getResources().getColor(R.color.color_dddddd);

        initializePainters();
        setWillNotDraw(false);
        lineWidth = DensityUtils.dip2px(1);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomLayout);

        top = ta.getBoolean(R.styleable.CustomLayout_layout_isShowTop, false);
        left = ta.getBoolean(R.styleable.CustomLayout_layout_isShowLeft, false);
        right = ta.getBoolean(R.styleable.CustomLayout_layout_isShowRight, false);
        bottom = ta.getBoolean(R.styleable.CustomLayout_layout_isShowBottom, false);
        isAddText = ta.getBoolean(R.styleable.CustomLayout_layout_isAddText, true);
        String content = ta.getString(R.styleable.CustomLayout_layout_content);

        ta.recycle();
        setGravity(Gravity.CENTER);
        if (isAddText) {
            tv = new TextView(context);
            tv.setTextSize(12);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(getResources().getColor(R.color.color_666666));
            addView(tv);
            if (StringUtils.isNotNull(content)) {
                tv.setText(content);
            }
        }
    }

    private void initializePainters() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorLine);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();

        if (top) {
            canvas.drawRect(0, 0, width, lineWidth, paint);
        }
        if (right) {
            canvas.drawRect(width - lineWidth, 0, width, height, paint);
        }
        if (bottom) {
            canvas.drawRect(0, height - lineWidth, width, height, paint);
        }
        if (left) {
            canvas.drawRect(0, 0, lineWidth, height, paint);
        }
    }

    public void setText(String text) {
        if (tv == null) {
            return;
        }
        tv.setText(text);
    }

}
