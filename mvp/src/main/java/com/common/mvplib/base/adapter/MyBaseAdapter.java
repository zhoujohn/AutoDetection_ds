package com.common.mvplib.base.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * ================================================
 * 包名：com.common.mvplib.base.adapter
 * 创建人：sws
 * 创建时间：2019/6/11  下午 07:10
 * 描述：
 * ================================================
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
    public Context context;
    public List<T> itemList;

    public Context getContext() {
        return context;
    }

    public MyBaseAdapter(Context context) {
        this.context = context;
    }

    public List<T> getItemList() {
        return itemList;
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
    public int getCount() {
        if (itemList != null && itemList.size() > 0)
            return itemList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
