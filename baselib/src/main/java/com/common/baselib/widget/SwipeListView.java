package com.common.baselib.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.common.baselib.R;
import com.common.baselib.utils.DensityUtils;
import com.common.baselib.utils.LogUtil;


public class SwipeListView extends ListView {
    private Boolean mIsHorizontal;
    private View mPreItemView;
    private View mCurrentItemView;
    private float mFirstX;
    private float mFirstY;
    private int mRightViewWidth;
    private final int mDuration = 100;
    private final int mDurationStep = 10;
    private boolean mIsShown;
    private int right_pre;

    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }

    private boolean isCanScroll = true;

    public SwipeListView(Context context) {
        this(context, null);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.swipelistviewstyle);
        // 获取自定义属性和默认值
        right_pre = mTypedArray.getInt(R.styleable.swipelistviewstyle_right_pre, 3);
        mRightViewWidth = (int) mTypedArray.getDimension(R.styleable.swipelistviewstyle_right_width, DensityUtils.getWidth() / right_pre);
        mTypedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isCanScroll) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 隐藏右边
     */
    public void setHideToRaw() {
        if (mPreItemView != null) {
            hiddenRight(mPreItemView);
        }
        if (mCurrentItemView != null) {
            hiddenRight(mCurrentItemView);
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        setHideToRaw();
    }

    /**
     * return true, deliver to listView. return false, deliver to child. if
     * move, return true
     */
    @SuppressLint("NewApi")
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float lastX = ev.getX();
        float lastY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mIsHorizontal = null;
                mFirstX = lastX;
                mFirstY = lastY;
                int motionPosition = pointToPosition((int) mFirstX, (int) mFirstY);

                int listSize = getAdapter().getCount();

                LogUtil.log("listSize:" + listSize);
                LogUtil.log("motionPosition:" + motionPosition);

                if (motionPosition < getHeaderViewsCount() || motionPosition >= listSize - getFooterViewsCount()) {
                    isCanSliding = false;
                    setHideToRaw();
                    return false;
                } else {
                    isCanSliding = true;
                }

                if (motionPosition >= 0) {
                    View currentItemView = getChildAt(motionPosition - getFirstVisiblePosition());
                    mPreItemView = mCurrentItemView;
                    mCurrentItemView = currentItemView;
                }

                if (mIsShown && mPreItemView != null && !isClickExtra(mFirstX, mFirstY, mPreItemView)) {
                    return true;
                }

                break;

            case MotionEvent.ACTION_MOVE:
                float dx = lastX - mFirstX;
                float dy = lastY - mFirstY;

                if (Math.abs(dx) >= 5 && Math.abs(dy) >= 5) {
                    if (!isCanSliding) {
                        return false;
                    }
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return true;
                    }

                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsShown && (mPreItemView != mCurrentItemView || isHitCurItemLeft(lastX))) {
                    /**
                     * 情况一： 一个Item的右边布局已经显示， 这时候点击任意一个item, 那么那个右边布局显示的item隐藏其右边布局
                     */
                    hiddenRight(mPreItemView);
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isHitCurItemLeft(float x) {
        return x < getWidth() - mRightViewWidth;
    }

    /**
     * 是否点击隐藏区域
     *
     * @param currX
     * @param currY
     * @param view
     * @return
     */
    @SuppressLint("NewApi")
    public boolean isClickExtra(float currX, float currY, View view) {
        boolean isClickX = mFirstX > DensityUtils.getWidth() - mRightViewWidth;
        boolean isClickY = (mFirstY > view.getY() && mFirstY < view.getY() + mPreItemView.getHeight());
        if (isClickX && isClickY) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param dx
     * @param dy
     * @return judge if can judge scroll direction
     */
    private boolean judgeScrollDirection(float dx, float dy) {
        boolean canJudge = true;
        if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
            mIsHorizontal = true;
        } else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
            mIsHorizontal = false;
        } else {
            canJudge = false;
        }
        return canJudge;
    }

    private boolean isCanSliding;

    /**
     * return false, can't move any direction. return true, cant't move
     * vertical, can move horizontal. return super.onTouchEvent(ev), can move
     * both.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float lastX = ev.getX();
        float lastY = ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isCanSliding) {
                    return false;
                }
                if (mIsShown) {
                    hiddenRight(mPreItemView);
                    hiddenRight(mCurrentItemView);
                    return false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isCanSliding) {
                    return false;
                }
                float dx = lastX - mFirstX;
                float dy = lastY - mFirstY;
                if (mIsHorizontal == null) {
                    if (!judgeScrollDirection(dx, dy)) {
                        break;
                    }
                }

                if (mIsHorizontal) {
                    if (mIsShown && mPreItemView != mCurrentItemView) {
                        /**
                         * 情况二： 一个Item的右边布局已经显示，
                         * 这时候左右滑动另外一个item,那个右边布局显示的item隐藏其右边布局
                         * 向左滑动只触发该情况，向右滑动还会触发情况五
                         */
                        hiddenRight(mPreItemView);
                    }

                    if (mIsShown && mPreItemView == mCurrentItemView) {
                        dx = dx - mRightViewWidth;
                    }
                    if (dx < 0 && dx > -mRightViewWidth) {
                        mCurrentItemView.scrollTo((int) (-dx), 0);
                    }
                    return true;
                } else {
                    if (mIsShown) {
                        /**
                         * 情况三： 一个Item的右边布局已经显示，
                         * 这时候上下滚动ListView,那么那个右边布局显示的item隐藏其右边布局
                         */
                        hiddenRight(mPreItemView);
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                clearPressedState();
                if (mIsShown) {
                    /**
                     * 情况四： 一个Item的右边布局已经显示， 这时候左右滑动当前一个item,那个右边布局显示的item隐藏其右边布局
                     */
                    hiddenRight(mPreItemView);
                }

                if (mIsHorizontal != null && mIsHorizontal) {
                    if (mFirstX - lastX > mRightViewWidth / 2) {
                        showRight(mCurrentItemView);
                    } else {
                        /**
                         * 情况五：
                         * <p>
                         * 向右滑动一个item,且滑动的距离超过了右边View的宽度的一半，隐藏之。
                         */
                        hiddenRight(mCurrentItemView);
                    }
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void clearPressedState() {
        if (mCurrentItemView != null) {
            mCurrentItemView.setPressed(false);
            setPressed(false);
            refreshDrawableState();
        }
    }

    private void showRight(View view) {

        Message msg = new MoveHandler().obtainMessage();
        msg.obj = view;
        msg.arg1 = view.getScrollX();
        msg.arg2 = mRightViewWidth;
        msg.sendToTarget();

        mIsShown = true;
    }

    public void hiddenRight(View view) {
        if (mCurrentItemView == null || view == null) {
            return;
        }
        Message msg = new MoveHandler().obtainMessage();//
        msg.obj = view;
        msg.arg1 = view.getScrollX();
        msg.arg2 = 0;
        msg.sendToTarget();
        mIsShown = false;
    }

    public void hiddenRightQucky(View view) {
        if (mCurrentItemView == null) {
            return;
        }
        mCurrentItemView.scrollTo(0, 0);
    }

    /**
     * show or hide right layout animation
     */
    @SuppressLint("HandlerLeak")
    class MoveHandler extends Handler {
        int stepX = 0;
        int fromX;
        int toX;
        View view;
        private boolean mIsInAnimation = false;

        private void animatioOver() {
            mIsInAnimation = false;
            stepX = 0;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (stepX == 0) {
                if (mIsInAnimation) {
                    return;
                }
                mIsInAnimation = true;
                view = (View) msg.obj;
                fromX = msg.arg1;
                toX = msg.arg2;
                stepX = (int) ((toX - fromX) * mDurationStep * 1.0 / mDuration);
                if (stepX < 0 && stepX > -1) {
                    stepX = -1;
                } else if (stepX > 0 && stepX < 1) {
                    stepX = 1;
                }
                if (Math.abs(toX - fromX) < 10) {
                    view.scrollTo(toX, 0);
                    animatioOver();
                    return;
                }
            }

            fromX += stepX;
            boolean isLastStep = (stepX > 0 && fromX > toX) || (stepX < 0 && fromX < toX);
            if (isLastStep) {
                fromX = toX;
            }
            view.scrollTo(fromX, 0);
            invalidate();
            if (!isLastStep) {
                this.sendEmptyMessageDelayed(0, mDurationStep);
            } else {
                animatioOver();
            }
        }
    }

    public int getRightViewWidth() {
        return mRightViewWidth;
    }

    public void setRightViewWidth(int mRightViewWidth) {
        this.mRightViewWidth = mRightViewWidth;
    }

    public void deleteItem(View v) {
        hiddenRight(v);
    }

    private float downX, downY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - downX) > Math.abs(ev.getY() - downY)) {
                    getParent().requestDisallowInterceptTouchEvent(true);// 这句话的作用
                    // 告诉父view，我的事件我自行处理，不要阻碍我。
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
