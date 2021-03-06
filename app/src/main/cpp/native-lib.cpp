#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>

using namespace std;
using namespace cv;

float xpos = 300;
float ypos = 80;
float width = 80;
float height = 40;

extern "C"
{
int JNICALL Java_com_frt_autodetection_mvp_ui_activity_MainActivity_linedetection(JNIEnv *env, jobject instance,
                                    jlong matAddrGray, jint cal_type) {
    Mat &mGr = *(Mat *) matAddrGray;
    static int i = -100;

    for (int k = 0; k < cal_type; k++) {
        int i = rand() % mGr.cols;
        int j = rand() % mGr.rows;
        mGr.at<uchar>(j, i) = 50;
    }

    i++;
    if (i == 100) {
        i = -99;
    }

    return i;
}

// save calibration area
void JNICALL Java_com_frt_autodetection_mvp_ui_activity_MainActivity_setvalidpos(JNIEnv *env, jobject instance,
                                                                                  jfloat x, jfloat y, jfloat w, jfloat h) {
    xpos = x;
    ypos = y;
    width = w;
    height = h;
}

}
