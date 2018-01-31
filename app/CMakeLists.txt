# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

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

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

# 链接opencv_java3
target_link_libraries( # Specifies the target library.
                       native-lib opencv_java3

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )