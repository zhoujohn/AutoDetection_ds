package com.common.baselib.ui.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.common.baselib.R;
import com.common.baselib.utils.DensityUtils;


public abstract class BaseDialog<DATA, BINDING extends ViewDataBinding> extends Dialog {

    public Activity mContext;
    public DialogConfig dialogConfig;
    public DATA bean;
    public BINDING mBinding;
    public View contentView;

    public BaseDialog(Context context, DialogConfig dialogConfig) {
        super(context, R.style.MyDialogStyle);
        mContext = (Activity) context;
        this.dialogConfig = dialogConfig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = initView();
        setContentView(contentView);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) (DensityUtils.getWidth() * dialogConfig.builder.widthPer);
        if (dialogConfig.builder.height != 0) {
            layoutParams.height = dialogConfig.builder.height;
        }
        window.setAttributes(layoutParams);

        setCancelable(dialogConfig.builder.isCancle);
        setCanceledOnTouchOutside(dialogConfig.builder.isCancle);
    }

    // 设置数据
    public void setData(DATA t) {
        this.bean = t;
    }

    // 获取数据
    public DATA getData() {
        return bean;
    }

    @Override
    public void show() {
        super.show();
        showViewData();
    }

    /**
     * 获取控件
     */
    protected View initView() {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), getLayoutId(), null, false);
        return mBinding.getRoot();
    }

    /**
     * getLayoutId
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 显示dialog内容
     */
    protected abstract void showViewData();


    public static class DialogConfig {
        Builder builder;

        public DialogConfig(Builder builder) {
            this.builder = builder;
        }
    }


    public static class Builder {
        public float widthPer;
        public int height;
        public boolean isCancle;

        public Builder widthPer(float widthPer) {
            this.widthPer = widthPer;
            return this;
        }

        public Builder isCancle(boolean isCancle) {
            this.isCancle = isCancle;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public DialogConfig build() {
            return new DialogConfig(this);
        }
    }

    public static boolean isShowing(Dialog dialog) {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    public static void diss(Dialog dialog) {
        if (isShowing(dialog)) {
            dialog.dismiss();
        }
    }


}
