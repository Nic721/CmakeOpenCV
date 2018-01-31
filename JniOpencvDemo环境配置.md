# JniOpencv环境配置
## 一、准备开发包
将OpenCV-android-sdk->sdk->native->libs->armeabi拷贝到项目main->jniLibs中
## 二、准备库
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
                       ../../../../src/main/jniLibs/armeabi/libopencv_java3.so)
                       
# 链接opencv_java3
target_link_libraries( # Specifies the target library.
                       native-lib opencv_java3

                       # Links the target library to the 
                       log library
                       # included in the NDK.
                       ${log-lib} )
                       
                       
```

## 三、配置CPU平台架构类型
在android节点的defaultconfig下添加,注意根据实际情况选取：

```
externalNativeBuild {
    cmake {
        ...
        // 配置
        abiFilters 'x86', 'x86_64', 'armeabi', 'armeabi-v7a', 'arm64-v8a', 'mips', 'mips64'
    }
}
```
