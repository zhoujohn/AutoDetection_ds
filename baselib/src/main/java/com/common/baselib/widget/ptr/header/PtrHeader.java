package com.common.baselib.widget.ptr.header;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.baselib.R;
import com.common.baselib.utils.LogUtil;
import com.common.baselib.widget.ptr.base.PtrBase;
import com.common.baselib.widget.ptr.indicator.PtrIndicator;


/**
 * Created by wdf on 2016/10/11.
 */
public class PtrHeader extends FrameLayout implements IHeaderLayout {

    private ImageView iv;
    private TextView txt_msg;
    private TextView txt_msg_sub;
    private Context context;

    private AnimationDrawable mLoadingAinm;

    public PtrHeader(Context context) {
        super(context);
        this.context = context;
        initViews(null);
    }

    public PtrHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public PtrHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(attrs);
    }

    protected void initViews(AttributeSet attrs) {
        View.inflate(getContext(), R.layout.ptr_header, this);
        iv = (ImageView) findViewById(R.id.iv);
        txt_msg_sub = (TextView) findViewById(R.id.txt_msg_sub);
        txt_msg = (TextView) findViewById(R.id.txt_msg);

        if (mLoadingAinm == null) {//spinner_black_2
            iv.setBackgroundResource(R.drawable.loading_header);
            mLoadingAinm = (AnimationDrawable) iv.getBackground();
        }
    }

    @Override
    public void onHeaderReset(PtrBase frame) {
        LogUtil.log("onHeaderReset:");
    }

    @Override
    public void onHeaderRefreshPrepare(PtrBase frame) {
        txt_msg.setText("下拉刷新");
        setSubTxt();
    }

    @Override
    public void onHeaderRefresh(PtrBase frame) {
        LogUtil.log("onHeaderRefresh:");
        iv.setImageDrawable(null);
        mLoadingAinm.start();
        txt_msg.setText("正在载入...");
    }

    @Override
    public void onHeaderRefreshComplete(PtrBase frame) {
        LogUtil.log("onHeaderRefreshComplete:");
        if (mLoadingAinm != null) {
            mLoadingAinm.stop();
        }
        txt_msg.setText("下拉刷新");
    }

    @Override
    public void onHeaderPositionChange(PtrBase frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        final int mOffsetToRefresh = ptrIndicator.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        float prepareProgress = currentPos * 1.0f / (mOffsetToRefresh);
        //LogUtil.log("onHeaderPositionChange:" + prepareProgress);
        if (status == PtrBase.PTR_STATUS_PREPARE) {
            if (prepareProgress > 1) {
                prepareProgress = prepareProgress - (int) prepareProgress;
                txt_msg.setText("放开以刷新");
            } else {
                txt_msg.setText("下拉刷新");
            }
            int index = 1 + ((int) (11 * prepareProgress)) % 11;
            iv.setImageResource(getDrawableRes(index));
        }
    }

    public int getDrawableRes(int level) {
        Resources res = context.getResources();
        final String packageName = context.getPackageName();
        return res.getIdentifier("spinner_black_" + level, "drawable", packageName);
    }

    private void setSubTxt() {
        String timeString = DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        txt_msg_sub.setText(timeString);
    }
}
