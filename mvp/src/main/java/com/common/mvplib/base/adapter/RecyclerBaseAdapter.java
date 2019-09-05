package com.common.mvplib.base.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * ================================================
 * 包名：com.common.mvplib.base.adapter
 * 创建人：sws
 * 创建时间：2019/6/11  下午 07:10
 * 描述：
 * ================================================
 */
public abstract class RecyclerBaseAdapter<T, U extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    public Context context;
    public List<T> itemList;

    public RecyclerBaseAdapter(Context context) {
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
                fillData(bean, (U) holder, position);
            }
        }
    }

    @Override
    public U onCreateViewHolder(ViewGroup parent, int viewType) {
        return initViewHolder(LayoutInflater.from(context).inflate(getLayoutId(), parent, false));
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
     * @param holder
     */
    public abstract void fillData(T bean, U holder, int position);


    public abstract U initViewHolder(View view);


}
