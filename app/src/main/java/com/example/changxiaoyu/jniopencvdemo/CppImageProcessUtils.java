package com.example.changxiaoyu.jniopencvdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by changxiaoyu on 18/1/28.
 */

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

    // 定义java本地方法
    public static native void cppImageProcess(int w, int h, int[] pixArr, int ld);
    // opencv c++图片二值化
    public static native void cppImageThreshold(int w, int h, int[] pixArr);
}
