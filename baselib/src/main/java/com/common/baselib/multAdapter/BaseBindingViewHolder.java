package com.common.baselib.multAdapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;

/**
 * ================================================
 * 包名：com.common.baselib.multAdapter
 * 创建人：sws
 * 创建时间：2019/5/27  下午 04:25
 * 描述：
 * ================================================
 */
public abstract class BaseBindingViewHolder<BINDING extends ViewDataBinding> {

    public BINDING mBinding;

    public BaseBindingViewHolder(Context context) {
        LayoutInflater localinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initView(localinflater);
    }

    /**
     * 获取布局文件Id
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化布局
     */
    public View initView(LayoutInflater layoutInflater) {
        mBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), null, false);
        return mBinding.getRoot();
    }
}
