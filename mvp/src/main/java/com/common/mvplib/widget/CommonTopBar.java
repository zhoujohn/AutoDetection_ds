package com.common.mvplib.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.baselib.utils.CommonUtils;
import com.common.baselib.utils.DensityUtils;
import com.common.baselib.utils.StringUtils;
import com.common.mvplib.R;

/**
 * ================================================
 * 包名：com.common.mvplib.widget
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:56
 * 描述：
 * ================================================
 */
public class CommonTopBar extends FrameLayout implements View.OnClickListener {
    public Activity mContext;

    public ImageView iv_right;
    public ImageView iv_left;
    public TextView txt_title;
    public TextView txt_right;
    public LinearLayout top_con;
    public View bottom_line;

    private boolean isShowBottomLine = true;
    private LinearLayout ll_title;
    private ImageView iv_title;

    public CommonTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) context;
        LayoutInflater mInflater = mContext.getLayoutInflater();
        mInflater.inflate(getLayoutId(), this, true);

        findView();
        init(context, attrs);
        setListener();
    }

    public void findView() {
        iv_right = (ImageView) findViewById(R.id.iv_right);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        ll_title = (LinearLayout) findViewById(R.id.ll_title);
        txt_title = (TextView) findViewById(R.id.txt_title);
        iv_title = (ImageView) findViewById(R.id.iv_title);
        txt_right = (TextView) findViewById(R.id.txt_right);

        top_con = (LinearLayout) findViewById(R.id.common_header_container);
        bottom_line = findViewById(R.id.bottom_line);

        top_con.setPadding(0, CommonUtils.getStatusBarHeight(mContext), 0, 0);
    }

    public void setListener() {
        iv_right.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        txt_right.setOnClickListener(this);
        top_con.setOnClickListener(this);
    }

    public int getLayoutId() {
        return R.layout.common_bar;
    }

    public TypedArray ta;

    /**
     * 初始化文字图标
     *
     * @param context
     * @param attrs
     */
    public void init(Context context, AttributeSet attrs) {
        ta = mContext.obtainStyledAttributes(attrs, R.styleable.CommonTopBar);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int itemId = ta.getIndex(i);
            if (itemId == R.styleable.CommonTopBar_titleCenter) {// 标题
                String mTitle = ta.getString(itemId);
                if (StringUtils.isNotNull(mTitle)) {
                    txt_title.setText(mTitle);
                }
            } else if (itemId == R.styleable.CommonTopBar_rightTxt) {// 右边文字（隐藏右边图标）
                String rightText = ta.getString(itemId);
                if (StringUtils.isNotNull(rightText)) {
                    txt_right.setText(rightText);
                    showRightImageOrText(false, true);
                }
            } else if (itemId == R.styleable.CommonTopBar_leftImageRes) {// 左边图标
                int leftImageRes = ta.getResourceId(itemId, -1);
                if (leftImageRes != -1) {
                    iv_left.setImageResource(leftImageRes);
                }
            } else if (itemId == R.styleable.CommonTopBar_rightImageRes) {//右边图标（隐藏右边文字）
                int rightImageRes = ta.getResourceId(itemId, -1);
                if (rightImageRes != -1) {
                    iv_right.setImageResource(rightImageRes);
                    showRightImageOrText(true, false);
                }
            } else if (itemId == R.styleable.CommonTopBar_isShowLeft) {//// 是否显示左边
                boolean isShowLeft = ta.getBoolean(itemId, false);
                if (isShowLeft) {
                    iv_right.setVisibility(View.VISIBLE);
                } else {
                    iv_right.setVisibility(View.INVISIBLE);
                }
            } else if (itemId == R.styleable.CommonTopBar_rightTxtColor) { // 是否显示左边
                int txtrightColor =
                        ta.getColor(itemId, getResources().getColor(R.color.color_333333));
                txt_right.setTextColor(txtrightColor);
            } else if (itemId == R.styleable.CommonTopBar_isShowBottomLine) {
                isShowBottomLine = ta.getBoolean(itemId, true);
                bottom_line.setVisibility(isShowBottomLine ? VISIBLE : GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();

        if (vId == R.id.iv_left) {
            if (onClickLeftListener != null) {
                onClickLeftListener.OnClickLeft();
            } else {
                mContext.finish();
            }
        } else if (vId == R.id.txt_right) {
            if (onClickRightListener != null) {
                onClickRightListener.OnClickRight();
            }
        } else if (vId == R.id.iv_right) {
            if (onClickRightListener != null) {
                onClickRightListener.OnClickRight();
            }
        }
    }

    /**
     * 设置标题文字
     */
    public void setTitle(String title) {
        txt_title.setText(title);
    }

    /**
     * 控制右边的图片和文字是否显示
     */
    public void showRightImageOrText(boolean showRightImage, boolean showRightText) {
        if (showRightImage) {
            iv_right.setVisibility(View.VISIBLE);
        } else {
            iv_right.setVisibility(View.GONE);
        }
        if (showRightText) {
            txt_right.setVisibility(View.VISIBLE);
        } else {
            txt_right.setVisibility(View.GONE);
        }
    }

    /**
     * 设置右边图片
     */
    public void setRightImage(int drawableRes) {
        if (drawableRes == 0) {
            return;
        }
        iv_right.setImageResource(drawableRes);
        showRightImageOrText(true, false);
    }

    /**
     * 设置右上角文字
     */
    public void setRightText(String headerRightText) {
        if (StringUtils.isNull(headerRightText)) {
            return;
        }
        txt_right.setText(headerRightText);
        showRightImageOrText(false, true);
    }

    /**
     * 设置左边图片
     */
    private void setLeftImage(int drawableRes) {
        if (drawableRes < 0) {
            iv_left.setVisibility(GONE);
        } else if (drawableRes == 0) {

        } else {
            iv_left.setImageResource(drawableRes);
        }
    }

    private OnClickRightListener onClickRightListener;
    private OnClickLeftListener onClickLeftListener;

    public void setOnClickRightListener(OnClickRightListener onClickRightListener) {
        this.onClickRightListener = onClickRightListener;
    }

    public void setOnClickLeftListener(OnClickLeftListener onClickLeftListener) {
        this.onClickLeftListener = onClickLeftListener;
    }

    public void isShowBottomLine(boolean isShowBottomLine) {
        this.isShowBottomLine = isShowBottomLine;
        bottom_line.setVisibility(isShowBottomLine ? VISIBLE : GONE);

    }

    public interface OnClickRightListener {
        void OnClickRight();
    }

    public interface OnClickLeftListener {
        void OnClickLeft();
    }

    public CommonTopBar title(String title) {
        setTitle(title);
        return this;
    }

    public CommonTopBar rightImgRes(int rightImgRes) {
        setRightImage(rightImgRes);
        return this;
    }

    public CommonTopBar leftImgRes(int leftImgRes) {
        setLeftImage(leftImgRes);
        return this;
    }

    public CommonTopBar rightText(String rightText) {
        setRightText(rightText);
        return this;
    }

    public void setLeftImageSamll(int padding) {
        iv_left.setPadding(0, DensityUtils.dip2px(padding), 0, DensityUtils.dip2px(padding));
    }

    public void setRightImageSamll(int padding) {
        iv_right.setPadding(0, DensityUtils.dip2px(padding), 0, DensityUtils.dip2px(padding));
    }

}
