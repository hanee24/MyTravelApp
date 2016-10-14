package com.example.parkhanee.mytravelapp;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;


public class FolderActivity extends AppCompatActivity {

    int folder_id;
    Folder folder;
    TextView tv_name,tv_desc, tv_date;
    DBHelper db;
    String TAG = "FolderActivity";
    RecyclerView recyclerView;
    FolderContentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        Intent a = getIntent();
        final Bundle bundle = a.getBundleExtra("args");
        folder_id = bundle.getInt("folder_id");

        // get folder from local DB
        db = new DBHelper(FolderActivity.this);
        //db.getAllFolders(MainActivity.getUserId());

        // initiate views
        tv_name = (TextView)findViewById(R.id.folderName);
        tv_desc = (TextView) findViewById(R.id.description);
        tv_date = (TextView) findViewById(R.id.textView25);

        mAdapter = new FolderContentsAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.listView4);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // TODO: 2016. 10. 12. may need to use different kind of LayoutManager
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator()); // TODO: 2016. 10. 12. may need to use different kind
    }


    public void mOnClick(View view){
        switch (view.getId()){
            case R.id.imageButton : // write imageButton
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
                        myClickHandler(false); // isList==false, since it's folder deleting process
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
            case R.id.writeButton : // write imageButton
                // go to WriteActivity
                Intent p = new Intent(FolderActivity.this,WriteActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putInt("folder_id",folder_id);
                p.putExtra("args",bundle2);
                startActivity(p);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // set folder info
        folder = db.getFolder(folder_id);
        tv_name.setText(folder.getName());
        tv_desc.setText(folder.getDesc());
        String date = folder.getDate_start().substring(0,10)+" ~ "+folder.getDate_end().substring(0,10);
        tv_date.setText(date);

        // TODO: 2016. 10. 13. 서버에서 폴더정보 받아와서 리사이클러뷰 어뎁터 통해서 뿌려주기
        myClickHandler(true /* isList */);

//        // get Posting info from local db and set it into listView
//        ArrayList<Posting> postings = db.getMyPostings(folder_id);
//        mAdapter.addItem(postings);
//        mAdapter.notifyDataSetChanged();
    }

    // check if the network has connected before executing AsyncTask network connection to server
    public void myClickHandler(Boolean isList) {

        //save update to server if there is network connection
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new mURLConnection().execute(isList);

        } else {
            Toast.makeText(FolderActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    private class mURLConnection extends AsyncTask<Boolean, Void, String> {

        String TAG_SUB, stringUrl;
        Boolean isList;
        ProgressDialog dialog;

        @Override
        protected String doInBackground(Boolean... booleen) {
            isList = booleen[0];
            if (isList){
                TAG_SUB = "[GetPostings]";
                stringUrl = "http://hanea8199.vps.phps.kr/get_postings.php";
            }else{
                TAG_SUB = "[DeleteFolderProcess]";
                stringUrl = "http://hanea8199.vps.phps.kr/deletefolder_process.php";
            }

            String postData = "folder_id="+String.valueOf(folder.getId());  // send folder id to server with POST method

            try {

                return downloadUrl(stringUrl,postData);
            } catch (IOException e) {
                return TAG_SUB + " Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPreExecute() {
//            if (isList){
                dialog = new ProgressDialog(FolderActivity.this);
                dialog.setMessage("잠시만 기다려 주세요");
                dialog.show();
//            }else{
//                // print the number of folders at local DB before deleting
//                Log.d(TAG, TAG_SUB + " onPreExecute: folders "+String.valueOf(db.getMyFolders(folder.getOwner_id()).size()));
//            }

        }

        @Override
        protected void onPostExecute(String s) {

            int resultCode=98;
            String str_result="";
            String resultMsg="";
            int totalCount=0;
            JSONObject bodyObject=null;

            try{
                //check the whole result
                JSONObject result = new JSONObject(s);
                Log.d(TAG,  TAG_SUB +"onPostExecute: result "+result.toString());

                if (isList){ // if it's [GetPostings] process
                    JSONObject header = result.getJSONObject("header");
                    resultMsg = header.getString("resultMsg");
                    Log.d(TAG,  TAG_SUB + " onPostExecute: resultMsg "+resultMsg);

                    // "body"로받은 오브젝트가 String 이면 totalCount = 0, JsonObject 이면 totalCount 받아서 설정해주기.
                    Object body = result.get("body");
                    if (body instanceof String) {// It's a string
                        Log.d(TAG, "onPostExecute: bodyString "+ body);
                        totalCount = 0;
                    } else if (body instanceof JSONObject) {// It's an object
                        bodyObject = (JSONObject) body;
                        totalCount = bodyObject.getInt("totalCount");
                    }

                    if(totalCount > 0){
                        JSONArray postingsArray=null;
                        JSONObject postingsObject=null;
                        // "postings" 로 받은 객체가 Array이면 totalCount >1 , Object 이면 totalCount ==1
                        Object postings = bodyObject.get("postings");
                        if (postings instanceof JSONArray) {// It's an Array
                            postingsArray = (JSONArray) postings;
                        } else if (postings instanceof JSONObject) {// It's an object
                            postingsObject = (JSONObject) postings;
                        }

                        // clear items which mAdapter has before adding new postings list.
                        mAdapter.clearItem();

                        // get each posting object from postings, and set it into recyclerView.
                        for (int i=0; i<totalCount; i++){
                            JSONObject posting=null;
                            if (postingsArray!=null){
                                posting = postingsArray.getJSONObject(i);
                            }else if (postingsObject !=null){
                                posting = postingsObject;
                            }else {
                                Log.d(TAG, "onPostExecute: something is wrong! both postingsArray and Object are null");
                            }

                            // add posting to recycler view adapter
                            Posting posting1 = new Posting();
                            posting1.setPosting_id(posting.getString("posting_id"));
                            posting1.setFolder_id(String.valueOf(folder_id));
                            posting1.setUser_id(posting.getString("user_id"));
                            posting1.setModified(posting.getString("modified"));
                            posting1.setCreated(posting.getString("modified"));
                            posting1.setType(posting.getString("type"));
                            posting1.setPosting_title(posting.getString("title"));
                            posting1.setNote(posting.getString("note"));
                            if ( ! posting.getString("image_path").equals("")){ // image_path 요소가 비어있지 않으면 posting1에 설정해줌.
                                posting1.setImage_path(posting.getString("image_path"));
                            }
                            mAdapter.addItem(posting1);
                        }
                        mAdapter.notifyDataSetChanged();

                        Log.d(TAG, TAG_SUB +" onPostExecute: postings "+postings.toString());
                    }

                }else { // if it's [DeleteFolderProcess]
                    // print the number of folders at local DB after deleting
                    Log.d(TAG, TAG_SUB + " onPostExecute: folders "+String.valueOf(db.getMyFolders(folder.getOwner_id()).size()));
                }


            }catch (JSONException e){
                e.printStackTrace();
            }

            // dismiss the progressDialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }



        private String downloadUrl(String myurl,String postData) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 5000000;

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
                Log.d(TAG, TAG_SUB + " The server response is: " + response);
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
