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


public final class CalDialog {

    public static final class Builder
            extends BaseDialogFragment.Builder<Builder>
            implements View.OnClickListener {

        private OnListener mListener;
        private boolean mAutoDismiss = true; // 设置点击按钮后自动消失
        private int type = 1;

        private TextView mCancelView;
        private TextView mConfirmView;
        private TextView mTvCalType;
        private ImageView mIvCalType1;
        private ImageView mIvCalType2;

        public Builder(FragmentActivity activity) {
            super(activity);

            setContentView(R.layout.dialog_cal);
            setAnimStyle(BaseDialog.AnimStyle.IOS);
            setGravity(Gravity.CENTER);


            mTvCalType = findViewById(R.id.v_cal_type_tv);
            mIvCalType1 = findViewById(R.id.v_iv_zhuibian);
            mIvCalType2 = findViewById(R.id.v_iv_zhuixian);
            mCancelView = findViewById(R.id.dialog_sex_cancel);
            mConfirmView = findViewById(R.id.dialog_sex_confirm);

            mIvCalType1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 1) return;
                    mTvCalType.setText("追边模式");
                    mIvCalType1.setImageResource(R.mipmap.dialog_btn_zhuibian_selected);
                    mIvCalType2.setImageResource(R.mipmap.dialog_btn_zhuixian_normal);
                    type = 1;
                }
            });

            mIvCalType2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 2) return;

                    mTvCalType.setText("追线模式");
                    mIvCalType1.setImageResource(R.mipmap.dialog_btn_zhuibian_normal);
                    mIvCalType2.setImageResource(R.mipmap.dialog_btn_zhuixian_selected);
                    type = 2;
                }
            });


            mCancelView.setOnClickListener(this);
            mConfirmView.setOnClickListener(this);
        }

        public Builder setType(int type) {
            this.type = type;
            mTvCalType.setText(type == 1 ? "追边模式" : "追线模式");
            mIvCalType1.setImageResource(type == 1 ? R.mipmap.dialog_btn_zhuibian_selected : R.mipmap.dialog_btn_zhuibian_normal);
            mIvCalType2.setImageResource(type == 2 ? R.mipmap.dialog_btn_zhuixian_selected : R.mipmap.dialog_btn_zhuixian_normal);
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
                dismiss();
            }

            if (mListener == null) return;

            if (v == mConfirmView) {
//                if (mContentView.getText().toString().trim().length() < 4) {
//                    ToastUtils.showShortToast("昵称长度4~10");
//                    return;
//                }


                // 判断输入是否为空
                mListener.onConfirm(getDialog(), type);
            } else if (v == mCancelView) {
                mListener.onCancel(getDialog());
            }
        }
    }

    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(Dialog dialog, int type);

        /**
         * 点击取消时回调
         */
        void onCancel(Dialog dialog);

    }
}