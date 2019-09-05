package com.common.baselib.widget.ptr.recycler;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Familiar OnScrollListener
 * Created by iWgang on 15/11/13.
 * https://github.com/iwgang/FamiliarRecyclerView
 */
public abstract class RecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private boolean isCanScrolledCallback = false;
    private int callbackType = 0; // 0 not callback, 1 scrolled to top, 2 scrolled to bottom

    @Override
    public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (!isCanScrolledCallback) return;
        if (checkToBottom(recyclerView)) {
            // scrolled to bottom
            callbackType = 2;
            if (!isIdleCallBack()) {
                onScrolledToBottom();
            }
        } else {
            callbackType = 0;
        }
    }

    @Override
    public final void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                isCanScrolledCallback = false;
                if (isIdleCallBack()) {
                    if (callbackType == 2) {
                        onScrolledToBottom();
                    }
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                isCanScrolledCallback = true;
                break;
        }
    }

    private boolean checkToBottom(RecyclerView recyclerView) {
        int itemCount = recyclerView.getAdapter().getItemCount() - 1;
        int currCount = recyclerView.getChildCount();
        View lastView = recyclerView.getChildAt(currCount - 1);
        if (lastView == null) {
            return false;
        }
        int currLastPoistion = ((RecyclerView.LayoutParams) lastView.getLayoutParams()).getViewLayoutPosition();

        if (currLastPoistion < itemCount - 1) {
            return false;
        }

//        if (lastView.getBottom() <= recyclerView.getBottom()) {
//            return true;
//        }
        return true;
    }

    public abstract void onScrolledToBottom();

    protected boolean isIdleCallBack() {
        return true;
    }

}