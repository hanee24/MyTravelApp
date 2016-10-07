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
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        // get arguments from FolderActivity
        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("args");
        folder_id = String.valueOf(bundle.getInt("folder_id"));


    }

    public void mOnClick(View view){
        switch (view.getId()){
            case R.id.save :
                // save data to local db
                dbHelper = new DBHelper(WriteActivity.this);


                // send data to server
                postDataParams = new HashMap<>();
                postDataParams.put("folder_id",folder_id);
                postDataParams.put("user_id",MainActivity.getUserId()); // TODO: 2016. 10. 7. is this going to cause an error? when MainActivity may not initiated?
                // TODO: 2016. 10. 7. manage posting type when it can add images
                postDataParams.put("type","note");
                // get text from editTexts
                postDataParams.put("posting_title", ((EditText) findViewById(R.id.posting_title)).getText().toString());
                postDataParams.put("note",((EditText) findViewById(R.id.posting_body)).getText().toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                postDataParams.put("created",dateFormat.format(date)); // TODO: 2016. 10. 7. server! save it as modified if it's not created but modified

                myNetworkHandler();
                break;
            case R.id.cancel :
                finish();
                break;
        }
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
            dialog.setMessage("데이터를 가져오는 중입니다");
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
