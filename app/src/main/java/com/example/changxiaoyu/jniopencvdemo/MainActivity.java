package com.example.changxiaoyu.jniopencvdemo;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ImageView mImageView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.image_view);
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.setImageBitmap(CppImageProcessUtils.getBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.girl)));
            }
        });
    }

}
