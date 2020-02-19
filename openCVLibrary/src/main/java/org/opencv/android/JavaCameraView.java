package org.opencv.android;

import java.util.List;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;

import org.opencv.BuildConfig;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Size;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

/**
 * This class is an implementation of the Bridge View between OpenCV and Java Camera.
 * This class relays on the functionality available in base class and only implements
 * required functions:
 * connectCamera - opens Java camera and sets the PreviewCallback to be delivered.
 * disconnectCamera - closes the camera and stops preview.
 * When frame is delivered via callback from Camera - it processed via OpenCV to be
 * converted to RGBA32 and then passed to the external callback for modifications if required.
 */
public class JavaCameraView extends CameraBridgeViewBase implements PreviewCallback {

    private static final int MAGIC_TEXTURE_ID = 10;
    private static final String TAG = "JavaCameraView";

    private byte mBuffer[];
    //private Mat[] mFrameChain;
    private StorageFrame[] mFrameChain;
    private int mChainIdx = 0;
    private Thread mThread;
    private boolean mStopThread;

    protected Camera mCamera;
    protected JavaCameraFrame[] mCameraFrame;
    private SurfaceTexture mSurfaceTexture;
    private int mPreviewFormat = ImageFormat.NV21;

    private int mFrameIdx = 0;
    private int mFrameShow = 0;

    private int mOriginWidth = 640;
    private int mDetectWidth = 240;
    private int mMaxLines = 10;
    private int[] mLines = new int[mMaxLines];

    private static final int MEAN_LEFT_SHIFT = 20;
    private static final int MEAN_RIGHT_SHIFT = 10;
    private static final int MEAN_LEFT_RIGHT_DIFF_SHIFT = 10;
    private static final int MEAN_HEIGHT = 10;
    private static final int MEAN_WIDTH = 10;
    private static final int MEAN_ROI_WIDTH = MEAN_LEFT_SHIFT;
    private static final int MEAN_DELTA_DRIFT = 20;
    private static final int MEAN_LEFT_RIGHT_DRIFT = 20;

    private static final int DETECTION_AREA_HEIGHT = 30;
    private static final int LINE_TRACKING_DIFFERENCE = 10;

    private int mExpandDetectArea = 0;
    private int mInvalidCount = 0;
    private int mLastVaildValue = 1000;
    private static final int DEVI_FILTER_COUNT_VALUE = 10;
    private static final int DEVI_FILTER_OFFSET_VALUE = 60;
    private static final int DEVI_FILTER_INVALID_VALUE = 1000;

    private int mCalValid1 = 0;
    private int mCalValid2 = 0;
    private int mCalValid3 = 0;
    private int mCalIndex = 0;

    private int mCalLeftMean1 = 0;
    private int mCalLeftMean2 = 0;
    private int mCalLeftMean3 = 0;
    private int mCalRightMean1 = 0;
    private int mCalRightMean2 = 0;
    private int mCalRightMean3 = 0;

    private int mRCalLeftMean1 = 0;
    private int mRCalLeftMean2 = 0;
    private int mRCalLeftMean3 = 0;
    private int mRCalRightMean1 = 0;
    private int mRCalRightMean2 = 0;
    private int mRCalRightMean3 = 0;


    public static class JavaCameraSizeAccessor implements ListItemAccessor {

        @Override
        public int getWidth(Object obj) {
            Camera.Size size = (Camera.Size) obj;
            return size.width;
        }

        @Override
        public int getHeight(Object obj) {
            Camera.Size size = (Camera.Size) obj;
            return size.height;
        }
    }

    public JavaCameraView(Context context, int cameraId) {
        super(context, cameraId);
    }

    public JavaCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public class StorageFrame{
        int devi = 0;

        Mat mat;

        StorageFrame() {
            mat = new Mat(mFrameHeight + (mFrameHeight/2), mFrameWidth, CvType.CV_8UC1);
        }
    }

    protected boolean initializeCamera(int width, int height) {
        Log.d(TAG, "Initialize java camera");
        boolean result = true;
        synchronized (this) {
            mCamera = null;

            if (mCameraIndex == CAMERA_ID_ANY) {
                Log.d(TAG, "Trying to open camera with old open()");
                try {
                    mCamera = Camera.open();
                }
                catch (Exception e){
                    Log.e(TAG, "Camera is not available (in use or does not exist): " + e.getLocalizedMessage());
                }

                if(mCamera == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    boolean connected = false;
                    for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                        Log.d(TAG, "Trying to open camera with new open(" + Integer.valueOf(camIdx) + ")");
                        try {
                            mCamera = Camera.open(camIdx);
                            connected = true;
                        } catch (RuntimeException e) {
                            Log.e(TAG, "Camera #" + camIdx + "failed to open: " + e.getLocalizedMessage());
                        }
                        if (connected) break;
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    int localCameraIndex = mCameraIndex;
                    if (mCameraIndex == CAMERA_ID_BACK) {
                        Log.i(TAG, "Trying to open back camera");
                        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                        for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                            Camera.getCameraInfo( camIdx, cameraInfo );
                            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                localCameraIndex = camIdx;
                                break;
                            }
                        }
                    } else if (mCameraIndex == CAMERA_ID_FRONT) {
                        Log.i(TAG, "Trying to open front camera");
                        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                        for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                            Camera.getCameraInfo( camIdx, cameraInfo );
                            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                localCameraIndex = camIdx;
                                break;
                            }
                        }
                    }
                    if (localCameraIndex == CAMERA_ID_BACK) {
                        Log.e(TAG, "Back camera not found!");
                    } else if (localCameraIndex == CAMERA_ID_FRONT) {
                        Log.e(TAG, "Front camera not found!");
                    } else {
                        Log.d(TAG, "Trying to open camera with new open(" + Integer.valueOf(localCameraIndex) + ")");
                        try {
                            mCamera = Camera.open(localCameraIndex);
                        } catch (RuntimeException e) {
                            Log.e(TAG, "Camera #" + localCameraIndex + "failed to open: " + e.getLocalizedMessage());
                        }
                    }
                }
            }

            if (mCamera == null)
                return false;

            /* Now set camera parameters */
            try {
                Camera.Parameters params = mCamera.getParameters();
                Log.d(TAG, "getSupportedPreviewSizes()");
                List<android.hardware.Camera.Size> sizes = params.getSupportedPreviewSizes();

                if (sizes != null) {
                    /* Select the size that fits surface considering maximum size allowed */
                    Size frameSize = calculateCameraFrameSize(sizes, new JavaCameraSizeAccessor(), width, height);

                    /* Image format NV21 causes issues in the Android emulators */
                    if (Build.FINGERPRINT.startsWith("generic")
                            || Build.FINGERPRINT.startsWith("unknown")
                            || Build.MODEL.contains("google_sdk")
                            || Build.MODEL.contains("Emulator")
                            || Build.MODEL.contains("Android SDK built for x86")
                            || Build.MANUFACTURER.contains("Genymotion")
                            || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                            || "google_sdk".equals(Build.PRODUCT))
                        params.setPreviewFormat(ImageFormat.YV12);  // "generic" or "android" = android emulator
                    else
                        params.setPreviewFormat(ImageFormat.NV21);

                    mPreviewFormat = params.getPreviewFormat();

                    Log.d(TAG, "Set preview size to " + Integer.valueOf((int)frameSize.width) + "x" + Integer.valueOf((int)frameSize.height));
                    params.setPreviewSize((int)frameSize.width, (int)frameSize.height);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && !android.os.Build.MODEL.equals("GT-I9100"))
                        params.setRecordingHint(true);

                    List<String> FocusModes = params.getSupportedFocusModes();
                    if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
                    //if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED))
                    {
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                        //params.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
                    }

                    List<String> WhiteBalanceModes = params.getSupportedWhiteBalance();
                    if (WhiteBalanceModes != null && WhiteBalanceModes.contains(Camera.Parameters.WHITE_BALANCE_AUTO))
                    {
                        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                    }

                    List<String> FlashModes = params.getSupportedFlashModes();
                    if (FlashModes != null && FlashModes.contains(Camera.Parameters.FLASH_MODE_OFF))
                    {
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    }

                    if (params.isAutoExposureLockSupported())
                    {
                        params.setAutoExposureLock(true);
                    }

                    if (params.isAutoWhiteBalanceLockSupported())
                    {
                        params.setAutoExposureLock(true);
                    }

                    mCamera.setParameters(params);
                    params = mCamera.getParameters();

                    mFrameWidth = params.getPreviewSize().width;
                    mFrameHeight = params.getPreviewSize().height;

                    if ((getLayoutParams().width == LayoutParams.MATCH_PARENT) && (getLayoutParams().height == LayoutParams.MATCH_PARENT))
                        mScale = Math.min(((float)height)/mFrameHeight, ((float)width)/mFrameWidth);
                    else
                        mScale = 0;

                    if (mFpsMeter != null) {
                        mFpsMeter.setResolution(mFrameWidth, mFrameHeight);
                    }

                    int size = mFrameWidth * mFrameHeight;
                    size  = size * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
                    mBuffer = new byte[size];

                    mCamera.addCallbackBuffer(mBuffer);
                    mCamera.setPreviewCallbackWithBuffer(this);

                    //mFrameChain = new Mat[2];
                    //mFrameChain[0] = new Mat(mFrameHeight + (mFrameHeight/2), mFrameWidth, CvType.CV_8UC1);
                    //mFrameChain[1] = new Mat(mFrameHeight + (mFrameHeight/2), mFrameWidth, CvType.CV_8UC1);
                    mFrameChain = new StorageFrame[2];
                    mFrameChain[0] = new StorageFrame();
                    mFrameChain[1] = new StorageFrame();
                    //mFrameChain[0].mat = new Mat;
                    //mFrameChain[1].mat = new Mat;
                    //mFrameChain[0].mat = new Mat(mFrameHeight + (mFrameHeight/2), mFrameWidth, CvType.CV_8UC1);
                    //mFrameChain[1].mat = new Mat(mFrameHeight + (mFrameHeight/2), mFrameWidth, CvType.CV_8UC1);

                    AllocateCache();

                    mCameraFrame = new JavaCameraFrame[2];
                    mCameraFrame[0] = new JavaCameraFrame(mFrameChain[0].mat, mFrameWidth, mFrameHeight);
                    mCameraFrame[1] = new JavaCameraFrame(mFrameChain[1].mat, mFrameWidth, mFrameHeight);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        mSurfaceTexture = new SurfaceTexture(MAGIC_TEXTURE_ID);
                        mCamera.setPreviewTexture(mSurfaceTexture);
                    } else
                       mCamera.setPreviewDisplay(null);

                    /* Finally we are ready to start the preview */
                    Log.d(TAG, "startPreview");
                    mCamera.startPreview();
                }
                else
                    result = false;
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }
        }

        return result;
    }

    protected void releaseCamera() {
        synchronized (this) {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);

                mCamera.release();
            }
            mCamera = null;
            if (mFrameChain != null) {
                mFrameChain[0].mat.release();
                mFrameChain[1].mat.release();
            }
            if (mCameraFrame != null) {
                mCameraFrame[0].release();
                mCameraFrame[1].release();
            }
        }
    }

    private boolean mCameraFrameReady = false;

    @Override
    protected boolean connectCamera(int width, int height) {

        /* 1. We need to instantiate camera
         * 2. We need to start thread which will be getting frames
         */
        /* First step - initialize camera connection */
        Log.d(TAG, "Connecting to camera");
        if (!initializeCamera(width, height))
            return false;

        mCameraFrameReady = false;

        /* now we can start update thread */
        Log.d(TAG, "Starting processing thread");
        mStopThread = false;
        mThread = new Thread(new CameraWorker());
        mThread.start();

        return true;
    }

    @Override
    protected void disconnectCamera() {
        /* 1. We need to stop thread which updating the frames
         * 2. Stop camera and release it
         */
        Log.d(TAG, "Disconnecting from camera");
        try {
            mStopThread = true;
            Log.d(TAG, "Notify thread");
            synchronized (this) {
                this.notify();
            }
            Log.d(TAG, "Waiting for thread");
            if (mThread != null)
                mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mThread =  null;
        }

        /* Now release camera */
        releaseCamera();

        mCameraFrameReady = false;
    }

    @Override
    public void onPreviewFrame(byte[] frame, Camera arg1) {
        mFrameIdx++;
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Preview Frame received. Frame size: " + mFrameIdx);
        synchronized (this) {
            mFrameChain[mChainIdx].mat.put(0, 0, frame);
            mCameraFrameReady = true;
            this.notify();
            int devi = calDeviation(mFrameChain[mChainIdx].mat, mCalType);
            mFrameChain[mChainIdx].devi = devi;
            // to keep the same with image show and deviation result, we must save deviation in onCameraFrame(activity)


            // send deviation to UI layer, which is used to send it through UART to controller
            setInternalDeviation(devi);
        }
        if (mCamera != null)
            mCamera.addCallbackBuffer(mBuffer);
    }

    private int calDeviation(Mat frame, int type) {
        if (type == CALIBRATION_TYPE_LINE) {
            if (mStartAlgDetect !=0) {
                //mStartAlgDetect = 0;
                // cal state 0
                if (mCalIndex == 0) {
                    int[] v = presetCal2(frame);
                    mCalValid1 = v[0];
                    mCalLeftMean1 = v[1];
                    mCalRightMean1 = v[2];
                    mRCalLeftMean1 = v[3];
                    mRCalRightMean1 = v[4];
                    if (mCalValid1 != DEVI_FILTER_INVALID_VALUE) {
                        mCalIndex = 1;
                    }
                    return DEVI_FILTER_INVALID_VALUE;
                } else if (mCalIndex == 1) {
                    // cal state 1
                    int v[] = presetCal2(frame);
                    mCalValid2 = v[0];
                    mCalLeftMean2 = v[1];
                    mCalRightMean2 = v[2];
                    mRCalLeftMean2 = v[3];
                    mRCalRightMean2 = v[4];
                    if ((mCalValid2 != DEVI_FILTER_INVALID_VALUE)
                        && (Math.abs(mCalValid2-mCalValid1) < 2)) {
                        mCalIndex = 2;
                    } else {
                        mCalIndex = 0;
                    }
                    return DEVI_FILTER_INVALID_VALUE;
                } else {
                    // cal state 2
                    int v[] = presetCal2(frame);
                    mCalValid3 = v[0];
                    mCalLeftMean3 = v[1];
                    mCalRightMean3 = v[2];
                    mRCalLeftMean3 = v[3];
                    mRCalRightMean3 = v[4];
                    if ((mCalValid3 != DEVI_FILTER_INVALID_VALUE)
                            && (Math.abs(mCalValid3-mCalValid1) < 2)
                            && (Math.abs(mCalValid3-mCalValid2) < 2)) {
                        mStartAlgDetect = 0;
                        mCalIndex = 0;

                        int mean_l = (mCalLeftMean1+mCalLeftMean2+mCalLeftMean3)/3;
                        int mean_r = (mCalRightMean1+mCalRightMean2+mCalRightMean3)/3;
                        int mean_rl = (mRCalLeftMean1+mRCalLeftMean2+mRCalLeftMean3)/3;
                        int mean_rr = (mRCalRightMean1+mRCalRightMean2+mRCalRightMean3)/3;
                        saveCalValue(mean_l, mean_rl, mean_r, mean_rr, v[5]);
                        int mean = (mCalValid1+mCalValid2+mCalValid3)/3;
                        return mean;
                    } else {
                        mCalValid1 = mCalValid3;
                        mCalLeftMean1 = v[1];
                        mCalRightMean1 = v[2];
                        mRCalLeftMean1 = v[3];
                        mRCalRightMean1 = v[4];
                        mCalIndex = 1;
                        return DEVI_FILTER_INVALID_VALUE;
                    }
                }
            } else {
                return calDeviation_adap2(frame);
            }
        } else {
            if (mStartAlgDetect !=0) {
                //mStartAlgDetect = 0;
                // cal state 0
                if (mCalIndex == 0) {
                    int[] v = presetCal(frame);
                    mCalValid1 = v[0];
                    mCalLeftMean1 = v[1];
                    mCalRightMean1 = v[2];
                    Log.d(TAG, "PRESET CAL1 status 0:" + mCalValid1 + "lm is:" + mCalLeftMean1 + "rm is:" + mCalRightMean1);
                    if (mCalValid1 != DEVI_FILTER_INVALID_VALUE) {
                        mCalIndex = 1;
                    }
                    return DEVI_FILTER_INVALID_VALUE;
                } else if (mCalIndex == 1) {
                    // cal state 1
                    int v[] = presetCal(frame);
                    mCalValid2 = v[0];
                    mCalLeftMean2 = v[1];
                    mCalRightMean2 = v[2];
                    Log.d(TAG, "PRESET CAL1 status 1 is:" + mCalValid2 + "lm is:" + mCalLeftMean2 + "rm is:" + mCalRightMean2);
                    if ((mCalValid2 != DEVI_FILTER_INVALID_VALUE)
                            && (Math.abs(mCalValid2-mCalValid1) < 2)) {
                        mCalIndex = 2;
                    } else {
                        mCalIndex = 0;
                    }
                    return DEVI_FILTER_INVALID_VALUE;
                } else {
                    // cal state 2
                    int v[] = presetCal(frame);
                    mCalValid3 = v[0];
                    mCalLeftMean3 = v[1];
                    mCalRightMean3 = v[2];
                    Log.d(TAG, "PRESET CAL1 status 2 is:" + mCalValid3 + "lm is:" + mCalLeftMean3 + "rm is:" + mCalRightMean3);
                    if ((mCalValid3 != DEVI_FILTER_INVALID_VALUE)
                            && (Math.abs(mCalValid3-mCalValid1) < 2)
                            && (Math.abs(mCalValid3-mCalValid2) < 2)) {
                        mStartAlgDetect = 0;
                        mCalIndex = 0;

                        int mean_l = (mCalLeftMean1+mCalLeftMean2+mCalLeftMean3)/3;
                        int mean_r = (mCalRightMean1+mCalRightMean2+mCalRightMean3)/3;
                        saveCalValue(mean_l, 0, mean_r, 0, 0);
                        int mean = (mCalValid1+mCalValid2+mCalValid3)/3;
                        Log.d(TAG, "PRESET CAL1 status 2 sub 1");
                        return mean;
                    } else {
                        mCalValid1 = mCalValid3;
                        mCalLeftMean1 = v[1];
                        mCalRightMean1 = v[2];
                        mCalIndex = 1;
                        Log.d(TAG, "PRESET CAL1 status 2 sub 2");
                        return DEVI_FILTER_INVALID_VALUE;
                    }
                }
            } else {
                return calDeviation_adap1(frame);
            }
        }
    }

    private int alignRoi(int x, int w) {
        int width;

        if ((mOriginWidth - x) <= mDetectWidth) {
            width = w;
        } else if ((w + x) < mDetectWidth) {
            width = w;
        } else {
            width = mDetectWidth;
        }
        return width;
    }

    private void detectLinesHough(Mat frame) {
        int index = 0;

        //Mat sobelImg = frame.clone();
        //Imgproc.Sobel(frame,sobelImg,-1,1,0,1,1,0, Core.BORDER_DEFAULT);
        Mat storage = new Mat();
        Imgproc.HoughLinesP(frame, storage, 1,  Math.PI, DETECTION_AREA_HEIGHT-2,
                DETECTION_AREA_HEIGHT-2,1);
        for (int x = 0; x < mMaxLines; x++) {
            mLines[x] = 0;
        }
        for (int x = 0; x < storage.rows(); x++)
        {
            // get first mMaxLines
            double[] vec = storage.get(x, 0);
            double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];
            mLines[index++] = (int)x1;
            Log.d(TAG, "detected lines at:" + mLines[index-1] + "frame width:" + frame.width());

            if (index >= mMaxLines) {
                break;
            }
        //Point start = new Point(x1, y1);
        //Point end = new Point(x2, y2);
        //Imgproc.line(frame, start, end, new Scalar(255, 255, 255, 255), 1, Imgproc.LINE_4, 0);
        }

        //sobelImg.release();
        storage.release();
    }

    private void detectLines(Mat frame) {
        int index = 0;
        int w = frame.width();
        int h = frame.height();
        int weight[] = new int[w];

        for (int i =0; i<mMaxLines; i++) {
            mLines[i] = 0;
        }

        for (int i = 0; i< w; i++) {
            int acc = 0;
            for (int j = 0; j < h; j++) {
                double xyz[];
                xyz = frame.get(j,i);
                //Log.e(TAG, "get xyz value" + (int) (xyz[0]) + "width is" + i + "height is" + j);

                if ((int)(xyz[0]) > 0) {
                    acc++;
                }
            }
            weight[i] = acc;
        }

        // only get first 10 lines
        for (int i=0; i<w; i++) {
            if (weight[i] >= (h-2)) {
                if (index < mMaxLines) {
                    mLines[index++] = i;
                }
            }
        }

        return;
    }

    private int[] getMeans(Mat frame, int line) {
        int isvalid = 0;
        int means[] = new int[5];
        means[0] = isvalid;

        //mLeftMean = 0;
        //mRightMean = 0;
        Log.d(TAG, "getmeans line value:" + line + "frame width is:" + frame.width() + "height is:" + frame.height());

        if ((line < MEAN_ROI_WIDTH) || (line > (frame.width() - MEAN_ROI_WIDTH))) {
            return means;
        }

        // get mean value of left area
        //Rect rect = new Rect(line-MEAN_LEFT_SHIFT, mROIy, MEAN_WIDTH, MEAN_HEIGHT);
        Rect rect = new Rect(line-MEAN_LEFT_SHIFT, 0, MEAN_WIDTH, MEAN_HEIGHT);
        Mat roi = new Mat(frame, rect);
        Mat gray  = roi.submat(0,MEAN_HEIGHT,0,MEAN_WIDTH);

        MatOfDouble l_mean = new MatOfDouble();
        MatOfDouble l_stddev = new MatOfDouble();
        Core.meanStdDev(gray, l_mean, l_stddev);
        means[1] = (int)l_mean.get(0,0)[0];
        means[2] = (int)l_stddev.get(0,0)[0];

        //Log.d(TAG, "getmeans valid left:" + (int)l_mean.get(1,0)[0] + "left mean:" + (int)l_mean.get(0,1)[0]);

        // get mean value of right area
        Rect rect1 = new Rect(line+MEAN_RIGHT_SHIFT, 0, MEAN_WIDTH, MEAN_HEIGHT);
        Mat roi1 = new Mat(frame, rect1);
        Mat gray1  = roi1.submat(0,MEAN_HEIGHT,0,MEAN_WIDTH);

        MatOfDouble r_mean = new MatOfDouble();
        MatOfDouble r_stddev = new MatOfDouble();
        Core.meanStdDev(gray1, r_mean, r_stddev);
        means[3] = (int)r_mean.get(0,0)[0];
        means[4] = (int)r_stddev.get(0,0)[0];

        //Log.d(TAG, "getmeans valid right:" + (int)r_mean.get(1,1)[0] + "left mean:" + (int)r_mean.get(2,2)[0]);

        isvalid = 1;

        means[0] = isvalid;
        Log.d(TAG, "getmeans valid:" + means[0] + "left mean:" + means[1] + "right mean:" + means[3]);

        l_mean.release();
        l_stddev.release();
        r_mean.release();
        r_stddev.release();
        roi.release();
        gray.release();
        roi1.release();
        gray1.release();

        return means;
    }

    private int[] getMeansPRI(Mat frame, int line) {
        int isvalid = 0;
        int total = 0;
        int means[] = new int[3];
        means[0] = isvalid;

        //mLeftMean = 0;
        //mRightMean = 0;
        Log.d(TAG, "getmeans line value:" + line + "frame width is:" + frame.width() + "height is:" + frame.height());

        if ((line < MEAN_ROI_WIDTH) || (line > (frame.width() - MEAN_ROI_WIDTH))) {
            return means;
        }

        // get mean value of left area
        //Rect rect = new Rect(line-MEAN_LEFT_SHIFT, mROIy, MEAN_WIDTH, MEAN_HEIGHT);
        Rect rect = new Rect(line-MEAN_LEFT_SHIFT, 0, MEAN_WIDTH, MEAN_HEIGHT);
        Mat roi = new Mat(frame, rect);
        Mat gray  = roi.submat(0,MEAN_HEIGHT,0,MEAN_WIDTH);
        for (int i=0; i<gray.width(); i++) {
            for (int j=0; j<gray.height(); j++) {
                total += (int)gray.get(j,i)[0];
            }
        }
        //mLeftMean = total/(gray.width() * gray.height());
        means[1] = total/(gray.width() * gray.height());

        // get mean value of right area
        total = 0;
        Rect rect1 = new Rect(line+MEAN_RIGHT_SHIFT, 0, MEAN_WIDTH, MEAN_HEIGHT);
        Mat roi1 = new Mat(frame, rect1);
        Mat gray1  = roi1.submat(0,MEAN_HEIGHT,0,MEAN_WIDTH);
        for (int i=0; i<gray1.width(); i++) {
            for (int j=0; j<gray1.height(); j++) {
                total += (int)gray1.get(j,i)[0];
            }
        }
        //mRightMean = total/(gray1.width() * gray1.height());
        means[2] = total/(gray1.width() * gray1.height());

        isvalid = 1;

        means[0] = isvalid;
        Log.d(TAG, "getmeans valid:" + means[0] + "left:" + means[1] + "right:" + means[2]);


        roi.release();
        gray.release();
        roi1.release();
        gray1.release();

        return means;
    }

    private int deviFilter(int devi) {
        int devi_s = 0;

        // check if continuous 'no target'
        if (devi == DEVI_FILTER_INVALID_VALUE) {
            mInvalidCount++;
            if (mInvalidCount > DEVI_FILTER_COUNT_VALUE) {
                mExpandDetectArea = 1;
            }
            devi_s = devi;

            return devi_s;
        }

        mInvalidCount = 0;
        mExpandDetectArea = 0;

        if (mLastVaildValue == DEVI_FILTER_INVALID_VALUE){
            mLastVaildValue = devi;
            devi_s = devi;
        } else {
            if (Math.abs(mLastVaildValue - devi) < DEVI_FILTER_OFFSET_VALUE) {
                mLastVaildValue = devi;
                devi_s = devi;
            } else {
                devi_s = DEVI_FILTER_INVALID_VALUE;
            }
        }

        return devi_s;
    }

    private int[] presetCal(Mat frame) {
        int devi = DEVI_FILTER_INVALID_VALUE;
        int[] value_array = new int[3];
        value_array[0] = DEVI_FILTER_INVALID_VALUE;
        value_array[1] = 0;
        value_array[2] = 0;
        //int roiwidth = mROIw;

        // 1. first step: detect lines
        // settings of ROI is not ready
        if (mStartWork == 0) {
            value_array[0] = devi;
            return value_array;
        }

        Log.d(TAG, "PRESET CAL1 ENTER");

        //roiwidth = alignRoi(mROIx_s, mROIw);

        Rect rect = new Rect(mROIx_s, mROIy, mROIw, DETECTION_AREA_HEIGHT);
        Mat roi = new Mat(frame, rect);

        Mat gray  = roi.submat(0,DETECTION_AREA_HEIGHT,0,mROIw);
        Mat target = gray.clone();
        //Imgproc.GaussianBlur(gray,target,new Size(3,3),0,0);
        Imgproc.bilateralFilter(gray,target,9,5,5);
        Mat thresholdImg = target.clone();
        Imgproc.adaptiveThreshold(target, thresholdImg,255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 3, 2);

        //detectLines(thresholdImg);
        detectLinesHough(thresholdImg);

        // 2. second step: choose the most suitable one of the detected lines
        int validLine = 0;
        for (int i = 0; i < mMaxLines; i++) {
            //Log.d(TAG, "preset CAL lines original:" + mLines[i]);
            if ((mLines[i] != 0) &&
                    (Math.abs(mLines[i] - mROIw/2) < Math.abs(validLine - mROIw/2)))  {
                // always choose the center-most one
                validLine = mLines[i];
            }
        }

        Log.d(TAG, "preset CAL lines at:" + validLine);

        // 3. third step: get mean value of two sides of detected line
        int res[] = getMeans(gray, validLine);
        int isvalid = res[0];
        if (isvalid != 0) {
            //saveCalValue(res[1], res[2], res[3], res[4]);
            value_array[1] = res[1];
            value_array[2] = res[3];
        }

        int rel_devi;
        if (isvalid != 0) {
            rel_devi = mROIx_s + validLine; // add shift value, get abstract value of shift, regarding to original frame point
            devi = rel_devi;// - mROIx - mROIw/2; // get relative shift to center of value
            value_array[0] = devi;

            // NOTES: Do not update roi position at calibration state.
            // update roi position, only update mROIx since it is the only variety of the four variables
            /*
            mROIx_s = devi-mROIw/2;
            if (mROIx_s < 0) {
                mROIx_s = 0;
            } else if (mROIx_s > (mOriginWidth - mROIw)) {
                mROIx_s = mOriginWidth - mROIw;
            }
            */
        }

        roi.release();
        gray.release();
        target.release();
        thresholdImg.release();
        return value_array;
    }

    private int[] presetCal2(Mat frame) {
        int devi = DEVI_FILTER_INVALID_VALUE;
        int[] value_array = new int[6];
        value_array[0] = DEVI_FILTER_INVALID_VALUE;
        value_array[1] = 0;
        value_array[2] = 0;
        value_array[3] = 0;
        value_array[4] = 0;
        value_array[5] = 0;
        //int roiwidth = mROIw;

        // 1. first step: detect lines
        // settings of ROI is not ready
        if (mStartWork == 0) {
            value_array[0] = devi;
            return value_array;
        }

        //roiwidth = alignRoi(mROIx_s, mROIw);

        Rect rect = new Rect(mROIx_s, mROIy, mROIw, DETECTION_AREA_HEIGHT);
        Mat roi = new Mat(frame, rect);

        Mat gray  = roi.submat(0,DETECTION_AREA_HEIGHT,0,mROIw);
        Mat target = gray.clone();
        //Imgproc.GaussianBlur(gray,target,new Size(3,3),0,0);
        Imgproc.bilateralFilter(gray,target,9,5,5);
        Mat thresholdImg = target.clone();
        Imgproc.adaptiveThreshold(target, thresholdImg,255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 3, 2);

        //detectLines(thresholdImg);
        detectLinesHough(thresholdImg);

        // 2. second step: choose the most suitable one of the detected lines
        int validLine_l = 0;
        int validLine_r = 0;
        int validLine = 0;
        for (int i = 0; i < mMaxLines; i++) {
            if (mLines[i] != 0) {
                if (mLines[i] < mROIw / 2) {
                    if ((mROIw / 2 - mLines[i]) < (mROIw / 2 - validLine_l)) {
                        // always choose the center-most two lines from two sides
                        validLine_l = mLines[i];
                    }
                } else {
                    if ((mLines[i] - mROIw / 2) < Math.abs(validLine_r - mROIw / 2)) {
                        // always choose the center-most two lines from two sides
                        validLine_r = mLines[i];
                    }
                }
            }
        }

        // 3. third step: get mean value of two sides of detected line
        int res_l[] = getMeans(gray, validLine_l);
        int res_r[] = getMeans(gray, validLine_r);
        int isvalid_l = res_l[0];
        int isvalid_r = res_r[0];
        if ((isvalid_l != 0) && (isvalid_r != 0)) {
            //saveCalValue(res[1], res[2], res[3], res[4]);
            value_array[1] = res_l[1];
            value_array[2] = res_l[3];
            value_array[3] = res_r[1];
            value_array[4] = res_r[3];
            value_array[5] = validLine_r - validLine_l;
            if ((validLine_r - validLine_l) > LINE_TRACKING_DIFFERENCE) {
                value_array[0] = (validLine_l + validLine_r) / 2;
            }
        }

        int rel_devi;
        if (value_array[0] != DEVI_FILTER_INVALID_VALUE) {
            validLine = value_array[0];
            rel_devi = mROIx_s + validLine; // add shift value, get abstract value of shift, regarding to original frame point
            devi = rel_devi;// - mROIx - mROIw/2; // get relative shift to center of value
            value_array[0] = devi;

            // NOTES: Do not update roi position at calibration state.
            // update roi position, only update mROIx since it is the only variety of the four variables
            /*
            mROIx_s = devi-mROIw/2;
            if (mROIx_s < 0) {
                mROIx_s = 0;
            } else if (mROIx_s > (mOriginWidth - mROIw)) {
                mROIx_s = mOriginWidth - mROIw;
            }
            */
        }

        roi.release();
        gray.release();
        target.release();
        thresholdImg.release();
        return value_array;
    }

    private int calDeviation_adap1(Mat frame) {
        int devi = DEVI_FILTER_INVALID_VALUE;
        int roiwidth = mROIw;
        int roistart = mROIx_s;

        // settings of ROI is not ready
        if (mStartWork == 0) {
            return devi;
        }

        Log.d(TAG, "caldeviation_adap1 enter.");

        if (mExpandDetectArea == 1) {
            roiwidth = alignRoi(roistart, mROIw);
            roistart = mROIx_s - (roiwidth - mROIw)/2;
        }
        Rect rect = new Rect(roistart, mROIy, roiwidth, DETECTION_AREA_HEIGHT);
        Mat roi = new Mat(frame, rect);

        Mat gray  = roi.submat(0,DETECTION_AREA_HEIGHT,0,roiwidth);
        Mat target = gray.clone();
        //Imgproc.GaussianBlur(gray,target,new Size(3,3),0,0);
        Imgproc.bilateralFilter(gray,target,9,5,5);
        Mat thresholdImg = target.clone();
        Imgproc.adaptiveThreshold(target, thresholdImg,255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 3, 2);
        detectLinesHough(thresholdImg);
        // detect lines
        //detectLines(thresholdImg);
        int validLine = 0;
        for (int i = 0; i < mMaxLines; i++) {
            Log.d(TAG, "caldeviation_adap1 enter getmeans");
            if (mLines[i] != 0) {
                //validLine = mLines[i];
                Log.d(TAG, "caldeviation_adap1 enter getmeans 1");
                int mean[] = getMeans(gray, mLines[i]);
                if (mean[0] != 0) {
                    int diff;
                    if (mean[1] > mean[3]) {
                        diff = 0;
                    } else {
                        diff = 1;
                    }

                    if (mIsCalibrated != 0) {
                        Log.d(TAG, "caldeviation_adap1 enter calibrated");
                        if ((Math.abs(mean[1] - mLeftMean) < MEAN_LEFT_RIGHT_DRIFT) &&
                                (Math.abs(mean[3] - mRightMean) < MEAN_LEFT_RIGHT_DRIFT) &&
                                (diff == mMeanDiff) &&
                                (Math.abs(Math.abs(mean[1] - mean[3]) - mMeanDelta) < MEAN_DELTA_DRIFT)) {
                            Log.d(TAG, "caldeviation_adap1 enter getvalidline");
                            //get valid line
                            validLine = mLines[i];
                            break;
                        }
                    }  else {
                        // not calibrated, always choose the first one
                        //validLine = mLines[i];
                        // not calibrated, return invalid value
                        Log.d(TAG, "caldeviation_adap1 enter on calibration");
                        validLine = 0;
                        break;
                    }
                }
            }
        }

        //Mat sobelImg = thresholdImg.clone();
        //Imgproc.Sobel(thresholdImg,sobelImg,-1,1,0,1,1,0, Core.BORDER_DEFAULT);
        //Mat storage = new Mat();
        //Imgproc.HoughLinesP(sobelImg, storage, 1,  Math.PI, 22, 22,1);
        //for (int x = 0; x < storage.rows(); x++)
        //{
         //   double[] vec = storage.get(x, 0);
         //   double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];
         //   devi = (int)x1;
            //Point start = new Point(x1, y1);
            //Point end = new Point(x2, y2);
            //Imgproc.line(frame, start, end, new Scalar(255, 255, 255, 255), 1, Imgproc.LINE_4, 0);
        //}

        //Imgproc.cvtColor(roi, gray, Imgproc.COLOR_RGB2GRAY);
        //threshold
        //sobel ---> x
        //get line detection
        //get position of line ---> x

        // change devi to fit camera frame position
        int rel_devi = 0;
        if (validLine != 0) {
            rel_devi = roistart + validLine; // add shift value, get abstract value of shift, regarding to original frame point
            devi = rel_devi;// - mROIx - mROIw/2; // get relative shift to center of value

            // update roi position, only update mROIx since it is the only variety of the four variables
            if (mStartAlgDetect == 0) {
                mROIx_s = devi - mROIw / 2;
                if (mROIx_s < 0) {
                    mROIx_s = 0;
                } else if (mROIx_s > (mOriginWidth - mROIw)) {
                    mROIx_s = mOriginWidth - mROIw;
                }
            }
            //Log.d(TAG, "mROI value is:" + mROIx_s + mROIx + mROIw + "devi is:" + devi);
        }

        devi = deviFilter(devi);


        roi.release();
        gray.release();
        target.release();
        thresholdImg.release();
        return devi;
    }

    private int getRightLine(int left, Mat frame) {
        int right = 0;

        for (int i = 0; i < mMaxLines; i++) {
            if (mLines[i] > left) {
                //validLine = mLines[i];
                int mean[] = getMeans(frame, mLines[i]);
                if (mean[0] != 0) {
                    int diff;
                    if (mean[1] > mean[3]) {
                        diff = 0;
                    } else {
                        diff = 1;
                    }

                    if (mIsCalibrated != 0) {
                        if ((Math.abs(mean[1] - mRLeftMean) < MEAN_LEFT_RIGHT_DRIFT) &&
                                (Math.abs(mean[3] - mRRightMean) < MEAN_LEFT_RIGHT_DRIFT) &&
                                (diff == mRMeanDiff) &&
                                (Math.abs(Math.abs(mean[1] - mean[3]) - mRMeanDelta) < MEAN_DELTA_DRIFT)) {
                            // check if left right difference is suitable
                            int delta = mLines[i] - left;
                            if (Math.abs(delta - mRightLeftMeanDiff) < MEAN_LEFT_RIGHT_DIFF_SHIFT) {
                                // get correct right line
                                right = mLines[i];
                                break;
                            }
                        }
                    } else {
                        // not calibrated, always choose the first one
                        // validLine = mLines[i];
                        // not calibrated, return invalid value
                        right = 0;
                        break;
                    }
                }
            }
        }

        return right;
    }

    private int calDeviation_adap2(Mat frame) {
        int devi = DEVI_FILTER_INVALID_VALUE;
        int roiwidth = mROIw;
        int roistart = mROIx_s;

        // settings of ROI is not ready
        if (mStartWork == 0) {
            return devi;
        }


        if (mExpandDetectArea == 1) {
            roiwidth = alignRoi(roistart, mROIw);
            roistart = mROIx_s - (roiwidth - mROIw)/2;
        }
        Rect rect = new Rect(roistart, mROIy, roiwidth, DETECTION_AREA_HEIGHT);
        Mat roi = new Mat(frame, rect);

        Mat gray  = roi.submat(0,DETECTION_AREA_HEIGHT,0,roiwidth);
        Mat target = gray.clone();
        //Imgproc.GaussianBlur(gray,target,new Size(3,3),0,0);
        Imgproc.bilateralFilter(gray,target,9,5,5);
        Mat thresholdImg = target.clone();
        Imgproc.adaptiveThreshold(target, thresholdImg,255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 3, 2);
        detectLinesHough(thresholdImg);
        // detect lines
        //detectLines(thresholdImg);
        int validLine = 0;
        int validLine_l = 0;
        int validLine_r = 0;
        for (int i = 0; i < mMaxLines; i++) {
            if (mLines[i] != 0) {
                //validLine = mLines[i];
                int mean[] = getMeans(gray, mLines[i]);
                if (mean[0] != 0) {
                    int diff;
                    if (mean[1] > mean[3]) {
                        diff = 0;
                    } else {
                        diff = 1;
                    }

                    if (mIsCalibrated != 0) {
                        if ((Math.abs(mean[1] - mLeftMean) < MEAN_LEFT_RIGHT_DRIFT) &&
                                (Math.abs(mean[3] - mRightMean) < MEAN_LEFT_RIGHT_DRIFT) &&
                                (diff == mMeanDiff) &&
                                (Math.abs(Math.abs(mean[1] - mean[3]) - mMeanDelta) < MEAN_DELTA_DRIFT)) {
                            //get valid left line
                            validLine_l = mLines[i];
                            validLine_r = getRightLine(validLine_l, gray);
                            if (validLine_r != 0) {
                                validLine = (validLine_l + validLine_r)/2;
                                break;
                            }
                        }
                    }  else {
                        // not calibrated, always choose the first one
                        // validLine = mLines[i];
                        // not calibrated, return invalid value
                        validLine = 0;
                        break;
                    }
                }
            }
        }

        //Mat sobelImg = thresholdImg.clone();
        //Imgproc.Sobel(thresholdImg,sobelImg,-1,1,0,1,1,0, Core.BORDER_DEFAULT);
        //Mat storage = new Mat();
        //Imgproc.HoughLinesP(sobelImg, storage, 1,  Math.PI, 22, 22,1);
        //for (int x = 0; x < storage.rows(); x++)
        //{
        //   double[] vec = storage.get(x, 0);
        //   double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];
        //   devi = (int)x1;
        //Point start = new Point(x1, y1);
        //Point end = new Point(x2, y2);
        //Imgproc.line(frame, start, end, new Scalar(255, 255, 255, 255), 1, Imgproc.LINE_4, 0);
        //}

        //Imgproc.cvtColor(roi, gray, Imgproc.COLOR_RGB2GRAY);
        //threshold
        //sobel ---> x
        //get line detection
        //get position of line ---> x

        // change devi to fit camera frame position
        int rel_devi = 0;
        if (validLine != 0) {
            rel_devi = roistart + validLine; // add shift value, get abstract value of shift, regarding to original frame point
            devi = rel_devi;// - mROIx - mROIw/2; // get relative shift to center of value

            // update roi position, only update mROIx since it is the only variety of the four variables
            if (mStartAlgDetect == 0) {
                mROIx_s = devi - mROIw / 2;
                if (mROIx_s < 0) {
                    mROIx_s = 0;
                } else if (mROIx_s > (mOriginWidth - mROIw)) {
                    mROIx_s = mOriginWidth - mROIw;
                }
            }
            //Log.d(TAG, "mROI value is:" + mROIx_s + mROIx + mROIw + "devi is:" + devi);
        }

        devi = deviFilter(devi);


        roi.release();
        gray.release();
        target.release();
        thresholdImg.release();
        return devi;
    }

    private class JavaCameraFrame implements CvCameraViewFrame {
        @Override
        public Mat gray() {
            return mYuvFrameData.submat(0, mHeight, 0, mWidth);
        }

        @Override
        public Mat rgba() {
            if (mPreviewFormat == ImageFormat.NV21)
                Imgproc.cvtColor(mYuvFrameData, mRgba, Imgproc.COLOR_YUV2RGBA_NV21, 4);
            else if (mPreviewFormat == ImageFormat.YV12)
                Imgproc.cvtColor(mYuvFrameData, mRgba, Imgproc.COLOR_YUV2RGB_I420, 4);  // COLOR_YUV2RGBA_YV12 produces inverted colors
            else
                throw new IllegalArgumentException("Preview Format can be NV21 or YV12");

            return mRgba;
        }

        public JavaCameraFrame(Mat Yuv420sp, int width, int height) {
            super();
            mWidth = width;
            mHeight = height;
            mYuvFrameData = Yuv420sp;
            mRgba = new Mat();
        }

        public void release() {
            mRgba.release();
        }

        private Mat mYuvFrameData;
        private Mat mRgba;
        private int mWidth;
        private int mHeight;
    };

    private class CameraWorker implements Runnable {

        @Override
        public void run() {
            do {
                boolean hasFrame = false;
                synchronized (JavaCameraView.this) {
                    try {
                        while (!mCameraFrameReady && !mStopThread) {
                            JavaCameraView.this.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mCameraFrameReady)
                    {
                        mChainIdx = 1 - mChainIdx;
                        mCameraFrameReady = false;
                        hasFrame = true;
                    }
                }

                if (!mStopThread && hasFrame) {
                    mFrameShow++;
                    if (mFrameShow >= 3) {
                        mFrameShow = 0;
                        if (!mFrameChain[1 - mChainIdx].mat.empty())
                            deliverAndDrawFrame(mCameraFrame[1 - mChainIdx],mFrameChain[1 - mChainIdx].devi);
                    }
                }
            } while (!mStopThread);
            Log.d(TAG, "Finish processing thread");
        }
    }
}
