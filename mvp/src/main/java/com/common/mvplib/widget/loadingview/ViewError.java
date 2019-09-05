package com.common.mvplib.widget.loadingview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.baselib.bean.BaseResponse;
import com.common.baselib.utils.StringUtils;
import com.common.mvplib.R;
import com.common.mvplib.listener.Action;
/**
 * ================================================
 * 包名：com.common.mvplib.widget.loadingview
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:58
 * 描述：
 * ================================================
 */
public class ViewError extends LinearLayout implements IError {

    private Activity mContext;

    public RelativeLayout rl_error;
    public ImageView iconError;
    public TextView txtErrorMsg, txt_refesh;

    public ViewError(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) context;
        LayoutInflater mInflater = mContext.getLayoutInflater();
        mInflater.inflate(getLayoutId(attrs), this, true);
        iconError = (ImageView) findViewById(R.id.iconError);
        txtErrorMsg = (TextView) findViewById(R.id.txtErrorMsg);
        rl_error = (RelativeLayout) findViewById(R.id.rl_error);
        txt_refesh = (TextView) findViewById(R.id.txt_refesh);

        setClickable(true);
    }

    @Override
    public int getLayoutId(AttributeSet attrs) {
        return R.layout.view_data_error;
    }

    @Override
    public void showError(BaseResponse errorBean) {
        try {
            String resultCode = errorBean.error;
            String showMsg = errorBean.msg;
            int showIcon = EmptyType.OPT_DEFAULT.icon;

//            if (VolleyErrorHelper.ExceptionMap.containsKey(resultCode)) {
//                showMsg = VolleyErrorHelper.ExceptionMap.get(resultCode);
//                if (resultCode == NetworkCode.NETWORK_NOLINK_CODE || resultCode == NetworkCode.NETWORK_TIMEOUT_CODE) {// 没有网络
//                    showIcon = R.drawable.icon_network_error;
//                }
//            }
            showError(showIcon, showMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRefreshListener(final Action.Action1<Integer> onRefresh) {
        if (onRefresh != null) {
            txt_refesh.setVisibility(VISIBLE);
            rl_error.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRefresh.call(0);
                }
            });
        }
    }

    /**
     * 数据加载为空，显示全屏
     *
     * @param iconResId 显示的图片资源
     * @param message   显示的信息
     */
    public void showError(int iconResId, String message) {
        iconError.setImageResource(iconResId == 0 ? EmptyType.OPT_DEFAULT.icon : iconResId);
        txtErrorMsg.setText(StringUtils.isNull(message) ? "出错了" : message);
    }

    public void showError(EmptyType emptyType) {
        showError(emptyType.icon, emptyType.message);
    }

    public void setIsShowOnLoading(boolean isShow) {
        rl_error.setBackgroundColor(isShow ? getResources().getColor(R.color.transparent) : getResources().getColor(R.color.white));
    }
}
