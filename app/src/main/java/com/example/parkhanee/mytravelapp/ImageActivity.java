package com.example.parkhanee.mytravelapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=113;

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
                    runtimePermission();
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

    public void runtimePermission (){
        // [Android 6.0] runtime permission request
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "saveToGallery: ask write_external_storage permission");

            // 권한 승인여부 사용자에게 물어보기
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        saveToGallery();
    }

    public void saveToGallery(){
        FileOutputStream stream;

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String r = String.valueOf((int)(Math.random() * 10000)+1); // 랜덤4자리숫자
        String imageFileName = getUserId()+"_" + timeStamp +"_"+r+ ".jpg";
        //String path = "/storage/emulated/0/Download/"+imageFileName;
        //String path = "/storage/emulated/0/DCIM/MyTravelApp/"+imageFileName;
       // String path = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/MyTravelApp/" + imageFileName;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                + "/MyTravelApp/" + imageFileName;
        Log.d(TAG, "saveToGallery: path "+path);

        try {
            // mkdirs() method create new directories if they don't exist but they do in the path. and returns boolean value if it is created directories or not
            Boolean mkdir = new File(path.substring(0,path.lastIndexOf("/"))).mkdirs();
            Log.d(TAG, "saveToGallery: mkdir "+mkdir);

            stream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

            MediaScanner scanner = MediaScanner.newInstance(ImageActivity.this);
            scanner.mediaScanning(path);

            Toast.makeText(ImageActivity.this, "갤러리에 이미지를 저장했습니다", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e){
            Toast.makeText(ImageActivity.this, "이미지 저장에 실패하였습니다", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "onRequestPermissionsResult: permission CASE 2 ");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    saveToGallery();

                } else {

                    Log.d(TAG, "onRequestPermissionsResult: permission CASE 3 ");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(ImageActivity.this, "외부 저장소 접근 권한에 동의해 주세요", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private String getUserId(){
        SharedPreferences sharedPreferences =  getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
        String str = sharedPreferences.getString(getString(R.string.userIdKey),null);
        return str;
    }
}

