package com.common.baselib.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * ================================================
 * 包名：com.common.baselib.ui
 * 创建人：sws
 * 创建时间：2019/5/27  下午 05:39
 * 描述：
 * ================================================
 */
public abstract class BaseView<BINDING extends ViewDataBinding> extends LinearLayout {

    public BINDING mBinding;
    public Activity mActivity;

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mActivity = (Activity) context;
        mBinding = DataBindingUtil.inflate(mActivity.getLayoutInflater(), getLayoutId(), null, false);
        addView(mBinding.getRoot(),new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public abstract int getLayoutId();

}

