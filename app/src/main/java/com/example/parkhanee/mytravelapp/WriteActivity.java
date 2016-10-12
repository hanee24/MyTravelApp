package com.example.parkhanee.mytravelapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WriteActivity extends AppCompatActivity {

    static String folder_id;
    HashMap<String, String> postDataParams;
    ProgressDialog dialog;
    private final String TAG = "WriteActivity";
    DBHelper dbHelper;

    private ImageView imageView;
    static final int REQUEST_GALLERY = 1;
    static final int REQUEST_IMAGE_CAPTURE = 12;
    private Bitmap bitmap;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        // get arguments from FolderActivity
        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("args");
        folder_id = String.valueOf(bundle.getInt("folder_id"));

        imageView = (ImageView) findViewById(R.id.image);
    }

    public void mOnClick(View view){
        switch (view.getId()){
            case R.id.save :
                String title = ((EditText) findViewById(R.id.posting_title)).getText().toString();
                String note = ((EditText) findViewById(R.id.posting_body)).getText().toString();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String now = dateFormat.format(date);
                // save data to local db
                dbHelper = new DBHelper(WriteActivity.this);
                String unixTime = String.valueOf(System.currentTimeMillis() / 1000); //set unix time as folder id
                dbHelper.addPosting(new Posting(unixTime,folder_id,MainActivity.getUserId(),"note",title, note, now,now));

                // send data to server
                postDataParams = new HashMap<>();
                postDataParams.put("posting_id",unixTime);
                postDataParams.put("folder_id",folder_id);
                postDataParams.put("user_id",MainActivity.getUserId()); // TODO: 2016. 10. 7. is this going to cause an error? when MainActivity may not initiated?
                // TODO: 2016. 10. 7. manage posting type when it can add images
                postDataParams.put("type","note");
                // get text from editTexts
                postDataParams.put("posting_title", title);
                postDataParams.put("note",note);
                postDataParams.put("created",now);

                myNetworkHandler();
                break;
            case R.id.cancel :
                finish();
                break;
            case R.id.addPicture : // 사진 추가하기
                final CharSequence[] items = {
                        "사진 촬영", "사진 앨범에서 선택"
                };

                // show an alert window
                AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
                builder.setTitle("사진 추가하기")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i){
                                    case 0 : // 사진 촬영
                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                                            // save the full-size photo : 빈 이미지파일을 만들고 카메라 인텐트를 호출하면 찍은 사진이 그 이미지파일로 간다
//                                            // Create the File where the photo should go
//                                            File photoFile = null;
//                                            try {
//                                                photoFile = createImageFile();
//                                            } catch (IOException ex) {
//                                                // Error occurred while creating the File
//
//                                            }
//                                            // Continue only if the File was successfully created
//                                            if (photoFile != null) {
//                                                Log.d(TAG, "onClick: "+getExternalFilesDir(Environment.DIRECTORY_PICTURES) );
//                                                Uri photoURI = FileProvider.getUriForFile(WriteActivity.this,
//                                                        "com.example.android.fileprovider",
//                                                        photoFile);
//                                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                                            }
//
//                                        }
                                        break;
                                    case 1 : // 사진 앨범에서 선택
                                        Intent intent = new Intent();
                                        intent.setType("image/*");
                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        startActivityForResult(intent, REQUEST_GALLERY);
                                        break;
                                }
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
//                // We need to recycle unused bitmaps
//                if (bitmap != null) {
//                    bitmap.recycle();
//                }
                InputStream stream = getContentResolver().openInputStream(
                        data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                assert stream != null;
                stream.close();
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            /*
                The Android Camera application encodes the photo in the return Intent delivered to onActivityResult()
                as a small Bitmap in the extras, under the key "data".
             */
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmap);

            // add the photo to a gallery
//            galleryAddPic();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "HANEE_" + timeStamp + "_";
        /*
         getExternalFilesDir method returns a directory within which the photos remain private to my app only.
         and the photos will be deleted when the user uninstalls the app
         ( https://developer.android.com/training/camera/photobasics.html#TaskScalePhoto )
         */
        File storageDir =  Environment.getExternalStorageDirectory();
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    public void myNetworkHandler() {

        // check if the network has connected
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            String stringUrl = "http://hanea8199.vps.phps.kr/write_process.php";
            new WriteProcess().execute(stringUrl); // connect to server

        } else {
            Toast.makeText(WriteActivity.this, "Cannot proceed, No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    private class WriteProcess extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(WriteActivity.this);
            dialog.setMessage("잠시만 기다려 주세요");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {

            String resultMsg="";

            try{
                JSONObject result = new JSONObject(s);

                //check the whole result
                resultMsg = result.toString();
                Log.d(TAG, "onPostExecute: "+resultMsg);

            }catch (JSONException e){
                e.printStackTrace();
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // finish write activity since it's done writing
            finish();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return downloadUrl(strings[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 50000;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                // add post parameters
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                int response = conn.getResponseCode();
                Log.d(TAG, "The server response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public String readIt(InputStream stream, int len) throws IOException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            //convert data  being sent to server as POST method into correct form
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            Log.d(TAG, "getPostDataString: "+result.toString());

            return result.toString();
        }

    }
}
