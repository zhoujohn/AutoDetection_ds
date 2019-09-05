package com.common.baselib.widget.ptr.recycler;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.common.baselib.widget.ptr.base.AbsPtrView;
import com.common.baselib.widget.ptr.footer.PtrFooter;


/**
 * 下拉刷新RecyclerView
 */
public class PtrRecyclerView extends AbsPtrView<FamiliarRecyclerView> {
    private RecyclerOnScrollListener onScrollListener;
    public RecyclerView.Adapter mAdapter;


    public PtrRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public FamiliarRecyclerView getRefreshView(Context context, AttributeSet attrs) {
        FamiliarRecyclerView mRvList = new FamiliarRecyclerView(getContext(), attrs);
        mRvList.setItemAnimator(null);
        if (null == onScrollListener) {
            onScrollListener = new RecycleronScrollListener();
        }
        mRvList.setHasFixedSize(true);
        mRvList.setNestedScrollingEnabled(false);
        mRvList.addOnScrollListener(onScrollListener);
        return mRvList;
    }

    /**
     * 设置适配器,放在最后
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
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
    private class RecycleronScrollListener extends RecyclerOnScrollListener {

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

            if (mPtrFooter != null && mPtrFooter.getLoadingStatus() == PtrFooter.State_LOADING_FAIL) {
                return;
            }

            loadingMore();
        }
    }


    @Override
    public void autoRefresh() {
        mRefreshView.scrollToPosition(0);
        super.autoRefresh();
    }
}
