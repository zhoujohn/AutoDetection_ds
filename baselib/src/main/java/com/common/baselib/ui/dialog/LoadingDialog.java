package com.common.baselib.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.baselib.R;


public class LoadingDialog extends Dialog {

    private LoadingDialog(Activity context) {
        super(context);
    }

    /**
     * 使用该构造方法设置主题为透明
     *
     * @param context
     * @param theme
     */
    public LoadingDialog(Activity context, int theme) {
        super(context, theme);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = findViewById(R.id.iv_dialog_progress);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();
    }

    private static View getView(Context context) {
        View view = View.inflate(context, R.layout.layout_dialog_progress, null);
        ImageView spinnerImageView = view.findViewById(R.id.iv_dialog_progress);
        spinnerImageView.setBackgroundResource(R.drawable.animation_resource_spinner);
        final AnimationDrawable animation = (AnimationDrawable) spinnerImageView.getBackground();
        animation.setOneShot(false);
        new Runnable() {
            @Override
            public void run() {
                // 开启动画
                animation.start();
            }
        };
        return view;
    }

    public static LoadingDialog getDialog(Activity activity, Fragment fragment, String message, boolean canceledOnTouchOutside, boolean cancleable, OnDismissListener onDismissListener) {
        if (fragment != null) {
            activity = fragment.getActivity();
        }
        if (activity == null) {
            return null;
        }
        LoadingDialog dialog = new LoadingDialog(activity, R.style.ProgressDialog);
        dialog.setContentView(getView(activity));
        TextView tv_message = dialog.findViewById(R.id.tv_dialog_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_message.setVisibility(View.VISIBLE);
            tv_message.setText(message);
        } else {
            tv_message.setVisibility(View.GONE);
        }
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dialog.setCancelable(cancleable);
        dialog.setOnDismissListener(onDismissListener);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }


}
