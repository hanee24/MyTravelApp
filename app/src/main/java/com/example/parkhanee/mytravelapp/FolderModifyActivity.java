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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class FolderModifyActivity extends AppCompatActivity {

    private EditText et_name;
    EditText et_desc, et_start, et_end;
    String TAG = "FolderModifyFragment";
    private InputMethodManager imm;
    ProgressDialog dialog;
    HashMap<String, String> modifyFolderPostDataParams;
    int position;
    Folder folder;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_modify);

        // get arguments from FolderActivity
        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("args");
        position = bundle.getInt("position");

        // get folder from local DB
        db = new DBHelper(FolderModifyActivity.this);
        folder = db.getFolder(position);

        //hide keyboard as default
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        setLayout();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                imm.hideSoftInputFromWindow(et_name.getWindowToken(), 0);
            }
        },100);
    }

    public void setLayout(){
        // initiate views
        et_name = (EditText) findViewById(R.id.editText6);
        et_desc = (EditText) findViewById(R.id.editText7);
        et_start = (EditText) findViewById(R.id.editText8);
        et_end = (EditText) findViewById(R.id.editText9);
        dialog = new ProgressDialog(FolderModifyActivity.this);
        modifyFolderPostDataParams = new HashMap<>();

        // set folder information
        et_name.setText(folder.getName());
        et_desc.setText(folder.getDesc());
        et_start.setText(folder.getDate_start());
        et_end.setText(folder.getDate_end());
    }

    public void mOnClick(View view){
        switch (view.getId()){
            case R.id.save :
                modifyFolderPostDataParams.put("folder_id",String.valueOf(folder.getId()));
                // get modified data from EditTexts
                modifyFolderPostDataParams.put("name",et_name.getText().toString());
                modifyFolderPostDataParams.put("desc",et_desc.getText().toString());
                modifyFolderPostDataParams.put("start",et_start.getText().toString());
                modifyFolderPostDataParams.put("end",et_end.getText().toString());
                myClickHandler();

                // update local db with new info
                Folder folder1 = folder;
                folder1.setName(et_name.getText().toString());
                folder1.setDesc(et_desc.getText().toString());
                folder1.setDate_start(et_start.getText().toString());
                folder1.setDate_end(et_end.getText().toString());
                db.updateFolder(folder1);
                break;
            case R.id.cancel :
                finish();
                break;
            default:
                Log.d(TAG, "mOnClick: DEFAULT?");
                break;
        }
    }

    // check if the network has connected before executing AsyncTask network connection to server
    public void myClickHandler() {

        String stringUrl; //server url
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            stringUrl = "http://hanea8199.vps.phps.kr/modifyfolder_process.php";
            new FolderModifyProcess().execute(stringUrl);
            
        } else {
            Toast.makeText(FolderModifyActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    private class FolderModifyProcess extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            dialog.setMessage("데이터를 가져오는 중입니다");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {

            int resultCode=99;
            String resultMsg="";

            try{
                JSONObject result = new JSONObject(s);
               //JSONObject header = result.getJSONObject("header");
                resultCode = result.getInt("resultCode");

                //check the whole result
                resultMsg = result.toString();
                Log.d(TAG, "onPostExecute: "+resultMsg);

                if (resultCode==00) { // result is ok
                    Log.d(TAG, "onPostExecute: Result is OK");
                }else{
                    Log.d(TAG, "onPostExecute: resultCode is not okay ? ");
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // finish folderModifyActivity since the modification is done.
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
                writer.write(getPostDataString(modifyFolderPostDataParams));
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
