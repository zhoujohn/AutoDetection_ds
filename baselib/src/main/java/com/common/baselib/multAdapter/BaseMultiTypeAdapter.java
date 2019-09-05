package com.common.baselib.multAdapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * ================================================
 * 包名：com.common.baselib.multAdapter
 * 创建人：sws
 * 创建时间：2019/5/27  下午 04:25
 * 描述：
 * ================================================
 */
public abstract class BaseMultiTypeAdapter<MANAGER extends BaseAdapterProxyManager> extends RecyclerView.Adapter {

    public MANAGER mProxyManager;

    public Activity mContext;

    public List<Object> mItemList;

    public BaseMultiTypeAdapter(Activity context) {
        this.mContext = context;
        mProxyManager = initProxyManager();
        checkProxyManager();
    }

    public void setItemList(List<Object> itemList) {
        mItemList = itemList;
    }

    public void reSetData(List<Object> itemList) {
        setItemList(itemList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mProxyManager != null && mProxyManager.getAdapterProxyByType(viewType) != null) {
            return mProxyManager.getAdapterProxyByType(viewType).onCreateViewHolder(parent, viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int currType = holder.getItemViewType();
        if (mProxyManager != null && mProxyManager.getAdapterProxyByType(currType) != null) {
            if (mItemList != null) {
                mProxyManager.getAdapterProxyByType(currType).onBindViewHolder(holder, position, mItemList.get(position));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mProxyManager != null && mItemList != null) {
            return mProxyManager.matchType(mItemList.get(position));
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        if (mItemList != null && mItemList.size() > 0) return mItemList.size();
        else return 0;
    }

    private void checkProxyManager() {
        if (mProxyManager == null) {
            throw new RuntimeException("must init a adapterProxyManager");
        }
    }

    protected abstract MANAGER initProxyManager();

    public BaseAdapterProxy findAdapterProxy(int type) {
        return mProxyManager.findAdapterProxy(type);
    }


}
