package com.common.mvplib.widget.loadingview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.baselib.utils.StringUtils;
import com.common.mvplib.R;
import com.common.mvplib.listener.Action;


/**
 * ================================================
 * 包名：com.common.mvplib.widget.loadingview
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:57
 * 描述：
 * ================================================
 */
public class ViewEmpty extends LinearLayout implements IEmpty {

    public RelativeLayout rl_empty;
    public ImageView icon_state;
    public TextView txt_message, txt_refesh;

    public Activity mContext;


    public ViewEmpty(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) context;
        dealAttribute(attrs);
        LayoutInflater mInflater = mContext.getLayoutInflater();
        mInflater.inflate(getLayoutId(), this, true);
        initView();
        setClickable(true);
    }

    @Override
    public void initView() {
        rl_empty = (RelativeLayout) findViewById(R.id.rl_empty);
        icon_state = (ImageView) findViewById(R.id.icon_state);
        txt_message = (TextView) findViewById(R.id.txt_message);
        txt_refesh = (TextView) findViewById(R.id.txt_refesh);

        setClickable(true);
    }

    @Override
    public int getLayoutId() {
        return EmptyLayoutType.getLayout(layoutKey);
    }

    int layoutKey = 0;

    @Override
    public void dealAttribute(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CommonLoadingView);
        if (ta.hasValue(R.styleable.CommonLoadingView_loadEmptyLayoutKey)) {
            layoutKey = ta.getInteger(R.styleable.CommonLoadingView_loadEmptyLayoutKey, 0);
        }
    }

    @Override
    public void showEmpty(EmptyType emptyType) {
        showEmpty(emptyType.icon, emptyType.message, emptyType.messageSub);
    }

    /**
     * 数据加载为空，显示全屏
     *
     * @param iconResId 显示的图片资源
     * @param message   显示的信息
     */
    public void showEmpty(int iconResId, String message, String meaageSamll) {
        // 刷新
        if (iconResId == 0) {
            icon_state.setImageResource(EmptyType.OPT_DEFAULT.icon);
        } else {
            icon_state.setImageResource(iconResId);
        }
        if (StringUtils.isNull(message)) {
            txt_message.setText("暂无数据");
        } else {
            txt_message.setText(message);
        }
    }

    @Override
    public void showState(boolean isShow) {
        setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public enum EmptyLayoutType {
        type1(0, R.layout.view_data_empty);

        public int id;
        public int layout;

        private EmptyLayoutType(int id, int layout) {
            this.id = id;
            this.layout = layout;
        }

        public static int getLayout(int id) {
            for (EmptyLayoutType type : EmptyLayoutType.values()) {
                if (type.id == id) {
                    return type.layout;
                }
            }
            return type1.layout;
        }
    }

    public void setIsShowOnLoading(boolean isShow) {
        rl_empty.setBackgroundColor(isShow ? getResources().getColor(R.color.transparent) : getResources().getColor(R.color.white));
    }

    @Override
    public void setRefreshCallback(final Action.Action1<Integer> action1) {
        if (action1 != null) {
            txt_refesh.setVisibility(View.VISIBLE);
            rl_empty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action1.call(0);
                }
            });
        }
    }
}
