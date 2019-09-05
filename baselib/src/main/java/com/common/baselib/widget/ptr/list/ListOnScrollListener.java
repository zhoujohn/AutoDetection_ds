package com.common.baselib.widget.ptr.list;

import android.widget.AbsListView;

/**
 * ListView
 */
public abstract class ListOnScrollListener implements AbsListView.OnScrollListener {

    public AbsListView absListView;
    public AbsListView.OnScrollListener mOnScrollListener;

    public ListOnScrollListener(AbsListView absListView, AbsListView.OnScrollListener onScrollListener) {
        this.absListView = absListView;
        this.mOnScrollListener = onScrollListener;
    }

    private boolean mLastItemVisible;


    public final void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
                               final int totalItemCount) {

        mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);

        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public final void onScrollStateChanged(final AbsListView view, final int state) {

        if (state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLastItemVisible) {
            if (checkToBottom()) {
                mLastItemVisible = false;
                onScrolledToBottom();
            }
        }
        if (null != mOnScrollListener) {
            mOnScrollListener.onScrollStateChanged(view, state);
        }
    }

    public boolean checkToBottom() {
//        final int lastItemPosition = absListView.getCount() - 1;
//        final int lastVisiblePosition = absListView.getLastVisiblePosition();
//        if (lastVisiblePosition >= lastItemPosition - 1) {
//            final int childIndex = lastVisiblePosition - absListView.getFirstVisiblePosition();
//            final View lastVisibleChild = absListView.getChildAt(childIndex);
//            if (lastVisibleChild != null) {
//                return lastVisibleChild.getBottom() <= absListView.getBottom();
//            }
//        }
        return true;
    }

    public abstract void onScrolledToBottom();

    public void setOnScrollListener(AbsListView.OnScrollListener mOnScrollListener) {
        this.mOnScrollListener = mOnScrollListener;
    }
}