package com.common.baselib.utils;

import android.os.Handler;
import android.os.Message;


/**
 * Created by Administrator on 2017/8/5 0005.
 */

public class CountHelper {

    public long mCount;

    private OnTimeChangeListener mOnTimeReachListener;

    public CountHelper(OnTimeChangeListener mOnTimeReachListener, long maxCount) {
        this.mOnTimeReachListener = mOnTimeReachListener;
        mCount = maxCount;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mCount == 0) {
                if (mOnTimeReachListener != null) {
                    mOnTimeReachListener.onTimeReach();
                }
            } else {
                mCount -= 1;
                if (mOnTimeReachListener != null) {
                    mOnTimeReachListener.onTimeChange(mCount);
                }
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    public void startCount() {
        mHandler.sendEmptyMessage(0);
    }

    public void stopCount() {
        mHandler.removeMessages(0);
    }
    public void setCount(long count){
        this.mCount = count;
    }

    public boolean isTimeReach() {
        return mCount <= 0;
    }

    public interface OnTimeChangeListener {

        void onTimeReach();

        void onTimeChange(long currTime);
    }

    public void setmOnTimeReachListener(OnTimeChangeListener mOnTimeReachListener) {
        this.mOnTimeReachListener = mOnTimeReachListener;
    }


}
