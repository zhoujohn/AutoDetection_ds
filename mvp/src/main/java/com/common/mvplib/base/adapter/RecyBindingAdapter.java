package com.common.mvplib.base.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.view.ViewGroup;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * ================================================
 * 包名：com.common.mvplib.base.adapter
 * 创建人：sws
 * 创建时间：2019/6/11  下午 07:09
 * 描述：
 * ================================================
 */
public abstract class RecyBindingAdapter<T, BINDING extends ViewDataBinding> extends RecyclerView.Adapter {

    public Context context;
    public List<T> itemList;

    public RecyBindingAdapter(Context context) {
        this.context = context;
    }

    public void setItemList(List<T> itemList) {
        this.itemList = itemList;
    }

    public void reSetData(List<T> itemList) {
        setItemList(itemList);
        notifyDataSetChanged();
    }

    public void addItemList(List<T> itemList) {
        if (this.itemList != null) {
            this.itemList.addAll(itemList);
        } else {
            this.itemList = itemList;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (itemList != null) {
            T bean = itemList.get(position);
            if (bean != null) {
                fillData(bean, (BINDING) ((ViewHolder) holder).mBinding, position);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BINDING mBinding = DataBindingUtil.inflate(((Activity) context).getLayoutInflater(), getLayoutId(), parent, false);
        return new ViewHolder(mBinding);
    }

    @Override
    public int getItemCount() {
        if (itemList != null && itemList.size() > 0)
            return itemList.size();
        else
            return 0;
    }

    /**
     * 获取layoutId
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 填充数据
     *
     * @param bean
     * @param binding
     */
    public abstract void fillData(T bean, BINDING binding, int position);


    static class ViewHolder<BINDING extends ViewDataBinding> extends RecyclerView.ViewHolder {

        BINDING mBinding;

        public ViewHolder(BINDING mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
    }
}
