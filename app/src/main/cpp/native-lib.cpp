#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>

using namespace std;
using namespace cv;

extern "C"
{
int JNICALL Java_com_frt_autodetection_MainActivity_linedetection(JNIEnv *env, jobject instance,
                                    jlong matAddrGray, jint cal_type) {
    Mat &mGr = *(Mat *) matAddrGray;
    for (int k = 0; k < cal_type; k++) {
        int i = rand() % mGr.cols;
        int j = rand() % mGr.rows;
        mGr.at<uchar>(j, i) = 255;
    }
}

}
