package com.common.baselib.widget.ptr.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.common.baselib.widget.ptr.footer.AbsFooter;
import com.common.baselib.widget.ptr.header.IHeaderLayout;
import com.common.baselib.widget.ptr.header.PtrHeader;


/**
 * 下拉刷新RecyclerView
 */
public abstract class AbsPtrView<T extends View> extends PtrBase<T> implements IPtrHandler {
    public Context context;

    public IHeaderLayout mPtrHeader;
    public AbsFooter mPtrFooter;
    public OnRefreshListener onRefreshListener;

    /**
     * 加载更多中
     */
    public boolean isLoadingMore;

    /**
     * 加载更多是否可用
     */
    public boolean isPullLoadEnable;

    public AbsPtrView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setPtrHandler(this);
    }

    @Override
    public IHeaderLayout getPtrHeader() {
        mPtrHeader = new PtrHeader(getContext());
        return mPtrHeader;
    }

    @Override
    public boolean checkCanDoRefresh(PtrBase frame, View content, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
    }

    @Override
    public void onRefreshBegin(PtrBase frame) {
        if (onRefreshListener != null) {
            onRefreshListener.onRefresh();
        }
    }

    /**
     * 加载更多
     */
    public void loadingMore() {
        isLoadingMore = true;
        if (mPtrFooter != null) {
            mPtrFooter.showLoading();
        }
        if (onRefreshListener != null) {
            onRefreshListener.onLoadMore();
        }
    }

    public void loadMoreComplete() {
        isLoadingMore = false;
        if (mPtrFooter != null) {
            mPtrFooter.hide();
        }
    }

    /**
     * 下拉刷新和加载更多 完毕
     */
    public void onPtrComplete() {
        loadMoreComplete();
        refreshComplete();
    }

    /**
     * 处理请求结果
     */
    public void setLoadMoreEnable(boolean isEnable) {
        isPullLoadEnable = isEnable;
        if (mPtrFooter != null) {
            if (isEnable) {
                mPtrFooter.setLoadingEnable();
            } else {
                mPtrFooter.hide();
            }
        }
    }

    /**
     * 加载更多失败
     */
    public void setLoadMoreFail() {
        if (mPtrFooter != null) {
            mPtrFooter.onLoadingFail();
        }
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }


    public interface OnRefreshListener {
        /**
         * 下拉刷新
         */
        public void onRefresh();

        /**
         * 加载更多
         */
        public void onLoadMore();
    }
}
