package com.example.parkhanee.mytravelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {

    private final String TAG = "ImageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent i = getIntent(); // comes from FolderContentsAdapter -- imageView.onClick()
        String path = i.getStringExtra("original_path");
        ImageView imageView = (ImageView) findViewById(R.id.imageView9);
        Picasso.with(ImageActivity.this).load(path).into(imageView);

    }
}
