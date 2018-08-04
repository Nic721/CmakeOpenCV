# Blog
## 一、准备开发包
1. 将OpenCV-android-sdk->sdk->native->libs->armeabi拷贝到项目main->jniLibs中
2. 将OpenCV-android-sdk->sdk->native->libs->armeabi拷贝到项目libs目录中

这里使用2.，原因是方便搭建OpenCV Java开发环境（详情见下文）
## 二、修改jniLibs的目录
jniLibs的默认目录是`../../../../src/main/jniLibs`，使用方法2.后要在bulid.gradle中修改jniLibs的目录路径，使用方法1.这一步就不需要指定
```
android {
    ...
    sourceSets {
        main {
            //jni库的调用会到资源文件夹下libs里面找so文件
            jniLibs.srcDirs = ['libs']
        }
    }
}
```
## 三、准备库
将以下配置拷贝到CMakeLists.txt中,注意根据实际情况修改路径
```
# OpenCV-android-sdk路径,配置公共路径
set(pathToOpenCv /Volumes/D/material/opencv/opencv-3.2.0-android-sdk)

# 支持-std=gnu++11
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# 配置加载native依赖（引入OpenCV头文件，类似于Java的jar包），配置头文件
include_directories(${pathToOpenCv}/sdk/native/jni/include)

# 动态方式加载，配置动态库
add_library( opencv_java3
             SHARED
             IMPORTED )
# 引入libopencv_java3.so文件，配置动态库
set_target_properties( opencv_java3
                       PROPERTIES IMPORTED_LOCATION
                       ../../../../libs/armeabi/libopencv_java3.so)

# 链接opencv_java3
target_link_libraries( # Specifies the target library.
                       native-lib opencv_java3

                       # Links the target library to the
                       log library
                       # included in the NDK.
                       ${log-lib} )
```
## 四、配置CPU平台架构类型
在android节点的defaultconfig下添加,注意根据实际情况选取：
```
externalNativeBuild {
    cmake {
        ...
        // 配置
//        abiFilters 'x86', 'x86_64', 'armeabi', 'armeabi-v7a', 'arm64-v8a', 'mips', 'mips64'
        abiFilters 'armeabi'
    }
}
```
## 五、如果是子项目配置OpenCV，在主项目的bulid.grale中要根据平台添加
```
defaultConfig {
    ...
    ndk {
//        abiFilters 'x86', 'x86_64', 'armeabi', 'armeabi-v7a', 'arm64-v8a', 'mips', 'mips64'
        abiFilters "armeabi"
    }
}
```

## 一个增强图片亮度的小Demo
native
```
// C++实现
public class CppImageProcessUtils {

    static {
        System.loadLibrary("native-lib");
    }
    public static Bitmap getBitmap(Bitmap bitmap){
        // 第一步：确定图片大小
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 第二步：将Bitmap->像素数组
        int[] pixArr = new int[width*height];
        bitmap.getPixels(pixArr,0,width,0,0,width,height);
        // 第三步：调用native方法
        cppImageProcess(width,height,pixArr,60);
        // 返回一张新的图片
        Bitmap newBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        // 给我们的图片填充数据
        newBitmap.setPixels(pixArr,0,width,0,0,width,height);
        return newBitmap;
    }

    // 定义java本地方法
    public static native void cppImageProcess(int w, int h, int[] pixArr, int ld);
}
```
native-lib.so
```
#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>

// C++命名空间->类似于java包
using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_changxiaoyu_jniopencvdemo_CppImageProcessUtils_cppImageProcess(JNIEnv *env,
                                                                                jobject jobj,
                                                                                jint jw,
                                                                                jint jh,
                                                                                jintArray jPixArr,
                                                                                jint jld) {
    // 第一步：导入OpenCV头文件
    // 第二步：将Java数组->C/C++数组
    jint *cPixArr = env->GetIntArrayElements(jPixArr, JNI_FALSE);
    if (cPixArr == NULL) {
        return;
    }
    // 第三步：将C/C++图片->Opencv图片
    Mat mat_image_src(jh, jw, CV_8UC4, (unsigned char *) cPixArr);
    // 增加一个往往会忽略的一步,将4通道Mat转换为3通道Mat，才能进行图像处理
    Mat mat_image_dst;
    cvtColor(mat_image_src, mat_image_dst, CV_RGBA2BGR, 3);
    // 第四步：进行图片处理
    // 克隆一张图片
    Mat mat_image_clone = mat_image_dst.clone();
    for (int i = 0; i < jh; i++) {
        for (int j = 0; j < jw; j++) {
            // 获取颜色值->修改颜色值
            // mat_image_clone.at<Vec3b>(i,j);获取像素点值，颜色值数组
            // 颜色值->Blue
            // mat_image_clone.at<Vec3b>(i,j)[0]表示获取蓝色值,saturate_cast<uchar>(),截取uchar长度的数据
            mat_image_clone.at<Vec3b>(i, j)[0] = saturate_cast<uchar>(
                    mat_image_dst.at<Vec3b>(i, j)[0] + jld);
            // 颜色值->Red
            // mat_image_clone.at<Vec3b>(i,j)[1]表示获取蓝色值
            mat_image_clone.at<Vec3b>(i, j)[1] = saturate_cast<uchar>(
                    mat_image_dst.at<Vec3b>(i, j)[1] + jld);
            // 颜色值->Green
            // mat_image_clone.at<Vec3b>(i,j)[2]表示获取蓝色值
            mat_image_clone.at<Vec3b>(i, j)[2] = saturate_cast<uchar>(
                    mat_image_dst.at<Vec3b>(i, j)[2] + jld);
        }
    }
    // 第五步：将修改后的数据赋值给原始Mat->mat_image_src
    cvtColor(mat_image_clone, mat_image_src, CV_RGB2RGBA, 4);
    // 第六步：更新Java数组
    // 0:表示处理完成之后，将C的内存释放掉
    env->ReleaseIntArrayElements(jPixArr, cPixArr, 0);
}
```
[源码请点击](https://github.com/amoscxy/JniOpenCVDemo)
# OpenCV4Android环境配置
准备工作做好之后，首先就是要在Android Studio中创建一个Android项目，创建好之后，选择File->New->Import Module
![这里写图片描述](http://otdfinygx.bkt.clouddn.com/JniOpenCVDemo1.png)

然后选择到SDK路径下的JAVA
![这里写图片描述](http://otdfinygx.bkt.clouddn.com/JniOpenCVDemo_2.png)
导入之后，你就会看到
![这里写图片描述](http://otdfinygx.bkt.clouddn.com/JniOpenCVDemo_3.png)
就说明成功导入了，然后打开Project Structure... ， 添加依赖
![这里写图片描述](http://otdfinygx.bkt.clouddn.com/JniOpenCVDemo_4.png)
添加依赖之后，就可以在项目中引用OpenCV相关API代码了，如果你此刻运行测试apk程序，它就会提示你安装OpenCV Manager这个东西。对多数开发者来说这不算配置成功，这样自己的APP就无法独立安装，必须依赖OpenCV Manager这个apk文件才可以运行，这个时候就该放大招来解决这个问题，首先把我们把opencv-3.2.0-android-sdk/sdk/native/libs/armeabi拷贝到你创建好的项目libs目录下（JniOpencv环境配置已经拷贝过了），然后在build.gradle末尾加上如下一段脚本：
![这里写图片描述](http://otdfinygx.bkt.clouddn.com/JniOpenCVDemo_5.png)
```
dependencies {
	...
}
task nativeLibsToJar(type: Jar, description: 'create a jar archive of the native libs') {
    destinationDir file("$buildDir/-nativelibs")
    baseName 'native-libs'
    from fileTree(dir: 'libs', include: '**/*.so')
    into 'lib/'
}

tasks.withType(JavaCompile) {
    compileTask -> compileTask.dependsOn(nativeLibsToJar)
}
```

然后dependencies中还要加上这句话：
```
dependencies {
	...
	 implementation fileTree(dir: "$buildDir/native-libs", include: 'native-libs.jar')
	...
}
```
检查一下openCVLibrary320的build.gradle文件：
![这里写图片描述](http://otdfinygx.bkt.clouddn.com/JniOpenCVDemo_6.png)
与module的build.gradle中版本保持一致

此配置方式OpenCV加载必须通过静态加载方式
```
//OpenCV库静态加载并初始化
private void staticLoadCVLibraries(){
    boolean load = OpenCVLoader.initDebug();
    if(load) {
        Log.i("CV", "Open CV Libraries loaded...");
    }
}
```

如此配置之后你就再也不需要其它任何配置了，这样既避免了NDK繁琐又不用依赖OpenCV Manager第三方APP，你的APP就可以直接使用OpenCV了。
# 分别通过opencv的c++接口和java接口实现图片二值化操作
## opencv c++二值化
native
```
public static Bitmap imageProcess(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixArr = new int[width*height];
        bitmap.getPixels(pixArr,0,width,0,0,width,height);
        cppImageThreshold(width,height,pixArr);
        Bitmap newBitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
        newBitmap.setPixels(pixArr,0,width,0,0,width,height);
        return newBitmap;
    }
    // opencv c++图片二值化
    public static native void cppImageThreshold(int w, int h, int[] pixArr);
```

jni实现
```
extern "C"
JNIEXPORT void JNICALL
Java_com_example_changxiaoyu_jniopencvdemo_CppImageProcessUtils_cppImageThreshold(JNIEnv *env,
                                                                                   jclass jclass,
                                                                                   jint jw, jint jh,
                                                                                   jintArray jpixArr) {
    jint* cPixArr = env->GetIntArrayElements(jpixArr,JNI_FALSE);
    if(cPixArr == NULL){
        return;
    }
    Mat mat_image_src(jh,jw,CV_8UC4,(unsigned char*)cPixArr);
    Mat mat_image_dst;
    cvtColor(mat_image_src,mat_image_dst,CV_RGBA2GRAY,1);
    Mat mat_image_thereshold;
    cv::adaptiveThreshold(mat_image_dst,mat_image_thereshold,255,ADAPTIVE_THRESH_GAUSSIAN_C,CV_THRESH_BINARY,31,9);
    cvtColor(mat_image_thereshold,mat_image_src,CV_GRAY2BGRA,4);
    env->ReleaseIntArrayElements(jpixArr,cPixArr,0);
}
```
## opencv java二值化
```
Mat src = new Mat();
Mat gray = new Mat();
Utils.bitmapToMat(mBitmap,src);
Imgproc.cvtColor(src,gray,Imgproc.COLOR_BGRA2GRAY);
Imgproc.adaptiveThreshold(gray, gray, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 31, 9);
Utils.matToBitmap(gray,mBitmap);
```
[点击查看博客](https://blog.csdn.net/amoscxy/article/details/79215757)

