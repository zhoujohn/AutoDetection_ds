package com.frt.autodetection.mvp.ui.widget.dialog;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.frt.autodetection.R;
import com.frt.autodetection.mvp.ui.widget.dialog.base.BaseDialog;
import com.frt.autodetection.mvp.ui.widget.dialog.base.BaseDialogFragment;

import androidx.fragment.app.FragmentActivity;


public final class SetDialog {

    public static final class Builder
            extends BaseDialogFragment.Builder<Builder>
            implements View.OnClickListener {

        private OnListener mListener;
        private boolean mAutoDismiss = true; // 设置点击按钮后自动消失
        private int level = 1;
        private int maxLevel = 5;
        private int minLevel = 1;

        private TextView mTvBrightnessLevel;
        private ImageView mBtnBrightnessAdd;
        private ImageView mBtnBrightnessMin;

        public Builder(FragmentActivity activity) {
            super(activity);

            setContentView(R.layout.dialog_set);
            setAnimStyle(BaseDialog.AnimStyle.IOS);
            setGravity(Gravity.CENTER);


            mTvBrightnessLevel = findViewById(R.id.v_brightness_level);
            mBtnBrightnessAdd = findViewById(R.id.v_brightness_add);
            mBtnBrightnessMin = findViewById(R.id.v_brightness_min);


            mBtnBrightnessAdd.setOnClickListener(this);
            mBtnBrightnessMin.setOnClickListener(this);
        }

        public Builder setLevel(int level) {
            this.level = level;
            mTvBrightnessLevel.setText(level + "");
            return this;
        }

        public Builder setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder setMinLevel(int minLevel) {
            this.minLevel = minLevel;
            return this;
        }


        public Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }

        public Builder setListener(OnListener l) {
            mListener = l;
            return this;
        }

        @Override
        public BaseDialog create() {
            return super.create();
        }

        /**
         * {@link View.OnClickListener}
         */
        @Override
        public void onClick(View v) {
            if (mAutoDismiss) {
//                dismiss();
            }

            if (mListener == null) return;

            if (v == mBtnBrightnessAdd && level < maxLevel) {
                level++;
                mTvBrightnessLevel.setText(level + "");
                mListener.onBrightnessChange(getDialog(), level);
            } else if (v == mBtnBrightnessMin && level > minLevel) {
                level--;
                mTvBrightnessLevel.setText(level + "");
                mListener.onBrightnessChange(getDialog(), level);
            }
        }
    }

    public interface OnListener {

        /**
         * 亮度改变
         */
        void onBrightnessChange(Dialog dialog, int level);

    }
}