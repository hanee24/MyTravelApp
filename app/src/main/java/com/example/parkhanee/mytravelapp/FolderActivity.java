package com.example.parkhanee.mytravelapp;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


public class FolderActivity extends AppCompatActivity {

    int folder_id;
    Folder folder;
    TextView tv_name,tv_desc, tv_date;
    DBHelper db;
    String TAG = "FolderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        Log.d(TAG, "sendNotification: ");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setSmallIcon(R.drawable.road);
        mBuilder.setTicker("Notification.Builder");
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setNumber(10);
        mBuilder.setContentTitle("Notification.Builder Title");
        mBuilder.setContentText("Notification.Builder Massage");
        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);

        nm.notify(111, mBuilder.build());

//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
////                .setSmallIcon(R.drawable.common_ic_googleplayservices)
//                .setContentTitle("title")
//                .setContentText("notification test");
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(11 /* ID of notification */, notificationBuilder.build());

        Intent a = getIntent();
        final Bundle bundle = a.getBundleExtra("args");
        folder_id = bundle.getInt("folder_id");

        // get folder from local DB
        db = new DBHelper(FolderActivity.this);
        db.getAllFolders(MainActivity.getUserId());

        // initiate views
        tv_name = (TextView)findViewById(R.id.folderName);
        tv_desc = (TextView) findViewById(R.id.description);
        tv_date = (TextView) findViewById(R.id.textView25);
    }

    public void mOnClick(View view){
        switch (view.getId()){
            case R.id.imageButton : // edit imageButton
                Intent i = new Intent(FolderActivity.this,FolderUpdateActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("folder_id",folder_id);
                i.putExtra("args",bundle1);
                startActivity(i);
                break;
            case R.id.imageButton4 : // delete imageButton
                // show a confirm dialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FolderActivity.this);
                    alertDialog.setTitle("폴더 삭제");
                    alertDialog.setMessage("정말 삭제 하시겠습니까?");
                    alertDialog.setIcon(R.drawable.garbage);

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        //  delete folder from local db
                        db.deleteFolder(folder_id);

                        // delete folder from server db
                        myClickHandler();
                        finish();
                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                // Showing Alert Message
                alertDialog.show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        folder = db.getFolder(folder_id);
        tv_name.setText(folder.getName());
        tv_desc.setText(folder.getDesc());
        String date = folder.getDate_start().substring(0,10)+" ~ "+folder.getDate_end().substring(0,10);
        tv_date.setText(date);
    }

    // check if the network has connected before executing AsyncTask network connection to server
    public void myClickHandler() {

        //save update to server if there is network connection
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            String stringUrl = "http://hanea8199.vps.phps.kr/deletefolder_process.php";
            String postData = "folder_id="+String.valueOf(folder.getId()); // send folder id to server with POST method
            new DeleteFolderProcess().execute(stringUrl,postData);

        } else {
            Toast.makeText(FolderActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    private class DeleteFolderProcess extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // print the number of folders at local DB before deleting
            Log.d(TAG, "onPreExecute: folders "+String.valueOf(db.getAllFolders(folder.getUser_id()).size()));
        }

        @Override
        protected void onPostExecute(String s) {

            int resultCode=98;
            String str_result="";

            try{
                JSONObject result = new JSONObject(s);
                resultCode = result.getInt("resultCode");

                //check the whole result
                str_result = result.toString();
                Log.d(TAG, "onPostExecute: "+str_result);
                // print the number of folders at local DB after deleting
                Log.d(TAG, "onPostExecute: folders "+String.valueOf(db.getAllFolders(folder.getUser_id()).size()));

            }catch (JSONException e){
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return downloadUrl(strings[0],strings[1]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        private String downloadUrl(String myurl,String postData) throws IOException {
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
                writer.write(postData);
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



    }

}
