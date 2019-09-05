package com.common.baselib.widget.ptr.list;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.common.baselib.widget.ptr.base.AbsPtrView;
import com.common.baselib.widget.ptr.base.IPtrHandler;
import com.common.baselib.widget.ptr.footer.PtrFooter;


/**
 * 下拉刷新RecyclerView
 */
public class PtrListView extends AbsPtrView<ListView> implements IPtrHandler {

    private ListOnScrollListener onScrollListener;

    public PtrListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public ListView getRefreshView(Context context, AttributeSet attrs) {
        ListView mRvList = new ListView(getContext(), attrs);
        if (null == onScrollListener) {
            onScrollListener = new MyScrollListener(mRvList, null);
        }
        mRvList.setOnScrollListener(onScrollListener);
        return mRvList;
    }

    /**
     * 设置适配器,放在最后
     *
     * @param adapter
     */
    public void setAdapter(BaseAdapter adapter) {
        if (mPtrFooter == null) {
            mPtrFooter = new PtrFooter(context, new PtrFooter.OnLoadFailListener() {
                @Override
                public void onLoadFail() {
                    loadingMore();
                }
            });
            mRefreshView.addFooterView(mPtrFooter);
        }
        mRefreshView.setAdapter(adapter);
    }

    /**
     * 滑动到底部监听
     */
    private class MyScrollListener extends ListOnScrollListener {

        public MyScrollListener(AbsListView absListView, AbsListView.OnScrollListener onScrollListener) {
            super(absListView, onScrollListener);
        }

        @Override
        public void onScrolledToBottom() {

            if (!isPullLoadEnable) {
                return;
            }

            if (isRefreshing()) {
                return;
            }

            if (isLoadingMore) {
                return;
            }

            if (mPtrFooter.getLoadingStatus() == PtrFooter.State_LOADING_FAIL) {
                return;
            }

            loadingMore();
        }
    }

    @Override
    public void autoRefresh() {
        mRefreshView.setSelection(0);
        super.autoRefresh();
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        if (null != this.onScrollListener) {
            this.onScrollListener.mOnScrollListener = onScrollListener;
        }
    }
}
