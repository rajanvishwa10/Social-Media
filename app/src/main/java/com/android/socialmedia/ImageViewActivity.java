package com.android.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this).load(getIntent().getStringExtra("url")).into(imageView);
        imageView.setOnClickListener( v -> {
            imageView.setRotation(imageView.getRotation() + 90);
        });
    }
}