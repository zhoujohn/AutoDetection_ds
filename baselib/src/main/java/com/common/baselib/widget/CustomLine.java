package com.common.baselib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.common.baselib.R;

/**
 * created by wdf 2019/3/22 0022
 *
 * @desc
 */
public class CustomLine extends LinearLayout {


    private Context mContext;
    private Paint paint;
    private int count = 50;
    private int color_point;


    public CustomLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        color_point = context.getResources().getColor(R.color.color_dddddd);

        initializePainters();
        setWillNotDraw(false);
    }

    private void initializePainters() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color_point);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float perWidth = getWidth() / (count * 2 + 1);
        float height = getHeight();

        for (int x = 0; x < count + 30; x++) {
            canvas.drawRect(x * perWidth * 2, 0, x * perWidth * 2 + perWidth, height, paint);
        }
    }

}
