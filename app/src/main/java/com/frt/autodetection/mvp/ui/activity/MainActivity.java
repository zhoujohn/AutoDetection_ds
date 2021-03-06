package com.frt.autodetection.mvp.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.common.mvplib.config.LayoutConfig;
import com.frt.autodetection.R;
import com.frt.autodetection.base.BaseConfigActivity;
import com.frt.autodetection.constant.AppInfo;
import com.frt.autodetection.databinding.ActivityMainBinding;
import com.frt.autodetection.mvp.iview.IMainActivityView;
import com.frt.autodetection.mvp.presenter.MainActivityPresenter;
import com.frt.autodetection.mvp.ui.widget.calibration.CalibrationView;
import com.frt.autodetection.serial.OnKeyEventReceiveListener;
import com.frt.autodetection.serial.SerialPortTerminal;
import com.frt.autodetection.utils.helper.SpHelper;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class MainActivity extends BaseConfigActivity<MainActivityPresenter, ActivityMainBinding> implements IMainActivityView, View.OnClickListener, CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase _cameraBridgeViewBase;
    private int index = 0;
    //private int shift = 0;
    private int show = 0;
    private int offsetValue = 0;

    private boolean isShowSetLayout;
    private boolean isShowCalLayout;
    //TODO 应该获取默认
    //默认亮度值
    private int currentBrightnessLevel = 5;
    //默认 切换模式 追边0 追线1
    private int currentSwitchMode = 0;
    //手动 0  自动 1
    private int currentSwitchControl = 0;
    //亮度最大值 和 最小值
    private int maxBrightnessLevel = 9;
    private int minBrightnessLevel = 1;
    //亮度level值 分7段
    private int[] brightnessArr = {7, 14, 20, 28, 36, 42, 56, 70, 88};

    //设置类型 0 亮度 1 adr 2 pol
    private int currSetType = 0;
    //地址值
    private int currAddressLevel = 1;
    private int minAddressLevel = 1;
    private int maxAddressLevel = 2;
    //极性值
    private int currPolarityLevel = 0;
    private int minPolarityLevel = 0;
    private int maxPolarityLevel = 1;

    private int devi;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // 1）获取亮度值
                    currentBrightnessLevel = SpHelper.getInstance().getInt(AppInfo.CURRENT_BRIGHTNESS_LEVEL, 5);
                    mBinding.vBrightnessTv.setText(currentBrightnessLevel + "");
                    //初始化设置亮度
                    SerialPortTerminal.getInstance().writeBrightness(brightnessArr[currentBrightnessLevel - 1]);

                    // 2）get追边/追线模式
                    currentSwitchMode = SpHelper.getInstance().getInt(AppInfo.CURRENT_SWITCH_MODE, 0);
                    mBinding.vTopBtn1.setImageResource(currentSwitchMode == 0 ? R.drawable.icon_zhuibian_white : R.drawable.icon_zhuixian_white);
                    //  通过JNI通知算法 追边模式
                    _cameraBridgeViewBase.setCalibrationType(currentSwitchMode);
                    //  linedetection(0,currentSwitchMode);

                    // 3）手动/自动
                    currentSwitchControl = SpHelper.getInstance().getInt(AppInfo.CURRENT_SWITCH_CONTROL, 0);
                    mBinding.vTopBtn2.setImageResource(currentSwitchControl == 0 ? R.drawable.bottom_btn_3_normal : R.drawable.bottom_btn_4_normal);
                    if (currentSwitchControl == 0) {
                        SerialPortTerminal.getInstance().whiteBtn3();
                    } else {
                        SerialPortTerminal.getInstance().whiteBtn4();
                    }

                    // 4）通信地址
                    currAddressLevel = SpHelper.getInstance().getInt(AppInfo.CURRENT_ADDRESS_LEVEL, 1);
                    //初始化设置通信地址
                    SerialPortTerminal.getInstance().writeAddress(currAddressLevel);

                    // 5）数据输出极性
                    currPolarityLevel = SpHelper.getInstance().getInt(AppInfo.CURRENT_POLARITY_LEVEL, 0);
                    //初始化数据输出极性
                    SerialPortTerminal.getInstance().writePolarity(currPolarityLevel);
                    break;
                case 2:
                    mHandler.removeMessages(2);
                    /*
                    if (devi != 1000) {
                        mBinding.vTargetInfo.setTextColor(Color.WHITE);
                        int act_devi;
                        act_devi = (devi-320)/3;
                        if (act_devi <= -100) {
                            act_devi = -100;
                        } else if (act_devi >=100) {
                            act_devi = 100;
                        }
                        mBinding.vTargetInfo.setText(act_devi + "");
                    } else {
                        mBinding.vTargetInfo.setTextColor(Color.RED);
                        mBinding.vTargetInfo.setText("NO TARGET");
                    }*/
                    break;
            }
        }
    };


    private BaseLoaderCallback _baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load ndk built module, as specified in moduleName in build.gradle
                    // after opencv initialization
                    System.loadLibrary("native-lib");
                    _cameraBridgeViewBase.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
            }
        }
    };

    @Override
    protected MainActivityPresenter newPresenter() {
        return new MainActivityPresenter(this, this);
    }

    @Override
    public int getLayoutId() {
        setContentView(R.layout.activity_main);
        return R.layout.activity_main;
    }

    @Override
    public LayoutConfig initLayoutConfig() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Permissions for Android 6+
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);

        _cameraBridgeViewBase = findViewById(R.id.main_surface);

        _cameraBridgeViewBase.setCalibrationType(0);
        // _cameraBridgeViewBase.setROI(250, 80, 140, 40);
        _cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        _cameraBridgeViewBase.setCvCameraViewListener(this);
//        _cameraBridgeViewBase.setMaxFrameSize(640, 200);
        _cameraBridgeViewBase.setMaxFrameSize(640, 200);
//        _cameraBridgeViewBase.setMaxFrameSize(DensityUtils.dip2px(600),DensityUtils.dip2px(200));
        _cameraBridgeViewBase.SetCaptureFormat(1);

        _baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }


    @Override
    public void afterInitView() {

    }

    /**
     * 获取缺省/已保存数据进行配置
     * =======================================================
     * ==== Tips ===如果串口启动如果时间过长的话，可以考虑延迟调用此方法）
     * ========================================================
     */
    protected void initParamData() {
        mHandler.sendEmptyMessageDelayed(1, 2000);
    }


    @Override
    public void loadNetWork() {

    }


    @Override
    public void setListener() {
        /*mBinding.vCalibrationView.setOnOffsetChangeListener(new CalibrationView.OnOffsetChangeListener() {
            @Override
            public void OnOffsetChange(int value, float left, float top, float width, float height, int type) {
                offsetValue = value;
                //mBinding.vInfo.setText("中线偏移：" + value + "\n" + "框左上角（" + (int) left + "," + (int) top + ")\n框 width：" + width + "\n框 height:" + height);

                /*if (value > 0) {
                    mBinding.vLeftTv.setText("0");
                    mBinding.vRightTv.setText(value + "");
                } else if (value < 0) {
                    mBinding.vLeftTv.setText(value + "");
                    mBinding.vRightTv.setText("0");
                } else {
                    mBinding.vLeftTv.setText("0");
                    mBinding.vRightTv.setText("0");
                }*//*
                _cameraBridgeViewBase.setROI((int) left, (int) top, (int) width, (int) height, type);
                //Log.d(TAG, "mROI value isis:" + left + "width is:"+ width + "type is:"+ type);
//                setvalidpos(left, top, width, height);
            }
        }); */
        //按钮 ==> 底部按钮
        mBinding.vBtn1.setOnClickListener(this);
        mBinding.vBtn2.setOnClickListener(this);
        mBinding.vBtn3.setOnClickListener(this);
        mBinding.vBtn4.setOnClickListener(this);
        mBinding.vBtn5.setOnClickListener(this);
        //按钮 ==> 追边/追线
        mBinding.vBtnZhuibian.setOnClickListener(this);
        mBinding.vBtnZhuixian.setOnClickListener(this);
        //按钮 ==> 亮度 +/-
        mBinding.vBtnBrightnessMin.setOnClickListener(this);
        mBinding.vBtnBrightnessAdd.setOnClickListener(this);
        //按钮 ==> SET
        mBinding.vBtnSet.setOnClickListener(this);
        //按钮 ==> CAL
        mBinding.vBtnCal.setOnClickListener(this);

        //按钮  set 中的小图标切换  亮度、地址、极性
        mBinding.vSetTypeImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.v_btn1) {                        //底部按钮1~5 点击事件
            SerialPortTerminal.getInstance().whiteBtn1();
        } else if (id == R.id.v_btn2) {
            SerialPortTerminal.getInstance().whiteBtn2();
        } else if (id == R.id.v_btn3) {
            SerialPortTerminal.getInstance().whiteBtn3();
            //存储手动模式
            currentSwitchControl = 0;
            mBinding.vTopBtn2.setImageResource(R.drawable.bottom_btn_3_normal);
            SpHelper.getInstance().putInt(AppInfo.CURRENT_SWITCH_CONTROL, currentSwitchControl);
        } else if (id == R.id.v_btn4) {
            SerialPortTerminal.getInstance().whiteBtn4();
            //存储自动模式
            currentSwitchControl = 1;
            mBinding.vTopBtn2.setImageResource(R.drawable.bottom_btn_4_normal);
            SpHelper.getInstance().putInt(AppInfo.CURRENT_SWITCH_CONTROL, currentSwitchControl);
        } else if (id == R.id.v_btn5) {
            SerialPortTerminal.getInstance().whiteBtn5();
        } else if (id == R.id.v_btn_zhuibian) {         //追边
            currentSwitchMode = 0;
            mBinding.vTopBtn1.setImageResource(R.drawable.icon_zhuibian_white);
            SpHelper.getInstance().putInt(AppInfo.CURRENT_SWITCH_MODE, currentSwitchMode);
            _cameraBridgeViewBase.setCalibrationType(currentSwitchMode);
            //  通过JNI通知算法 追边模式
            //  linedetection(0,0);
        } else if (id == R.id.v_btn_zhuixian) {         //追线
            currentSwitchMode = 1;
            mBinding.vTopBtn1.setImageResource(R.drawable.icon_zhuixian_white);
            SpHelper.getInstance().putInt(AppInfo.CURRENT_SWITCH_MODE, currentSwitchMode);
            _cameraBridgeViewBase.setCalibrationType(currentSwitchMode);
            //  通过JNI通知算法 追线模式
            //  linedetection(0,1);
        } else if (id == R.id.v_btn_brightness_min) {    //亮度减小
            doSetTypeMin();
        } else if (id == R.id.v_btn_brightness_add) {    //亮度增加
            doSetTypeAdd();
        } else if (id == R.id.v_btn_set) {              //set
            isShowSetLayout = !isShowSetLayout;
            isShowSetLayout(isShowSetLayout);
        } else if (id == R.id.v_btn_cal) {              //cal
            isShowCalLayout = !isShowCalLayout;
            isShowCalLayout(isShowCalLayout);
        } else if (id == R.id.v_set_type_img) {              //set中的 小图标
            currSetType = ++currSetType % 3;
            switchSetTypeImg();
        }
    }

    /**
     * 设置 右侧按钮 +
     */
    private void doSetTypeAdd() {
        switch (currSetType) {
            case 0: //亮度
                if (currentBrightnessLevel < maxBrightnessLevel) {
                    currentBrightnessLevel++;
                    SpHelper.getInstance().putInt(AppInfo.CURRENT_BRIGHTNESS_LEVEL, currentBrightnessLevel);
                    mBinding.vBrightnessTv.setText(currentBrightnessLevel + "");
                    //通过串口写入亮度值
                    SerialPortTerminal.getInstance().writeBrightness(brightnessArr[currentBrightnessLevel - 1]);
                }
                break;
            case 1: //地址
                if (currAddressLevel < maxAddressLevel) {
                    currAddressLevel++;
                    SpHelper.getInstance().putInt(AppInfo.CURRENT_ADDRESS_LEVEL, currAddressLevel);
                    mBinding.vBrightnessTv.setText(currAddressLevel + "");
                    SerialPortTerminal.getInstance().writeAddress(currAddressLevel);
                }
                break;
            case 2: //极性
                if (currPolarityLevel < maxPolarityLevel) {
                    currPolarityLevel++;
                    SpHelper.getInstance().putInt(AppInfo.CURRENT_POLARITY_LEVEL, currPolarityLevel);
                    mBinding.vBrightnessTv.setText(currPolarityLevel + "");
                    SerialPortTerminal.getInstance().writeBrightness(currPolarityLevel);
                }
                break;
        }
    }

    /**
     * 设置 左侧按钮 -
     */
    private void doSetTypeMin() {
        switch (currSetType) {
            case 0: //亮度
                if (currentBrightnessLevel > minBrightnessLevel) {
                    currentBrightnessLevel--;
                    SpHelper.getInstance().putInt(AppInfo.CURRENT_BRIGHTNESS_LEVEL, currentBrightnessLevel);
                    mBinding.vBrightnessTv.setText(currentBrightnessLevel + "");
                    //通过串口写入亮度值
                    SerialPortTerminal.getInstance().writeBrightness(brightnessArr[currentBrightnessLevel - 1]);
                }
                break;
            case 1: //地址
                if (currAddressLevel > minAddressLevel) {
                    currAddressLevel--;
                    SpHelper.getInstance().putInt(AppInfo.CURRENT_ADDRESS_LEVEL, currAddressLevel);
                    mBinding.vBrightnessTv.setText(currAddressLevel + "");
                    SerialPortTerminal.getInstance().writeBrightness(currAddressLevel);
                }
                break;
            case 2: //极性
                if (currPolarityLevel > minPolarityLevel) {
                    currPolarityLevel--;
                    SpHelper.getInstance().putInt(AppInfo.CURRENT_POLARITY_LEVEL, currPolarityLevel);
                    mBinding.vBrightnessTv.setText(currPolarityLevel + "");
                    SerialPortTerminal.getInstance().writeBrightness(currPolarityLevel);
                }
                break;
        }
    }

    /**
     * 切换设置类型图标
     */
    private void switchSetTypeImg() {
        switch (currSetType) {
            case 0: //亮度
                mBinding.vSetTypeImg.setImageResource(R.mipmap.icon_brightness_white);
                mBinding.vBrightnessTv.setText(currentBrightnessLevel + "");
                break;
            case 1: //地址
                mBinding.vSetTypeImg.setImageResource(R.mipmap.icon_adr);
                mBinding.vBrightnessTv.setText(currAddressLevel + "");
                break;
            case 2: //极性
                mBinding.vSetTypeImg.setImageResource(R.mipmap.icon_pol);
                mBinding.vBrightnessTv.setText(currPolarityLevel + "");
                break;
        }
    }

    private void isShowSetLayout ( boolean isShowSetLayout){
        mBinding.vLayoutSet.setVisibility(isShowSetLayout ? View.VISIBLE : View.GONE);
        mBinding.vBtnSet.setText(isShowSetLayout ? "ⓧ" : "SET");
//        mBinding.vCalibrationView.setVisibility(View.INVISIBLE);
        //mBinding.vCalibrationView.setIsTouch(false);
        _cameraBridgeViewBase.setIsTouch(false);
        if (isShowCalLayout) {
            mBinding.vLayoutCal.setVisibility(View.GONE);
            mBinding.vLayoutBottom.setVisibility(View.VISIBLE);
            mBinding.vBtnCal.setText("CAL");
            isShowCalLayout = false;
            _cameraBridgeViewBase.setCalStatus(0);
        }
    }

    private void isShowCalLayout ( boolean isShowCalLayout){
        mBinding.vLayoutBottom.setVisibility(isShowCalLayout ? View.GONE : View.VISIBLE);
        mBinding.vLayoutCal.setVisibility(isShowCalLayout ? View.VISIBLE : View.GONE);
        //选择框
//        mBinding.vCalibrationView.setVisibility(isShowCalLayout ? View.VISIBLE : View.INVISIBLE);
        //mBinding.vCalibrationView.setIsTouch(isShowCalLayout);
        _cameraBridgeViewBase.setIsTouch(isShowCalLayout);
        mBinding.vBtnCal.setText(isShowCalLayout ? "ⓧ" : "CAL");
        if (isShowCalLayout) {
            _cameraBridgeViewBase.setCalStatus(1);
        } else {
            _cameraBridgeViewBase.setCalStatus(0);
        }
        if (isShowSetLayout) {
            mBinding.vLayoutSet.setVisibility(View.GONE);
            mBinding.vBtnSet.setText("SET");
            isShowSetLayout = false;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        disableCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_4_1_0, this, _baseLoaderCallback);
            _baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            _baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onDestroy() {
        super.onDestroy();
        disableCamera();
    }

    @Override
    public OnKeyEventReceiveListener setOnKeyEventReceiveListener() {
        return mOnKeyEventReceiveListener;
    }

    //这是串口的回调  在这里切换右上角图标
    private OnKeyEventReceiveListener mOnKeyEventReceiveListener = new OnKeyEventReceiveListener() {
        //封装回调
        //保存当前的亮度/模式 值
        //SpHelper.getInstance().putInt(AppInfo.CURRENT_BRIGHTNESS_LEVEL, current***);
        @Override
        // key: 1:手动, 2:自动 3.回中 4.回中结束
        // TODO: 在手动/自动区域显示上述状态
        public void onDialShortDown(int key) {
//            super.onDialShortDown(key);
            switch (key) {
                case 1:
                    mBinding.vTopBtn2.setImageResource(R.drawable.bottom_btn_3_normal);
                    break;
                case 2:
                    mBinding.vTopBtn2.setImageResource(R.drawable.bottom_btn_4_normal);
                    break;
                case 3:
                    mBinding.vTopBtn2.setImageResource(R.drawable.bottom_btn_5_press);
                    break;
                case 4:
                    mBinding.vTopBtn2.setImageResource(R.drawable.bottom_btn_5_normal);
                    break;

            }

        }
    };

    public void disableCamera() {
        if (_cameraBridgeViewBase != null)
            _cameraBridgeViewBase.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    //////////////////////////////////////
    // valid interface start here.
    //////////////////////////////////////
    // Camera Frame rendering and CV algorithm interface.
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame, int devi) {
        index++;
        Log.i(TAG, "Frame index is:" + index + "deviation is:" + devi + "...time is:" + System.currentTimeMillis());

        //////////////////////////////////////////////////
        // send frame to CV algorithm
        //Mat matGray = inputFrame.gray();
        //shift = linedetection(matGray.getNativeObjAddr(), 400);
        //Log.i(TAG, "XXXFrame index is:" + shift);

        // TODO: 1. show shift value and red_box on UI
        //判断当前没有点击 CAL按钮 以及 devi不等于1000
//        if (!isShowCalLayout && devi != 1000) {
        //现在的逻辑是始终显示devi的值
        if (!isShowCalLayout && devi != 1000) {
            if ((this.devi / 3) != (devi / 3)) {
                _cameraBridgeViewBase.translationBox(devi);
            }
            //if ((this.devi / 3) != (devi / 3)) {
                /*mBinding.vCalibrationView.post(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.vCalibrationView.translationBox(devi);
                    }
                });*/
            //}
        }
        if(this.devi/3 != devi/3){
            this.devi = devi;
            //mHandler.sendEmptyMessage(2);
            if (devi != 1000) {
                mBinding.vTargetInfo.setTextColor(Color.WHITE);
                int act_devi;
                act_devi = (devi-320)/3;
                if (act_devi <= -100) {
                    act_devi = -100;
                } else if (act_devi >=100) {
                    act_devi = 100;
                }
                mBinding.vTargetInfo.setText(act_devi + "");
            } else {
                mBinding.vTargetInfo.setTextColor(Color.RED);
                mBinding.vTargetInfo.setText("NO TARGET");
            }
        }
        /*mBinding.vTargetInfo.post(new Runnable() {
            @Override
            public void run() {

            }
        });*/

        // Show Frame on target area.
        Mat matOrigin = inputFrame.rgba();
        //show++;
        //if (show >= 10) {
        //   show = 0;
        return matOrigin;
        //}
        //return null;
    }

    int logCount = 0;
    long firstTimeMillis = System.currentTimeMillis();

    public void onTargetDeviation(int devi) {
        if (System.currentTimeMillis() - firstTimeMillis >= 1000) {
            Log.d(TAG, "aaOnTargetDeviation: count = " + logCount);
            firstTimeMillis = System.currentTimeMillis();
            logCount = 0;
        } else {
            logCount++;
        }
        Log.i(TAG, "onTargetDeviation value is:" + devi + "...time is:" + System.currentTimeMillis());
        // send shift value to controller
        if (!isShowCalLayout) {
            SerialPortTerminal.getInstance().writeDeviation(devi);
        }
        //byte a = (byte)(devi/256);
        //byte b = (byte)(devi%256);
        //Log.i(TAG, "Shift value to SERIAL port is:" + a + "b is:" + b);
    }

    // start detection of line
    // input Frame data, cal type(returned by line_calibration
    public native int linedetection(long matAddrGray, int cal_type);

    // give calibration area information to native algorithms.
    public native void setvalidpos(float x, float y, float w, float h);

}
