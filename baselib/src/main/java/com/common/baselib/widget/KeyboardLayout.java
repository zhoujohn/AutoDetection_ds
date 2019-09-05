package com.common.baselib.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class KeyboardLayout extends RelativeLayout {

    private static final String TAG = KeyboardLayout.class.getSimpleName();

    public static final byte KEYBOARD_STATE_SHOW = -3;
    public static final byte KEYBOARD_STATE_HIDE = -2;
    public static final byte KEYBOARD_STATE_INIT = -1;

    // true: show; false:hide
    private boolean lastStatus = false;

    private OnKeyboardChangedListener onKeyboardChangedListener;

    public KeyboardLayout(Context context) {
        super(context);
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnKeyboardChangedListener(OnKeyboardChangedListener keyboardChangedListener) {
        if (keyboardChangedListener == null) {
            return;
        }
        this.onKeyboardChangedListener = keyboardChangedListener;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private int screenheight = 0;

            public int getHeight() {
                if (screenheight > 0)
                    return screenheight;
                WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

                // int width = wm.getDefaultDisplay().getWidth();
                int height = wm.getDefaultDisplay().getHeight();
                screenheight = height;
                return screenheight;

            }

            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                ((Activity) getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);

                int screenHeight = getHeight();
                int heightDiff = screenHeight - (r.bottom - r.top);
                Log.d("Keyboard Size", "Size: " + heightDiff);
                boolean visible = Math.abs(heightDiff) > screenHeight / 3;

                if (lastStatus != visible) {
                    lastStatus = visible;
                    Log.d("Keyboard", "Keyboard " + (visible ? "opened" : "closed"));
                    if (!visible && onKeyboardChangedListener != null) {
                        onKeyboardChangedListener.onKeyboardChanged(false);
                    }
                    if (visible && onKeyboardChangedListener != null) {
                        onKeyboardChangedListener.onKeyboardChanged(true);
                    }
                }
            }
        });
    }


    public interface OnKeyboardChangedListener {
        void onKeyboardChanged(boolean isShow);
    }


}
