package org.opencv.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import org.opencv.BuildConfig;
import org.opencv.R;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.utils.helper.SpHelper;

import java.util.List;

/**
 * This is a basic class, implementing the interaction with Camera and OpenCV library.
 * The main responsibility of it - is to control when camera can be enabled, process the frame,
 * call external listener to make any adjustments to the frame and then draw the resulting
 * frame to the screen.
 * The clients shall implement CvCameraViewListener.
 */
public abstract class CameraBridgeViewBase extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraBridge";
    private static final int MAX_UNSPECIFIED = -1;
    private static final int STOPPED = 0;
    private static final int STARTED = 1;

    private int mState = STOPPED;
    private Bitmap mCacheBitmap;
    private CvCameraViewListener2 mListener;
    private boolean mSurfaceExist;
    private final Object mSyncObject = new Object();

    protected int mFrameWidth;
    protected int mFrameHeight;
    protected int mMaxHeight;
    protected int mMaxWidth;
    protected float mScale = 0;
    protected int mPreviewFormat = RGBA;
    protected int mCameraIndex = CAMERA_ID_ANY;
    protected boolean mEnabled;
    protected FpsMeter mFpsMeter = null;

    public static final int CAMERA_ID_ANY = -1;
    public static final int CAMERA_ID_BACK = 99;
    public static final int CAMERA_ID_FRONT = 98;
    public static final int RGBA = 1;
    public static final int GRAY = 2;

    public static final int CALIBRATION_TYPE_EDGE = 0;
    public static final int CALIBRATION_TYPE_LINE = 1;

    public int mCalType = CALIBRATION_TYPE_EDGE; //line tracking: (0: edge tracking; 1: line tracking)
    public int mStartWork = 0;
    public int mROIx = 120;
    public int mROIx_s = 120;
    public int mROIy = 85;
    public int mROIw = 120;
    public int mROIh = 30;
    public int mStartAlgDetect = 0;

    public int mLeftMean = 0;
    public int mRightMean = 0;
    public int mRLeftMean = 0;
    public int mRRightMean = 0;
    public int mIsCalibrated = 0;
    public int mMeanDiff = 0;
    public int mMeanDelta = 0;
    public int mRMeanDiff = 0;
    public int mRMeanDelta = 0;
    public int mRightLeftMeanDiff = 0;


    //============================== Calibration view start ==============================
    //存储的框的位置 规则：字段用','连接  0.left 1.top 2.width 3.height
    String MARK_POINT = "mark_point";
    String CAL_VALUE = "cal_value";
    private int rootWidth = 640;
    private int rootHeight = 200;


    private float viewWidth;
    private float viewHeight;
    private RectF rect;
    private float aspectRatio;
    private int borderColor;
    private int lineColor;
    //是否绘制中间的分割线
    private static final boolean isShowLine = true;

    //选择框 最大和最小绘制限制
    private float minWidth = 120;
    private float minHeight = 30;
    private float maxWidth = 160;
    private float maxHeight = 40;


    //默认框的宽度 和 长宽比例
    private static final float DEFAULT_VIEW_WIDTH = 120;
    private static final float DEFAULT_ASPECT_RATIO = 4;
    //默认框的绘制位置
    private static final float DEFAULT_VIEW_LEFT = 260;
    private static final float DEFAULT_VIEW_RIGHT = 380;
    private static final float DEFAULT_VIEW_TOP = 85;
    private static final float DEFAULT_VIEW_BOTTOM = 115;
    //默认框的颜色
    private static final int DEFAULT_BORDER_COLOR = Color.RED;
    //默认框中线的颜色
    private static final int DEFAULT_Line_COLOR = Color.GREEN;
    protected Paint paint = new Paint();

    //是否是手动设置/第一次设置
    private int setRoiType = 0;

    private boolean isTouch;

    public void initCalibrationView() {
        aspectRatio = DEFAULT_ASPECT_RATIO;
        maxHeight = maxWidth / aspectRatio;
        borderColor = DEFAULT_BORDER_COLOR;
        lineColor = DEFAULT_Line_COLOR;
        rect = new RectF();

        String cacheParam = SpHelper.getInstance().getString(MARK_POINT, "");
        if (cacheParam != null && !cacheParam.equals("")) {
            String[] cacheParamArr = cacheParam.split(",");
            viewWidth = Float.parseFloat(cacheParamArr[2]);
            viewHeight = Float.parseFloat(cacheParamArr[3]);
            Log.d(TAG, "initCalibrationView: 有缓存 viewWidth = " + viewWidth + ",viewHeight = " + viewHeight);

            rect.left = Float.parseFloat(cacheParamArr[0]);
            rect.top = Float.parseFloat(cacheParamArr[1]);
            rect.right = rect.left + viewWidth;
            rect.bottom = rect.top + viewHeight;
            Log.d(TAG, "initCalibrationView: 有缓存 框的位置：left = " + rect.left + ",top = " + rect.top + ",right = " + viewWidth + ",bottom = " + viewHeight);
        } else {
            viewWidth = DEFAULT_VIEW_WIDTH;
            viewHeight = viewWidth / aspectRatio;
            Log.d(TAG, "initCalibrationView: 无缓存 viewWidth=" + viewWidth + ",viewHeight=" + viewHeight);

            rect.left = rootWidth / 2 - viewWidth / 2;
            rect.top = DEFAULT_VIEW_TOP;//getMeasuredHeight() / 2 - viewHeight / 2;
            rect.right = rect.left + viewWidth;
            rect.bottom = rect.top + viewHeight;
            Log.d(TAG, "initCalibrationView: 无缓存 框的位置：left = " + rect.left + ",top = " + rect.top + ",right = " + viewWidth + ",bottom = " + viewHeight);
        }

        String cacheParam1 = SpHelper.getInstance().getString(CAL_VALUE, "");
        if (cacheParam1 != null && !cacheParam1.equals("")) {
            String[] cacheParamArr = cacheParam1.split(",");
            mLeftMean = Integer.parseInt(cacheParamArr[1]);
            mRightMean = Integer.parseInt(cacheParamArr[2]);
            mRLeftMean = Integer.parseInt(cacheParamArr[3]);
            mRRightMean = Integer.parseInt(cacheParamArr[4]);
            mRightLeftMeanDiff = Integer.parseInt(cacheParamArr[5]);

            mIsCalibrated = Integer.parseInt(cacheParamArr[0]);
            Log.d(TAG, "initCalibrationValue: mLeftMean = " + mLeftMean + ",mRightMean = " + mRightMean +
                    "mIsCalibrated=" + mIsCalibrated);
            if (mLeftMean >= mRightMean) {
                mMeanDiff = 0;
                mMeanDelta = mLeftMean - mRightMean;
            } else {
                mMeanDiff = 1;
                mMeanDelta = mRightMean - mLeftMean;
            }

            if (mRLeftMean >= mRRightMean) {
                mRMeanDiff = 0;
                mRMeanDelta = mRLeftMean - mRRightMean;
            } else {
                mRMeanDiff = 1;
                mRMeanDelta = mRRightMean - mRLeftMean;
            }
        }
    }

    public void saveCalValue(int lmean, int rlmean, int rmean, int rrmean, int lrdiff) {
        mLeftMean = lmean;
        mRightMean = rmean;
        mRLeftMean = rlmean;
        mRRightMean = rrmean;
        mIsCalibrated = 1;

        if (mLeftMean >= mRightMean) {
            mMeanDiff = 0;
            mMeanDelta = mLeftMean - mRightMean;
        } else {
            mMeanDiff = 1;
            mMeanDelta = mRightMean - mLeftMean;
        }

        if (mRLeftMean > mRRightMean) {
            mRMeanDiff = 0;
            mRMeanDelta = mRLeftMean - mRRightMean;
        } else {
            mRMeanDiff = 1;
            mRMeanDelta = mRRightMean - mRLeftMean;
        }
        mRightLeftMeanDiff = lrdiff;

        String markPoint = mIsCalibrated + "," + mLeftMean + "," + mRightMean + ","
                + mRLeftMean + "," + mRRightMean + "," + mRightLeftMeanDiff;
        SpHelper.getInstance().putString(CAL_VALUE, markPoint);
        Log.d(TAG, "saveCalValue: mLeftMean = " + mLeftMean + ",mRightMean = " + mRightMean +
                ",mIsCalibrated = " + mIsCalibrated);
    }


    protected void onDrawCheckBox(Canvas canvas) {
        //TODO 比较last绘制的位置 left top width height 如何一样 则不绘制 否则绘制 然后更新last参数
        paint.setColor(borderColor);
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(rect, paint);

        if (isShowLine) {
            paint.setStrokeWidth(1.0f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(lineColor);
            canvas.drawLine(rect.centerX(), 0, rect.centerX(), getHeight(), paint);
        }
        setROI((int) rect.left, (int) rect.top, (int) rect.width(), (int) rect.height(), setRoiType);
        setRoiType = 0;

    }

    float last_x = -1;
    float last_y = -1;
    float baseValue;
    public long scaleTime;

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTouch)
            return false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            baseValue = 0;
            float x = last_x = event.getRawX();
            float y = last_y = event.getRawY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getPointerCount() == 2) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
                if (baseValue == 0) {
                    baseValue = value;
                } else {
                    if (value - baseValue >= 10 || value - baseValue <= -10) {
                        float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                        setRoiType = 1;
                        //Log.d(TAG, "setROItype is 1");
                        scaleRect(scale);
                        scaleTime = System.currentTimeMillis();
                    }
                }
                //bug  两指缩放抬起后 会导致一次 滑动。 利用时间间隔做的判断
            } else if (event.getPointerCount() == 1 && System.currentTimeMillis() - scaleTime > 300) {
                float x = event.getRawX();
                float y = event.getRawY();
                x -= last_x;
                y -= last_y;
                if (x >= 10 || y >= 10 || x <= -10 || y <= -10) {
                    setRoiType = 1;
                    //Log.d(TAG, "setROItype is 1");
                    center(x, y);
                }
                last_x = event.getRawX();
                last_y = event.getRawY();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //setRoiType = 1;
            //center(0, 0);
        }
        return true;
    }


    /**
     * 缩放后 更新框 且移动0,0
     *
     * @param scale
     */
    private void scaleRect(float scale) {
        if (scale > 1) {
            viewWidth += 4;
            viewHeight += 1;
        } else {
            viewWidth -= 4;
            viewHeight -= 1;
        }

        if (viewWidth > maxWidth) {
            viewWidth = maxWidth;
            viewHeight = maxHeight;
        } else if (viewWidth < minWidth) {
            viewWidth = minWidth;
            viewHeight = minHeight;
        }

        rect.left = rect.centerX() - viewWidth / 2;
        rect.top = rect.centerY() - viewHeight / 2;
        rect.right = rect.left + viewWidth;
        rect.bottom = rect.top + viewHeight;
        center(0, 0);
    }

    /**
     * 触摸点为中心->>移动
     *
     * @param dx
     * @param dy
     */
    private void center(float dx, float dy) {
        rect.left = rect.left + dx;
        rect.top = rect.top + dy;
        rect.right = rect.right + dx;
        rect.bottom = rect.bottom + dy;
        if (rect.left < 0) {
            rect.left = 0;
            rect.right = rect.left + viewWidth;
        }
        if (rect.right > rootWidth) {
            rect.right = rootWidth;
            rect.left = rect.right - viewWidth;
        }
        if (rect.top < 0) {
            rect.top = 0;
            rect.bottom = rect.top + viewHeight;
        }
        if (rect.bottom > rootHeight) {
            rect.bottom = rootHeight;
            rect.top = rect.bottom - viewHeight;
        }
        String markPoint = rect.left + "," + rect.top + "," + viewWidth + "," + viewHeight;
        Log.d(TAG, "center: 存储拖动过的 值：" + markPoint);
        SpHelper.getInstance().putString(MARK_POINT, markPoint);
    }


    /**
     * 平移
     */
    public void translationBox(int devi) {
        //存储规则 "left,top,width,height"
        String cacheParam = SpHelper.getInstance().getString(MARK_POINT, "");


        if (cacheParam != null && !cacheParam.equals("")) {
            String[] cacheParamArr = cacheParam.split(",");
            rect.left = Float.parseFloat(cacheParamArr[0]);
            rect.right = rect.left + Float.parseFloat(cacheParamArr[2]);
        } else {
            rect.left = DEFAULT_VIEW_LEFT;
            rect.right = DEFAULT_VIEW_RIGHT;
            rect.top = DEFAULT_VIEW_TOP;
            rect.bottom = DEFAULT_VIEW_BOTTOM;
        }

        float width = rect.right - rect.left;
        rect.left = devi - width / 2;
        rect.right = rect.left + width;
        if (rect.left < 0) {
            rect.left = 0;
            rect.right = rect.left + viewWidth;
        }
        if (rect.right > rootWidth) {
            rect.right = rootWidth;
            rect.left = rect.right - viewWidth;
        }
        if (rect.top < 0) {
            rect.top = 0;
            rect.bottom = rect.top + viewHeight;
        }
        if (rect.bottom > rootHeight) {
            rect.bottom = rootHeight;
            rect.top = rect.bottom - viewHeight;
        }
    }

    /**
     * 设置是否 cal 可以滑动  或 缩放
     * @param isTouch
     */
    public void setIsTouch(boolean isTouch) {
        this.isTouch = isTouch;
    }

    //============================== Calibration view end ==============================


    public CameraBridgeViewBase(Context context, int cameraId) {
        super(context);
        mCameraIndex = cameraId;
        getHolder().addCallback(this);
        mMaxWidth = MAX_UNSPECIFIED;
        mMaxHeight = MAX_UNSPECIFIED;
        initCalibrationView();
    }

    public CameraBridgeViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);

        int count = attrs.getAttributeCount();
        Log.d(TAG, "Attr count: " + Integer.valueOf(count));

        TypedArray styledAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.CameraBridgeViewBase);
        if (styledAttrs.getBoolean(R.styleable.CameraBridgeViewBase_show_fps, false))
            enableFpsMeter();

        mCameraIndex = styledAttrs.getInt(R.styleable.CameraBridgeViewBase_camera_id, -1);

        getHolder().addCallback(this);
        mMaxWidth = MAX_UNSPECIFIED;
        mMaxHeight = MAX_UNSPECIFIED;
        styledAttrs.recycle();
        initCalibrationView();
    }

    public void setCalibrationType(int type) {
        mCalType = type;
    }

    public void setCalStatus(int status) {
        if (status == 1) {
            mStartAlgDetect = 1;
        } else {
            mStartAlgDetect = 0;
        }
    }

    public void setROI(int x, int y, int width, int height, int type) {
        if (mStartWork == 0 || type == 1) {
            synchronized (mSyncObject) {
                mStartWork = 1;
                mROIx = x;
                mROIx_s = mROIx;
                mROIy = y;
                mROIw = width;
                mROIh = height;
            }
        }

        if (type == 1) {
            // start to choose algorithms automatically
            mStartAlgDetect = 1;
        }
    }


    /**
     * Sets the camera index
     *
     * @param cameraIndex new camera index
     */
    public void setCameraIndex(int cameraIndex) {
        this.mCameraIndex = cameraIndex;
    }

    public interface CvCameraViewListener {
        /**
         * This method is invoked when camera preview has started. After this method is invoked
         * the frames will start to be delivered to client via the onCameraFrame() callback.
         *
         * @param width  -  the width of the frames that will be delivered
         * @param height - the height of the frames that will be delivered
         */
        public void onCameraViewStarted(int width, int height);

        /**
         * This method is invoked when camera preview has been stopped for some reason.
         * No frames will be delivered via onCameraFrame() callback after this method is called.
         */
        public void onCameraViewStopped();

        /**
         * This method is invoked when delivery of the frame needs to be done.
         * The returned values - is a modified frame which needs to be displayed on the screen.
         * TODO: pass the parameters specifying the format of the frame (BPP, YUV or RGB and etc)
         */
        public Mat onCameraFrame(Mat inputFrame, int devi);

        public void onTargetDeviation(int devi);
    }

    public interface CvCameraViewListener2 {
        /**
         * This method is invoked when camera preview has started. After this method is invoked
         * the frames will start to be delivered to client via the onCameraFrame() callback.
         *
         * @param width  -  the width of the frames that will be delivered
         * @param height - the height of the frames that will be delivered
         */
        public void onCameraViewStarted(int width, int height);

        /**
         * This method is invoked when camera preview has been stopped for some reason.
         * No frames will be delivered via onCameraFrame() callback after this method is called.
         */
        public void onCameraViewStopped();

        /**
         * This method is invoked when delivery of the frame needs to be done.
         * The returned values - is a modified frame which needs to be displayed on the screen.
         * TODO: pass the parameters specifying the format of the frame (BPP, YUV or RGB and etc)
         */
        public Mat onCameraFrame(CvCameraViewFrame inputFrame, int devi);

        public void onTargetDeviation(int devi);
    }

    ;

    protected class CvCameraViewListenerAdapter implements CvCameraViewListener2 {
        public CvCameraViewListenerAdapter(CvCameraViewListener oldStypeListener) {
            mOldStyleListener = oldStypeListener;
        }

        public void onCameraViewStarted(int width, int height) {
            mOldStyleListener.onCameraViewStarted(width, height);
        }

        public void onCameraViewStopped() {
            mOldStyleListener.onCameraViewStopped();
        }

        public Mat onCameraFrame(CvCameraViewFrame inputFrame, int devi) {
            Mat result = null;
            switch (mPreviewFormat) {
                case RGBA:
                    result = mOldStyleListener.onCameraFrame(inputFrame.rgba(), devi);
                    break;
                case GRAY:
                    result = mOldStyleListener.onCameraFrame(inputFrame.gray(), devi);
                    break;
                default:
                    Log.e(TAG, "Invalid frame format! Only RGBA and Gray Scale are supported!");
            }
            ;

            return result;
        }

        public void onTargetDeviation(int devi) {
            mOldStyleListener.onTargetDeviation(devi);
        }

        public void setFrameFormat(int format) {
            mPreviewFormat = format;
        }

        private int mPreviewFormat = RGBA;
        private CvCameraViewListener mOldStyleListener;
    }

    ;

    /**
     * This class interface is abstract representation of single frame from camera for onCameraFrame callback
     * Attention: Do not use objects, that represents this interface out of onCameraFrame callback!
     */
    public interface CvCameraViewFrame {

        /**
         * This method returns RGBA Mat with frame
         */
        public Mat rgba();

        /**
         * This method returns single channel gray scale Mat with frame
         */
        public Mat gray();
    }

    ;

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d(TAG, "call surfaceChanged event");
        synchronized (mSyncObject) {
            if (!mSurfaceExist) {
                mSurfaceExist = true;
                checkCurrentState();
            } else {
                /** Surface changed. We need to stop camera and restart with new parameters */
                /* Pretend that old surface has been destroyed */
                mSurfaceExist = false;
                checkCurrentState();
                /* Now use new surface. Say we have it now */
                mSurfaceExist = true;
                checkCurrentState();
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        /* Do nothing. Wait until surfaceChanged delivered */
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (mSyncObject) {
            mSurfaceExist = false;
            checkCurrentState();
        }
    }

    /**
     * This method is provided for clients, so they can enable the camera connection.
     * The actual onCameraViewStarted callback will be delivered only after both this method is called and surface is available
     */
    public void enableView() {
        synchronized (mSyncObject) {
            mEnabled = true;
            checkCurrentState();
        }
    }

    /**
     * This method is provided for clients, so they can disable camera connection and stop
     * the delivery of frames even though the surface view itself is not destroyed and still stays on the scren
     */
    public void disableView() {
        synchronized (mSyncObject) {
            mEnabled = false;
            checkCurrentState();
        }
    }

    /**
     * This method enables label with fps value on the screen
     */
    public void enableFpsMeter() {
        if (mFpsMeter == null) {
            mFpsMeter = new FpsMeter();
            mFpsMeter.setResolution(mFrameWidth, mFrameHeight);
        }
    }

    public void disableFpsMeter() {
        mFpsMeter = null;
    }

    /**
     * @param listener
     */

    public void setCvCameraViewListener(CvCameraViewListener2 listener) {
        mListener = listener;
    }

    public void setCvCameraViewListener(CvCameraViewListener listener) {
        CvCameraViewListenerAdapter adapter = new CvCameraViewListenerAdapter(listener);
        adapter.setFrameFormat(mPreviewFormat);
        mListener = adapter;
    }

    /**
     * This method sets the maximum size that camera frame is allowed to be. When selecting
     * size - the biggest size which less or equal the size set will be selected.
     * As an example - we set setMaxFrameSize(200,200) and we have 176x152 and 320x240 sizes. The
     * preview frame will be selected with 176x152 size.
     * This method is useful when need to restrict the size of preview frame for some reason (for example for video recording)
     *
     * @param maxWidth  - the maximum width allowed for camera frame.
     * @param maxHeight - the maximum height allowed for camera frame
     */
    public void setMaxFrameSize(int maxWidth, int maxHeight) {
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
    }

    public void SetCaptureFormat(int format) {
        mPreviewFormat = format;
        if (mListener instanceof CvCameraViewListenerAdapter) {
            CvCameraViewListenerAdapter adapter = (CvCameraViewListenerAdapter) mListener;
            adapter.setFrameFormat(mPreviewFormat);
        }
    }

    /**
     * Called when mSyncObject lock is held
     */
    private void checkCurrentState() {
        Log.d(TAG, "call checkCurrentState");
        int targetState;

        if (mEnabled && mSurfaceExist && getVisibility() == VISIBLE) {
            targetState = STARTED;
        } else {
            targetState = STOPPED;
        }

        if (targetState != mState) {
            /* The state change detected. Need to exit the current state and enter target state */
            processExitState(mState);
            mState = targetState;
            processEnterState(mState);
        }
    }

    private void processEnterState(int state) {
        Log.d(TAG, "call processEnterState: " + state);
        switch (state) {
            case STARTED:
                onEnterStartedState();
                if (mListener != null) {
                    mListener.onCameraViewStarted(mFrameWidth, mFrameHeight);
                }
                break;
            case STOPPED:
                onEnterStoppedState();
                if (mListener != null) {
                    mListener.onCameraViewStopped();
                }
                break;
        }
        ;
    }

    private void processExitState(int state) {
        Log.d(TAG, "call processExitState: " + state);
        switch (state) {
            case STARTED:
                onExitStartedState();
                break;
            case STOPPED:
                onExitStoppedState();
                break;
        }
        ;
    }

    private void onEnterStoppedState() {
        /* nothing to do */
    }

    private void onExitStoppedState() {
        /* nothing to do */
    }

    // NOTE: The order of bitmap constructor and camera connection is important for android 4.1.x
    // Bitmap must be constructed before surface
    private void onEnterStartedState() {
        Log.d(TAG, "call onEnterStartedState");
        /* Connect camera */
        if (!connectCamera(getWidth(), getHeight())) {
//        if (!connectCamera(3200, 1000)) {
            AlertDialog ad = new AlertDialog.Builder(getContext()).create();
            ad.setCancelable(false); // This blocks the 'BACK' button
            ad.setMessage("It seems that you device does not support camera (or it is locked). Application will be closed.");
            ad.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ((Activity) getContext()).finish();
                }
            });
            ad.show();

        }
    }

    private void onExitStartedState() {
        disconnectCamera();
        if (mCacheBitmap != null) {
            mCacheBitmap.recycle();
        }
    }

    /**
     * This method shall be called by the subclasses when they have valid
     * object and want it to be delivered to external client (via callback) and
     * then displayed on the screen.
     *
     * @param frame - the current frame to be delivered
     */
    protected void deliverAndDrawFrame(CvCameraViewFrame frame, int devi) {
        Mat modified;

        if (mListener != null) {
            modified = mListener.onCameraFrame(frame, devi);
        } else {
            modified = frame.rgba();
        }

        boolean bmpValid = true;
        if (modified != null) {
            try {
                Utils.matToBitmap(modified, mCacheBitmap);
            } catch (Exception e) {
                Log.e(TAG, "Mat type: " + modified);
                Log.e(TAG, "Bitmap type: " + mCacheBitmap.getWidth() + "*" + mCacheBitmap.getHeight());
                Log.e(TAG, "Utils.matToBitmap() throws an exception: " + e.getMessage());
                bmpValid = false;
            }
        }

        if (bmpValid && mCacheBitmap != null) {
            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null) {
                canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "mStretch value: " + mScale);

                if (mScale != 0) {
                    canvas.drawBitmap(mCacheBitmap, new Rect(0, 0, mCacheBitmap.getWidth(), mCacheBitmap.getHeight()),
                            new Rect((int) ((canvas.getWidth() - mScale * mCacheBitmap.getWidth()) / 2),
                                    (int) ((canvas.getHeight() - mScale * mCacheBitmap.getHeight()) / 2),
                                    (int) ((canvas.getWidth() - mScale * mCacheBitmap.getWidth()) / 2 + mScale * mCacheBitmap.getWidth()),
                                    (int) ((canvas.getHeight() - mScale * mCacheBitmap.getHeight()) / 2 + mScale * mCacheBitmap.getHeight())), null);
                } else {
                    canvas.drawBitmap(mCacheBitmap, new Rect(0, 0, mCacheBitmap.getWidth(), mCacheBitmap.getHeight()),
                            new Rect((canvas.getWidth() - mCacheBitmap.getWidth()) / 2,
                                    (canvas.getHeight() - mCacheBitmap.getHeight()) / 2,
                                    (canvas.getWidth() - mCacheBitmap.getWidth()) / 2 + mCacheBitmap.getWidth(),
                                    (canvas.getHeight() - mCacheBitmap.getHeight()) / 2 + mCacheBitmap.getHeight()), null);
                }

                if (mFpsMeter != null) {
                    mFpsMeter.measure();
                    mFpsMeter.draw(canvas, 20, 30);
                }
                //============================== Calibration view start ==============================
                onDrawCheckBox(canvas);
                //============================== Calibration view end ==============================
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * This method is invoked shall perform concrete operation to initialize the camera.
     * CONTRACT: as a result of this method variables mFrameWidth and mFrameHeight MUST be
     * initialized with the size of the Camera frames that will be delivered to external processor.
     *
     * @param width  - the width of this SurfaceView
     * @param height - the height of this SurfaceView
     */
    protected abstract boolean connectCamera(int width, int height);

    /**
     * Disconnects and release the particular camera object being connected to this surface view.
     * Called when syncObject lock is held
     */
    protected abstract void disconnectCamera();

    // NOTE: On Android 4.1.x the function must be called before SurfaceTexture constructor!
    protected void AllocateCache() {
        mCacheBitmap = Bitmap.createBitmap(mFrameWidth, mFrameHeight, Bitmap.Config.ARGB_8888);
    }

    protected void setInternalDeviation(int devi) {
        if (mListener != null) {
            mListener.onTargetDeviation(devi);
        }
    }

    public interface ListItemAccessor {
        public int getWidth(Object obj);

        public int getHeight(Object obj);
    }

    ;

    /**
     * This helper method can be called by subclasses to select camera preview size.
     * It goes over the list of the supported preview sizes and selects the maximum one which
     * fits both values set via setMaxFrameSize() and surface frame allocated for this view
     *
     * @param supportedSizes
     * @param surfaceWidth
     * @param surfaceHeight
     * @return optimal frame size
     */
    protected Size calculateCameraFrameSize(List<?> supportedSizes, ListItemAccessor accessor, int surfaceWidth, int surfaceHeight) {
        int calcWidth = 0;
        int calcHeight = 0;

        int maxAllowedWidth = (mMaxWidth != MAX_UNSPECIFIED && mMaxWidth < surfaceWidth) ? mMaxWidth : surfaceWidth;
        int maxAllowedHeight = (mMaxHeight != MAX_UNSPECIFIED && mMaxHeight < surfaceHeight) ? mMaxHeight : surfaceHeight;

        for (Object size : supportedSizes) {
            int width = accessor.getWidth(size);
            int height = accessor.getHeight(size);

            if (width <= maxAllowedWidth && height <= maxAllowedHeight) {
                if (width >= calcWidth && height >= calcHeight) {
                    calcWidth = (int) width;
                    calcHeight = (int) height;
                }
            }
        }

        return new Size(calcWidth, calcHeight);
    }
}
