package com.common.baselib.multAdapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * ================================================
 * 包名：com.common.baselib.multAdapter
 * 创建人：sws
 * 创建时间：2019/5/27  下午 04:24
 * 描述：
 * ================================================
 */
public abstract class BaseAdapterProxy<BEAN, HOLDER extends RecyclerView.ViewHolder> {

    public Context mContext;

    public BaseMultiTypeAdapter rootAdapter;

    public BaseAdapterProxy(Context mContext,BaseMultiTypeAdapter rootAdapter) {
        this.mContext = mContext;
        this.rootAdapter = rootAdapter;
    }

    public void onBindViewHolder(HOLDER holder, int position, BEAN bean) {
        if (bean != null) {
            bindData(bean, holder, position);
        }
    }

    public HOLDER onCreateViewHolder(ViewGroup parent, int viewType) {
        return initViewHolder(LayoutInflater.from(mContext).inflate(getLayoutId(), parent, false));

    }


    /**
     * 获取viewHolder
     *
     * @param view
     * @return
     */
    public abstract HOLDER initViewHolder(View view);

    /**
     * 获取布局id
     *
     * @return
     */
    public abstract int getLayoutId();


    /**
     * 绑定数据
     *
     * @param bean
     * @param holder
     * @param position
     */
    public abstract void bindData(BEAN bean, HOLDER holder, int position);

    /**
     * 根据数据匹配类型
     *
     * @param bean
     * @return
     */
    public abstract boolean isMatchType(Object bean);


}
