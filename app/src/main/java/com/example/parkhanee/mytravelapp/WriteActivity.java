package com.example.parkhanee.mytravelapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
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

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    String imageFileName;

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
//                                                Uri photoURI = FileProvider.getUriForFile(WriteActivity.this,
//                                                        "com.example.android.fileprovider",
//                                                        photoFile);
//                                                Log.d(TAG, "onClick: photoURI "+photoURI);
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
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = "HANEE_" + timeStamp + ".jpg";
            String path = saveToInternalStorage(bitmap,imageFileName);
            Log.d(TAG, "onActivityResult: path "+path);
            loadImageFromStorage(path,imageFileName);
//            imageView.setImageBitmap(bitmap);

            // add the photo to a gallery
//            galleryAddPic();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // for saving image into Internal memory
    private String saveToInternalStorage(Bitmap bitmapImage,String imageFileName){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath=new File(directory,imageFileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    // for loading image from Internal memory
    private void loadImageFromStorage(String path, String fileName)
    {

        try {
            File f=new File(path, fileName );  //File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    // for saving image into SD card
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

    // for saving image into SD card
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

            new ImageUploadTask().execute();
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

    /**
     * The class connects with server and uploads the photo
     *
     *
     */
    class ImageUploadTask extends AsyncTask<Void, Void, String> {
        private String webAddressToPost = "http://hanea8199.vps.phps.kr/uploadImage/temp.php";

        // private ProgressDialog dialog;
//        private ProgressDialog dialog = new ProgressDialog(WriteActivity.this);

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(webAddressToPost);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");

                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] data = bos.toByteArray();
                ByteArrayBody bab = new ByteArrayBody(data, "test.jpg");
                entity.addPart("file", bab);
//                entity.addPart("imageFileName", new StringBody("test.jpg"));

                conn.addRequestProperty("Content-length", entity.getContentLength() + "");
                conn.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());

                OutputStream os = conn.getOutputStream();
                entity.writeTo(conn.getOutputStream());
                os.close();
                conn.connect();

                int response = conn.getResponseCode();
                Log.d(TAG, "[ImageUploadTask] The server response is: " + response);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "[ImageUploadTask] doInBackground: http_ok");
                    return readStream(conn.getInputStream());
                }else {
                    Log.d(TAG, "[ImageUploadTask] doInBackground: connection error");
                    return readStream(conn.getErrorStream());
                }


            } catch (Exception e) {
                e.printStackTrace();
                // something went wrong. connection with the server error
            }

            return "[ImageUploadTask] something is wrong with the http result";
        }


        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d(TAG, "[ImageUploadTask] onPostExecute: result "+result);
            Toast.makeText(getApplicationContext(), "[ImageUploadTask] file uploaded",
                    Toast.LENGTH_LONG).show();
        }

    }
}
