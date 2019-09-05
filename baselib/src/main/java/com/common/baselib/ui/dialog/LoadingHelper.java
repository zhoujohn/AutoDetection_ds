package com.common.baselib.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.common.baselib.R;
import com.common.baselib.utils.StringUtils;


/**
 * Created by dongfang5 on 2018/8/6.
 */

public class LoadingHelper {

    private LoadingDialog mLoadingDialog;

    public LoadingHelper() {

    }

    public void showIfNotExist(Fragment fragment, boolean canceledOnTouchOutside) {
        showIfNotExist(fragment, "", canceledOnTouchOutside, true, null);
    }

    public void showIfNotExist(Fragment fragment, boolean canceledOnTouchOutside, boolean cancleable) {
        showIfNotExist(fragment, "", canceledOnTouchOutside, cancleable, null);
    }

    public void showIfNotExist(Fragment fragment, String message, boolean canceledOnTouchOutside, boolean cancleable) {
        showIfNotExist(fragment, message, canceledOnTouchOutside, cancleable, null);
    }


    public void showIfNotExist(Fragment fragment, int messageId, boolean canceledOnTouchOutside, boolean cancleable) {
        showIfNotExist(fragment, fragment.getResources().getString(messageId), canceledOnTouchOutside, cancleable, null);
    }


    public void showIfNotExist(Fragment fragment, int strId, boolean canceledOnTouchOutside, boolean cancleable, DialogInterface.OnDismissListener onDismissListener) {
        showIfNotExist(fragment, fragment.getResources().getString(strId), canceledOnTouchOutside, cancleable, onDismissListener);
    }

    public void showIfNotExist(Fragment fragment, String message, boolean canceledOnTouchOutside, boolean cancleable, DialogInterface.OnDismissListener onDismissListener) {
        if (fragment != null && !(mLoadingDialog != null && mLoadingDialog.isShowing())) {
            mLoadingDialog = LoadingDialog.getDialog(null, fragment, message, canceledOnTouchOutside, cancleable, onDismissListener);
            if (mLoadingDialog != null) {
                mLoadingDialog.show();
            }
        }
    }
    public void showIfNotExist(Activity activity, String message, boolean canceledOnTouchOutside, boolean cancleable, DialogInterface.OnDismissListener onDismissListener) {
        if (activity != null && !(mLoadingDialog != null && mLoadingDialog.isShowing())) {
            mLoadingDialog = LoadingDialog.getDialog(activity, null, message, canceledOnTouchOutside, cancleable, onDismissListener);
            if (mLoadingDialog != null) {
                mLoadingDialog.show();
            }
        }
    }

    public void setLoadingDialogText(String text, String loadingProgress) {
        if (mLoadingDialog == null || !mLoadingDialog.isShowing()) {
            return;
        }
        TextView message = mLoadingDialog.findViewById(R.id.tv_dialog_progress_message);
        message.setText(text);
        if (!StringUtils.isNull(loadingProgress)) {
            TextView tvProgress = mLoadingDialog.findViewById(R.id.tv_dialog_progress_value);
            tvProgress.setVisibility(View.VISIBLE);
            tvProgress.setText(loadingProgress);
        }

    }

    public void dismissIfExist() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }
}
