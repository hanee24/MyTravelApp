package com.example.parkhanee.mytravelapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class ImageActivity extends AppCompatActivity {

    private final String TAG = "ImageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent i = getIntent(); // intent comes from FolderContentsAdapter -- imageView.onClick()
        String path = i.getStringExtra("original_path");
        ImageView imageView = (ImageView) findViewById(R.id.imageView9);
        //Picasso.with(ImageActivity.this).load(path).into(imageView);
        new DownloadImageTask(imageView).execute(path);
    }

    public void mOnClick(View view){
        switch (view.getId()){
            case R.id.imageExit :
                finish();
                break;
            case R.id.imageDownload :
                break;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
