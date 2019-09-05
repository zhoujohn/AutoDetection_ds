package com.common.baselib.widget.ptr.base;

import android.view.View;

public interface IPtrHandler {

    /**
     * Check can do refresh or not. For example the content is empty or the first child is in view.
     * <p/>
     */
    public boolean checkCanDoRefresh(final PtrBase frame, final View content, final View header);

    /**
     * When refresh begin
     *
     * @param frame
     */
    public void onRefreshBegin(final PtrBase frame);
}