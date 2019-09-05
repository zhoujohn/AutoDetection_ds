package com.common.mvplib.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * ================================================
 * 包名：com.common.mvplib.base.adapter
 * 创建人：sws
 * 创建时间：2019/6/11  下午 07:12
 * 描述：  简易适配器
 * @param <T> 数据类型
 * ================================================
 */
public abstract class MySimpleAdapter<T, HOLDER extends HolderInit> extends MyBaseAdapter<T> {

    public MySimpleAdapter(Context context) {
        super(context);
    }

    /**
     * 获取layoutId
     *
     * @return
     */
    public abstract int getLayoutId();


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HOLDER holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, getLayoutId(), null);
            holder = getViewHolder();
            if (holder != null) {
                holder.initItemViews(convertView);
            }
            convertView.setTag(holder);
            dealHolder(holder, convertView);
        } else {
            holder = (HOLDER) convertView.getTag();
        }
        if (itemList != null && itemList.size() > 0) {
            final T bean = itemList.get(position);
            if (bean != null && holder != null) {
                fillData(bean, holder, position);
            }
        }
        return convertView;
    }

    public void dealHolder(HolderInit holder, View convertView) {

    }


    /**
     * 填充数据
     *
     * @param bean
     * @param holder
     */
    public abstract void fillData(T bean, HOLDER holder, int position);

    /**
     * 获取viewHolder
     *
     * @return
     */
    public abstract HOLDER getViewHolder();

}
