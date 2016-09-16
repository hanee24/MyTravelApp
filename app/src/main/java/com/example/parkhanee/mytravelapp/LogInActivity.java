package com.example.parkhanee.mytravelapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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

public class LogInActivity extends AppCompatActivity {

    Button btn_okay;
    Button btn_signin;
    private static final String DEBUG_TAG = "LogInActivity";
    HashMap<String, String> postDataParams;
    EditText et_id;
    ProgressDialog dialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(LogInActivity.this);
        setContentView(R.layout.activity_log_in);

        dialog = new ProgressDialog(LogInActivity.this);
        btn_okay = (Button) findViewById(R.id.okay);
        postDataParams = new HashMap<>();

        // hide password when typed
//        EditText pwd = (EditText) findViewById(R.id.pasword);
//        pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = ((EditText) findViewById(R.id.id)).getText().toString();
                String password = ((EditText) findViewById(R.id.pasword)).getText().toString();

                postDataParams.put("id", id);
                postDataParams.put("password", password);

                myClickHandler(view);

            }
        });

        btn_signin = (Button) findViewById(R.id.signin);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aa = new Intent(LogInActivity.this, SignInActivity.class);
                startActivity(aa);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void myClickHandler(View view) { // check if the network has connected   //TODO : add this to all the activities which need network connection


        String stringUrl = "http://hanea8199.vps.phps.kr/login.php";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            Toast.makeText(LogInActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "LogIn Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.parkhanee.mytravelapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "LogIn Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.parkhanee.mytravelapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            //create progress dialog
            dialog.setMessage("잠시만 기다려주세요");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            int resultCode = 99;
            String resultMsg = "";
            String msg;
            try {
                JSONObject result = new JSONObject(s);
                resultCode = result.getInt("resultCode");
                //resultMsg = result.getString("resultMsg");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //close progress dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (resultCode == 00) { //result is Okay
                msg = "로그인 되었습니다";
                et_id = (EditText) findViewById(R.id.id);
                String id = et_id.getText().toString();
                MainActivity.login(id, id, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                builder.setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent aa = new Intent(LogInActivity.this, MainActivity.class);
                                aa.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(aa);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else { // error occurred
                switch (resultCode) {
                    case 14:
                        msg = "로그인 정보가 바르지 않습니다";
                        break;
                    case 11:
                        msg = "모든 정보를 입력해 주세요";
                        break;
                    default:
                        msg = "unknown error";
                        break;
                }
                Toast.makeText(LogInActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

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
                Log.d(DEBUG_TAG, "The response is: " + response);
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

        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
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

            return result.toString();
        }
    }
}
