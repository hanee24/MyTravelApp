package com.example.parkhanee.mytravelapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ViaNotificationActivity extends AppCompatActivity {

    DBHelper dbHelper;
    String TAG = "ViaNotificationActivity";
    String share_id ;
    static Share share;
    HashMap<String,String> postDataParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_via_notification);

        Intent i = getIntent();
        share_id = i.getStringExtra("share_id");
        dbHelper = new DBHelper(this);
        share = dbHelper.getShare(Integer.valueOf(share_id));
        Folder folder = dbHelper.getFolder(Integer.parseInt(share.getFolder_id()));
        //폴더이름, 보낸 사용자 이름 보여주고 수락/거부 버튼
        TextView tv = (TextView) findViewById(R.id.textView39);
        String str = folder.getOwner_id()+"님이 <"+folder.getName()+"> 폴더의 공유를 요청하였습니다. 수락하시겠습니까? ";
        tv.setText(str);

    }

    public void mOnClick(View view){

        //  수락 / 거부한 정보 서버에 업뎃.

        switch (view.getId()){
            case R.id.accept : // 공유 수락
                // 로컬이비에 수락 정보 저장
                share.setState("Accepted");
                dbHelper.updateShare(share);

                Toast.makeText(ViaNotificationActivity.this, "폴더 공유를 수락하였습니다", Toast.LENGTH_SHORT).show();

                // 서버로 수락 정보 보내기
                MyNetworkHandler("Accepted");
                break;
            case R.id.reject : // 공유 거부
                // deleteShare
                dbHelper.deleteFolder(Integer.parseInt(share_id));
                // deleteFolder
                dbHelper.deleteFolder(Integer.parseInt(share.getFolder_id()));
                MyNetworkHandler("Denied");
                break;
        }
    }


    // check if the network has connected before executing AsyncTask network connection to server
    public void MyNetworkHandler(String state) {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new UpdateShareState().execute(state);

        } else {
            Toast.makeText(ViaNotificationActivity.this , "Cannot proceed, No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }


    public class UpdateShareState extends AsyncTask<String, Void, String>{
        ProgressDialog dialog;
        String state;

//        @Override
//        protected void onPreExecute() {
//            dialog = new ProgressDialog(ViaNotificationActivity.this);
//            dialog.setMessage("잠시만 기다려 주세요");
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
//        }

        @Override
        protected void onPostExecute(String s) {

            String str_result="";

            try{
                JSONObject result = new JSONObject(s);

                //check the whole result
                str_result = result.toString();
                Log.d(TAG, "onPostExecute: "+str_result);
            }catch (JSONException e){
                e.printStackTrace();
            }
//
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }


            if (state.equals("Denied")){
                afterDeny();
            }else {
                afterAccept();
            }
        }

        private void afterAccept(){
            // 수락했으니까 해당 폴더의 폴더액티비티로 넘어가기
            Intent i = new Intent(ViaNotificationActivity.this,FolderActivity.class);
            Bundle args = new Bundle();
            args.putInt("folder_id",Integer.parseInt(share.getFolder_id()));
            i.putExtra("args",args);
            startActivity(i);
            finish();
            dbHelper.close();
        }

        private void afterDeny(){
//          dbHelper.deleteFolder(Integer.parseInt(share.getFolder_id()));
            // TODO: 2016. 10. 6. 바로 지우지말고 일단 DENIED 상태로 남겨서 '거부한 목록' 보이기. 거기서 지워야 로컬디비에서 정보 완전히 지우기.

            Toast.makeText(ViaNotificationActivity.this, "폴더 공유를 거부하였습니다", Toast.LENGTH_SHORT).show();
            finish();
            dbHelper.close();
        }


        @Override
        protected String doInBackground(String... strings) {
            state = strings[0];
            try {
                return downloadUrl("http://hanea8199.vps.phps.kr/update_share_state.php");
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
                postDataParams = new HashMap<>();
                postDataParams.put("user_id",share.getUser_id());
                postDataParams.put("share_id",share.getShare_id());
                postDataParams.put("state",state);
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
