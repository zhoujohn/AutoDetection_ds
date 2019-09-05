package com.common.mvplib.base.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.common.baselib.bean.BaseResponse;
import com.common.baselib.ui.dialog.LoadingHelper;
import com.common.baselib.utils.CommonUtils;
import com.common.mvplib.R;
import com.common.mvplib.config.LayoutConfig;
import com.common.mvplib.engine.AppManager;
import com.common.mvplib.mvp.IView;
import com.common.mvplib.widget.loadingview.CommonLoadingView;
import com.common.mvplib.widget.loadingview.EmptyType;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.OnKeyboardListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;

/**
 * ================================================
 * 包名：com.common.mvplib.base.activity
 * 创建人：sws
 * 创建时间：2019/5/27  下午 03:39
 * 描述：
 * ================================================
 */
public abstract class BaseActivity<BINDING extends ViewDataBinding> extends AppCompatActivity implements IView, BGASwipeBackHelper.Delegate {

    public BINDING mBinding;
    public BaseActivity mBaseContext;
    public LayoutConfig layoutConfig;
    private boolean hasFinish;
    protected volatile LoadingHelper mDialogLoadingHelper;

    private View rootView;
    int rootViewVisibleHeight;
    private boolean softKeyboardIsShow = false;
    private BGASwipeBackHelper mSwipeBackHelper;//侧滑返回

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isNotStatusBarTransparent()) {
//            statusBarTransparent();
//            _setInvasion();

        }
        if (isHideSystem()) {
            hideSystem();
        }
        if (isSupportSwipeBack()) {
            initSwipeBack();
        }
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //处理部分手机状态栏颜色问题
//            StatusBarUtil.setLightMode(this, false);
        }
        mBaseContext = this;
        beforeInitView();
        layoutConfig = LayoutConfig.of(initLayoutConfig());
        layoutConfig.setBaseActivity(this);
        setContentView(layoutConfig.initContent());
        afterInitView();
        setKeyBoardLayoutChangeListener();
        setListener();
        loadNetWork();
        AppManager.getInstance().addActivity(this);

    }

    private void initSwipeBack() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);

        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
        // 下面几项可以不配置，这里只是为了讲述接口用法。

        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper.setSwipeBackEnable(true);
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper.setIsWeChatStyle(true);
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(true);
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
        mSwipeBackHelper.setSwipeBackThreshold(0.3f);
        // 设置底部导航条是否悬浮在内容上，默认值为 false
        mSwipeBackHelper.setIsNavigationBarOverlap(false);
    }

    @Override
    public void finish() {
        hasFinish = true;
        super.finish();
    }

    /**
     * 让字体不随系统变化
     *
     * @return
     */
    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return resources;
    }

    /**
     * 获取布局文件Id
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 配置 topbar and loading
     *
     * @return
     */
    public abstract LayoutConfig initLayoutConfig();

    /**
     * 初始化布局之前的逻辑
     *
     * @return
     */
    public void beforeInitView() {

    }

    /**
     * 初始化布局
     */
    public View initView() {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), getLayoutId(), null, false);
        return mBinding.getRoot();
    }

    /**
     * 初始化布局之后的逻辑
     */
    public abstract void afterInitView();

    /**
     * 加载数据
     */
    public abstract void loadNetWork();

    /**
     * 加载数据
     */
    public abstract void setListener();

    @Override
    protected void onDestroy() {
        dissLoadingDialog();
        CommonUtils.closeSoftKeyBoard(this.getWindow(), this);
        mBaseContext = null;
        AppManager.getInstance().finishActivity(this);
        System.gc();
        super.onDestroy();
    }

    public ViewDataBinding getBinding() {
        return DataBindingUtil.inflate(getLayoutInflater(), getLayoutId(), null, false);
    }

    public void showLoading(String loadingMsg) {
        layoutConfig.loading.showLoading();
    }

    public void showEmpty(EmptyType emptyType) {
        layoutConfig.loading.showEmpty(emptyType);
    }

    public void onLoadSuccess() {
        layoutConfig.loading.disLoading();
    }

    public void onLoadingError(BaseResponse error, boolean isRefresh) {
        if (isRefresh) {
            layoutConfig.loading.showError(error);
        } else {
            CommonLoadingView.showErrorToast(error);
        }
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOnResumeHideSoftKeyBord()) {
            hideSoftKeyboard(rootView);
        }
    }

    protected void hideSoftKeyboard(View view) {
        //强制隐藏键盘避免弹出键盘 与弹出popupwindow冲突
//        if(softKeyboardIsShow){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
    }

    public boolean isPageFinish() {
        return hasFinish;
    }

    protected boolean isNotStatusBarTransparent() {
        return true;
    }

    protected boolean isHideSystem() {
        return false;
    }

    protected boolean isOnResumeHideSoftKeyBord() {
        return false;
    }

    protected void setStatusBarLight() {
        ImmersionBar mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.navigationBarColor(R.color.white).statusBarDarkFont(false).init();
    }

    protected void setStatusBarDark() {
        ImmersionBar mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.navigationBarColor(R.color.white).statusBarDarkFont(true).init();
    }

    private void _setInvasion() {
        ImmersionBar mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.transparentStatusBar()                                                                     //透明状态栏，不写默认透明色
//                .transparentNavigationBar()                                                                 //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
                .transparentBar()
//                .fitsSystemWindows(true)
                //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
//                .statusBarColor(R.color.transparent)                                                     //状态栏颜色，不写默认透明色
                .navigationBarColor(R.color.white)                                                 //导航栏颜色，不写默认黑色
//                .barColor(R.color.colorPrimary)                                                           //同时自定义状态栏和导航栏颜色，不写默认状态栏为透明色，导航栏为黑色
//                .statusBarAlpha(0.3f)                                                                     //状态栏透明度，不写默认0.0f
//                .navigationBarAlpha(0.4f)  //导航栏透明度，不写默认0.0F
//                .barAlpha(0.3f)  //状态栏和导航栏透明度，不写默认0.0f
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
//                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
//                .autoDarkModeEnable(true) //自动状态栏字体和导航栏图标变色，必须指定状态栏颜色和导航栏颜色才可以自动变色哦
//                .autoStatusBarDarkModeEnable(true, 0.2f) //自动状态栏字体变色，必须指定状态栏颜色才可以自动变色哦
//                .autoNavigationBarDarkModeEnable(true, 0.2f) //自动导航栏图标变色，必须指定导航栏颜色才可以自动变色哦
//                .flymeOSStatusBarFontColor(R.color.btn3)  //修改flyme OS状态栏字体颜色
//                .fullScreen(true)      //有导航栏的情况下，activity全屏显示，也就是activity最下面被导航栏覆盖，不写默认非全屏
//                .hideBar(BarHide.FLAG_HIDE_BAR)  //隐藏状态栏或导航栏或两者，不写默认不隐藏
//                .addViewSupportTransformColor(toolbar)  //设置支持view变色，可以添加多个view，不指定颜色，默认和状态栏同色，还有两个重载方法
//                .titleBar(view)    //解决状态栏和布局重叠问题，任选其一
//                .titleBarMarginTop(view)     //解决状态栏和布局重叠问题，任选其一
//                .statusBarView(view)  //解决状态栏和布局重叠问题，任选其一
//                .fitsSystemWindows(true)    //解决状态栏和布局重叠问题，任选其一，默认为false，当为true时一定要指定statusBarColor()，不然状态栏为透明色，还有一些重载方法
//                .supportActionBar(true) //支持ActionBar使用
//                .statusBarColorTransform(R.color.orange)  //状态栏变色后的颜色
//                .navigationBarColorTransform(R.color.orange) //导航栏变色后的颜色
//                .barColorTransform(R.color.orange)  //状态栏和导航栏变色后的颜色
//                .removeSupportView(toolbar)  //移除指定view支持
//                .removeSupportAllView() //移除全部view支持
                .navigationBarEnable(true)   //是否可以修改导航栏颜色，默认为true
                .navigationBarWithKitkatEnable(true)  //是否可以修改安卓4.4和emui3.1手机导航栏颜色，默认为true
//                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
//                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)  //单独指定软键盘模式
                .setOnKeyboardListener(new OnKeyboardListener() {    //软键盘监听回调
                    @Override
                    public void onKeyboardChange(boolean isPopup, int keyboardHeight) {
                        //LogUtils.e(isPopup);  //isPopup为true，软键盘弹出，为false，软键盘关闭
                    }
                })
//                .setOnNavigationBarListener(onNavigationBarListener) //导航栏显示隐藏监听，目前只支持华为和小米手机
                .addTag("tag")  //给以上设置的参数打标记
                .getTag("tag")  //根据tag获得沉浸式参数
//                .reset()  //重置所以沉浸式参数
                .init();  //必须调用方可沉浸式
    }

    public void statusBarTransparent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //解决华为灰色状态栏背景bug
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.TRANSPARENT);
                //设置虚拟按键为白色
                window.setNavigationBarColor(Color.WHITE);
            }
            //设置深色字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


            // getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//恢复状态栏白色字体
            // 透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

//        if (isFinishing()) {
//            return;
//        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            return;
//        }
//
//        //透明状态栏
//        Window window = getWindow();
//        if (window == null) {
//            return;
//        }
//
//        // 防止第一次播放视频，闪黑屏幕
//        window.setFormat(PixelFormat.TRANSLUCENT);
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            //[4.4, 5.0)
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        } else {
//            //[5.0, +)
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            try {
//                window.setStatusBarColor(Color.TRANSPARENT);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 隐藏状态栏以及底部虚拟键
     */
    public void hideSystem() {


        //4.1及以上通用flags组合
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    flags | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    public void showLoadingDialog(String message, boolean canceledOnTouchOutside, boolean cancleable, DialogInterface.OnDismissListener onDismissListener) {
        if (mDialogLoadingHelper == null) {
            mDialogLoadingHelper = new LoadingHelper();
        }
        try {
            mDialogLoadingHelper.showIfNotExist(this, message, canceledOnTouchOutside, cancleable, onDismissListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoadingDialog(boolean canceledOnTouchOutside) {
        showLoadingDialog("", canceledOnTouchOutside, true, null);
    }

    public void showLoadingDialog(String message, boolean canceledOnTouchOutside) {
        showLoadingDialog(message, canceledOnTouchOutside, true, null);
    }

    public void dissLoadingDialog() {
        if (mDialogLoadingHelper == null) {
            return;
        }
        try {
            mDialogLoadingHelper.dismissIfExist();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听软键盘弹出收起
     */
    private void setKeyBoardLayoutChangeListener() {
        rootView = getWindow().getDecorView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获取当前根视图在屏幕上显示的大小
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int visibleHeight = r.height();
                if (rootViewVisibleHeight == 0) {
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }
                //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
                if (rootViewVisibleHeight == visibleHeight) {
                    return;
                }
                //根视图显示高度变小超过200，可以看作软键盘显示了
                if (rootViewVisibleHeight - visibleHeight > 200) {
                    rootViewVisibleHeight = visibleHeight;
                    softKeyboardIsShow = true;
                    if (mOnKeyBoardLayoutChangeListener != null)
                        mOnKeyBoardLayoutChangeListener.onKeyBoardLayoutChange(true);
                    return;
                }
                //根视图显示高度变大超过200，可以看作软键盘隐藏了
                if (visibleHeight - rootViewVisibleHeight > 200) {
                    rootViewVisibleHeight = visibleHeight;
                    softKeyboardIsShow = false;
                    if (mOnKeyBoardLayoutChangeListener != null)
                        mOnKeyBoardLayoutChangeListener.onKeyBoardLayoutChange(false);
                    return;
                }
            }
        });
    }


    public interface OnKeyBoardLayoutChangeListener {
        void onKeyBoardLayoutChange(boolean isShow);
    }

    private OnKeyBoardLayoutChangeListener mOnKeyBoardLayoutChangeListener;

    public void setKeyBoardLayoutChangeListener(OnKeyBoardLayoutChangeListener onKeyBoardLayoutChangeListener) {
        this.mOnKeyBoardLayoutChangeListener = onKeyBoardLayoutChangeListener;
    }

    /**
     * {@link BGASwipeBackHelper.Delegate}
     */

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 正在滑动返回
     *
     * @param slideOffset 从 0 到 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        if (mSwipeBackHelper != null) {
            mSwipeBackHelper.swipeBackward();
        }

    }

    @Override
    public void onBackPressed() {
        if (mSwipeBackHelper != null) {
            // 正在滑动返回的时候取消返回按钮事件
            if (mSwipeBackHelper.isSliding()) {
                return;
            }
            mSwipeBackHelper.backward();
        }
        super.onBackPressed();
    }
}
