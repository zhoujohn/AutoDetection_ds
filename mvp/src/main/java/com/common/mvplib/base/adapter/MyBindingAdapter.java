package com.common.mvplib.base.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

/**
 * ================================================
 * 包名：com.common.mvplib.base.adapter
 * 创建人：sws
 * 创建时间：2019/6/11  下午 07:11
 * 描述：
 * ================================================
 */
public abstract class MyBindingAdapter<T,BINDING extends ViewDataBinding> extends MyBaseAdapter<T> {

    public MyBindingAdapter(Context context) {
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
        BINDING binding = null;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(((Activity) context).getLayoutInflater(), getLayoutId(), null, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
            dealBinding(binding);
        } else {
            binding = (BINDING) convertView.getTag();
        }
        if (itemList != null && itemList.size() > 0) {
            final T bean = itemList.get(position);
            if (bean != null && binding != null) {
                fillData(bean, binding, position);
            }
        }
        return convertView;
    }

    /**
     * 填充数据
     *
     * @param bean
     */
    public abstract void fillData(T bean, BINDING binding, int position);

    public void dealBinding(BINDING binding) {

    }

}
