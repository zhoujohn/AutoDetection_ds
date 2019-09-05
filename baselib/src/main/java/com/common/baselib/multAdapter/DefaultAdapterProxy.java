package com.common.baselib.multAdapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.common.baselib.R;

/**
 * ================================================
 * 包名：com.common.baselib.multAdapter
 * 创建人：sws
 * 创建时间：2019/5/27  下午 04:25
 * 描述：
 * ================================================
 */
public class DefaultAdapterProxy extends BaseAdapterProxy<Object, DefaultAdapterProxy.DefaultViewHolder> {


    public DefaultAdapterProxy(Context mContext, BaseMultiTypeAdapter rootAdapter) {
        super(mContext,rootAdapter);
    }

    @Override
    public DefaultViewHolder initViewHolder(View view) {
        return new DefaultViewHolder(view);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_holder_item_default;
    }

    @Override
    public void bindData(Object o, DefaultViewHolder holder, int position) {
    }

    @Override
    public boolean isMatchType(Object o) {
        return false;
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {

        private TextView tv;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_default);
        }
    }
}
