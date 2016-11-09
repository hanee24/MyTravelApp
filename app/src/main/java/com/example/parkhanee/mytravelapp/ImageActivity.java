package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageActivity extends AppCompatActivity {

    private final String TAG = "ImageActivity";
    private Bitmap bitmap;

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
            case R.id.imageExit : // exit button clicked
                finish();
                break;
            case R.id.imageDownload : // image download button clicked
                if (bitmap == null){
                    Toast.makeText(ImageActivity.this, "잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                }else{
                    saveToGallery(bitmap);
                }
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
            bitmap = result;
        }
    }

    public void saveToGallery(Bitmap bm){
        FileOutputStream stream;

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = getUserId()+"_" + timeStamp + ".jpg";
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                    + "/MyTravelApp/" + imageFileName;
            Log.d(TAG, "saveToGallery: path "+path);

            // mkdirs() method create new directories if they don't exist but they do in the path. and returns boolean value if it created directories or not
            Boolean mkdir = new File(path.substring(0,path.lastIndexOf("/"))).mkdirs();
            Log.d(TAG, "saveToGallery: mkdir "+mkdir);

            stream = new FileOutputStream(path);
            bm.compress(Bitmap.CompressFormat.JPEG,100,stream);

            MediaScanner scanner = MediaScanner.newInstance(ImageActivity.this);
            scanner.mediaScanning(path);

            Toast.makeText(ImageActivity.this, "갤러리에 이미지를 저장했습니다", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e){
            Toast.makeText(ImageActivity.this, "이미지 저장에 실패하였습니다", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String getUserId(){
        SharedPreferences sharedPreferences =  getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
        String str = sharedPreferences.getString(getString(R.string.userIdKey),null);
        return str;
    }
}

