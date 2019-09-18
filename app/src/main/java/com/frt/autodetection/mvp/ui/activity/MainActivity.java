package com.frt.autodetection.mvp.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.common.baselib.utils.RxToast;
import com.common.mvplib.config.LayoutConfig;
import com.frt.autodetection.R;
import com.frt.autodetection.base.BaseConfigActivity;
import com.frt.autodetection.databinding.ActivityMainBinding;
import com.frt.autodetection.mvp.iview.IMainActivityView;
import com.frt.autodetection.mvp.presenter.MainActivityPresenter;
import com.frt.autodetection.mvp.ui.widget.calibration.CalibrationView;

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
    private int show = 0;
    private int offsetValue = 0;

    private boolean isShowSetLayout;
    private boolean isShowCalLayout;
    //TODO 应该获取默认
    private int currentBrightnessLevel = 5;
    private int maxBrightnessLevel = 7;
    private int minBrightnessLevel = 1;

    @Override
    public void onClick(View v) {

    }

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
        //透明底部导航栏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
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

        _cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        _cameraBridgeViewBase.setCvCameraViewListener(this);
        _cameraBridgeViewBase.setMaxFrameSize(640, 200);
//        _cameraBridgeViewBase.setMaxFrameSize(DensityUtils.dip2px(600),DensityUtils.dip2px(200));
        _cameraBridgeViewBase.SetCaptureFormat(1);
        _baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }


    @Override
    public void afterInitView() {

    }


    @Override
    public void loadNetWork() {

    }


    @Override
    public void setListener() {
        mBinding.vCalibrationView.setOnOffsetChangeListener(new CalibrationView.OnOffsetChangeListener() {
            @Override
            public void OnOffsetChange(int value, float left, float top, float width, float height) {
                offsetValue = value;
                mBinding.vInfo.setText("中线偏移：" + value + "\n" + "框左上角（" + (int) left + "," + (int) top + ")\n框 width：" + width + "\n框 height:" + height);
               /* if (offsetValue > 0) {
                    mBinding.vLeftTv.setText("0");
                    mBinding.vRightTv.setText(offsetValue + "");
                } else if (offsetValue < 0) {
                    mBinding.vLeftTv.setText(offsetValue + "");
                    mBinding.vRightTv.setText("0");
                } else {
                    mBinding.vLeftTv.setText("0");
                    mBinding.vRightTv.setText("0");
                }*/
            }
        });

        mBinding.vBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxToast.showToast("调用函数======》》》底部按钮1");
            }
        });
        mBinding.vBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxToast.showToast("调用函数======》》》底部按钮2");
            }
        });
        mBinding.vBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxToast.showToast("调用函数======》》》底部按钮3");
            }
        });
        mBinding.vBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxToast.showToast("调用函数======》》》底部按钮4");
            }
        });
        mBinding.vBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxToast.showToast("调用函数======》》》底部按钮5");
            }
        });
        mBinding.vBtnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSetLayout = !isShowSetLayout;
                isShowSetLayout(isShowSetLayout);
            }
        });
        mBinding.vBtnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowCalLayout = !isShowCalLayout;
                isShowCalLayout(isShowCalLayout);
            }
        });
        mBinding.vBtnBrightnessMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBrightnessLevel > minBrightnessLevel) {
                    currentBrightnessLevel--;
                    mBinding.vBrightnessTv.setText(currentBrightnessLevel + "");
                } else {
                    RxToast.showToast("Already the lowest");
                }
            }
        });
        mBinding.vBtnBrightnessAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBrightnessLevel < maxBrightnessLevel) {
                    currentBrightnessLevel++;
                    mBinding.vBrightnessTv.setText(currentBrightnessLevel + "");
                } else {
                    RxToast.showToast("Already the highest");
                }
            }
        });

        mBinding.vBtnZhuibian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.vTopBtn1.setImageResource(R.drawable.icon_zhuibian_white);
                RxToast.showToast("调用函数======》》》追边模式");
            }
        });
        mBinding.vBtnZhuixian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.vTopBtn1.setImageResource(R.drawable.icon_zhuixian_white);
                RxToast.showToast("调用函数======》》》追线模式");
            }
        });
    }

    private void isShowSetLayout(boolean isShowSetLayout) {
        mBinding.vLayoutSet.setVisibility(isShowSetLayout ? View.VISIBLE : View.GONE);
        mBinding.vBtnSet.setText(isShowSetLayout ? "ⓧ" : "SET");
        mBinding.vCalibrationView.setVisibility(View.INVISIBLE);
        if (isShowCalLayout) {
            mBinding.vLayoutCal.setVisibility(View.GONE);
            mBinding.vLayoutBottom.setVisibility(View.VISIBLE);
            mBinding.vBtnCal.setText("CAL");
            isShowCalLayout = false;
        }
    }

    private void isShowCalLayout(boolean isShowCalLayout) {
        mBinding.vLayoutBottom.setVisibility(isShowCalLayout ? View.GONE : View.VISIBLE);
        mBinding.vLayoutCal.setVisibility(isShowCalLayout ? View.VISIBLE : View.GONE);
        //选择框
        mBinding.vCalibrationView.setVisibility(isShowCalLayout ? View.VISIBLE : View.INVISIBLE);
        mBinding.vBtnCal.setText(isShowCalLayout ? "ⓧ" : "CAL");
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
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        index++;
        Log.i(TAG, "Frame index is:" + index + "...time is:" + System.currentTimeMillis());

        //////////////////////////////////////////////////
        // send frame to CV algorithm
        //Mat matGray = inputFrame.gray();
        //linedetection(matGray.getNativeObjAddr(), 100);

        // Show Frame on target area.
        Mat matOrigin = inputFrame.rgba();
        //show++;
        //if (show >= 10) {
        //   show = 0;
        return matOrigin;
        //}
        //return null;
    }

    // start detection of line
    // input Frame data, cal type(returned by line_calibration
    public native int linedetection(long matAddrGray, int cal_type);
}
