package com.example.parkhanee.mytravelapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class FolderShareActivity extends Activity {

    public static int folder_id;
    EditText et_search;
    //FolderShareAdapter myAdapter;
    public static ProgressDialog dialog;
    String TAG = "FolderShareActivity";
    HashMap<String,String> postDataParams;
    private ListView listView;
    private FolderShareAdapter mAdapter;
    public static Folder folder;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_share);

        Intent i = getIntent();
        Bundle args = i.getExtras();
        folder_id = args.getInt("folder_id");
        db = new DBHelper(FolderShareActivity.this);
        folder = db.getFolder(folder_id);


        et_search = (EditText) findViewById(R.id.editText10);
        mAdapter = new FolderShareAdapter(FolderShareActivity.this);
        listView = (ListView) findViewById(R.id.listView3);
        listView.setAdapter(mAdapter);
        dialog = new ProgressDialog(FolderShareActivity.this);
        postDataParams  = new HashMap<>();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
        String str = sharedPreferences.getString(getString(R.string.userIdKey),null);
        postDataParams.put("sender",str);
        postDataParams.put("folder_id",String.valueOf(folder_id));

        myNetworkHandler("http://hanea8199.vps.phps.kr/getuserlist_process.php"); // --> get user list process

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    // check if the network has connected before executing AsyncTask network connection to server
    public void myNetworkHandler(String url) {
        TextView tv_large = (TextView) findViewById(R.id.textView37);
        TextView tv_small = (TextView) findViewById(R.id.textView38);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            tv_small.setVisibility(View.GONE);
            tv_large.setVisibility(View.GONE);
            new AsyncProcess().execute(url);

        } else {
            Toast.makeText(FolderShareActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();

            if (url.equals("http://hanea8199.vps.phps.kr/getuserlist_process.php")){
                // 인터넷없을때는 안됨 안내.
                tv_large.setVisibility(View.VISIBLE);
                tv_small.setVisibility(View.VISIBLE);

                tv_large.setText("사용자 목록을 불러올 수 없습니다");
                tv_small.setText("인터넷 연결을 확인해 주세요");
            }
        }
    }

    public class AsyncProcess extends AsyncTask<String,Void,String>{
        String stringUrl;

        @Override
        protected void onPreExecute() {
            dialog.setMessage("잠시만 기다려 주세요");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String string) {

            try{
                JSONObject result = new JSONObject(string);
                //check the whole result
                String str_result = result.toString();
                Log.d(TAG, "onPostExecute: "+str_result);

                // JSONObject header = result.getJSONObject("header");
                // int resultCode = header.getInt("resultCode");

                JSONObject body = result.getJSONObject("body");
                JSONArray users = body.getJSONArray("users");

                mAdapter.clearItem();
                for (int i=0;i<users.length();i++){
                    JSONObject u = users.getJSONObject(i);
                    String f = u.getString("isFB");
                    Boolean isFB = f.equals("1");
                    User user = new User(u.getString("user_id"),u.getString("user_name"),isFB);
                    mAdapter.addItem(i,user);
                }
                mAdapter.notifyDataSetChanged();

            }catch (JSONException e){
                e.printStackTrace();
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            stringUrl= strings[0];
            try {
                return downloadUrl(stringUrl);
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
