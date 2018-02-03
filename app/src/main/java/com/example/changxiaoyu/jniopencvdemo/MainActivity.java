package com.example.changxiaoyu.jniopencvdemo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {
    private ImageView mImageView;
    private Button mButton;
    private Button mJniThreshold;
    private Button mJavaThreshold;
    private Bitmap mBitmap;
    private TextView mRetultTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.image_view);
        mButton = findViewById(R.id.button);
        mJniThreshold = findViewById(R.id.jni_threshold);
        mJavaThreshold = findViewById(R.id.java_Threshold);
        mRetultTime = findViewById(R.id.retult_time);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.girl);
                long time = System.currentTimeMillis();
                mImageView.setImageBitmap(CppImageProcessUtils.getBitmap(mBitmap));
                mRetultTime.setText("增强亮度耗时(ms):" + String.valueOf(System.currentTimeMillis()-time));
            }
        });
        mJniThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.girl);
                long time = System.currentTimeMillis();
                mImageView.setImageBitmap(CppImageProcessUtils.imageProcess(mBitmap));
                mRetultTime.setText("opencv c++二值化耗时(ms):" + String.valueOf(System.currentTimeMillis()-time));
            }
        });
        mJavaThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.girl);
                long time = System.currentTimeMillis();
                Mat src = new Mat();
                Mat gray = new Mat();
                Utils.bitmapToMat(mBitmap,src);
                Imgproc.cvtColor(src,gray,Imgproc.COLOR_BGRA2GRAY);
                Imgproc.adaptiveThreshold(gray, gray, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 31, 9);
                mRetultTime.setText("opencv java二值化耗时(ms):" + String.valueOf(System.currentTimeMillis()-time));
                Utils.matToBitmap(gray,mBitmap);
                mImageView.setImageBitmap(mBitmap);
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.girl);
                mImageView.setImageBitmap(mBitmap);
            }
        });
    }

}
