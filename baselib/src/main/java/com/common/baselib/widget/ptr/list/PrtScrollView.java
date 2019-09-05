package com.common.baselib.widget.ptr.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.common.baselib.utils.LogUtil;
import com.common.baselib.widget.ptr.base.AbsPtrView;
import com.common.baselib.widget.ptr.base.IPtrHandler;
import com.common.baselib.widget.ptr.header.IHeaderLayout;


/**
 * Created by wdf on 2016/12/5.
 */

public class PrtScrollView extends AbsPtrView<ScrollView> implements IPtrHandler {

    public PrtScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public ScrollView getRefreshView(Context context, AttributeSet attrs) {
        ScrollView scrollView = new ScrollView(getContext(), attrs);
        return scrollView;
    }

    @Override
    public void autoRefresh() {
        mRefreshView.scrollTo(0, 0);
        super.autoRefresh();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mRefreshView instanceof ViewGroup) {
            LogUtil.log("child:"+child);
            if(child instanceof ScrollView || child instanceof IHeaderLayout){
               super.addView(child, index, params);
            }else{
                mRefreshView.addView(child, index, params);
            }
        } else {
            throw new UnsupportedOperationException("Refreshable View is not a ViewGroup so can't addView");
        }
    }
}
