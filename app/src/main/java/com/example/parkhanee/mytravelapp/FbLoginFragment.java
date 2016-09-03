package com.example.parkhanee.mytravelapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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


/**
 * Created by parkhanee on 2016. 8. 31..
 */
public class FbLoginFragment extends Fragment{
    public FbLoginFragment(){   }

    CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker tokenTracker;
    LoginButton loginButton;
    static Profile profile;
    public static Boolean profileHasSet=false;

    HashMap<String, String> postDataParams;
    private static final String DEBUG_TAG = "FbLoginFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) { //get arguments and set them as private variables
        super.onCreate(savedInstanceState);

        postDataParams = new HashMap<>();

        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create(); //create a callback manager to handle login responses
        tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                if (newProfile != null) { //the user may have logged in or changed some of his profile settings
                    profile = newProfile;
                    profileHasSet=true;
                } else {
                    profile = oldProfile;
                    profileHasSet=true;
                }
            }
        };
        profileTracker.startTracking();
        tokenTracker.startTracking();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fb_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        loginButton = (LoginButton) view.findViewById(R.id.login_button);

        //TODO: could get some more permissions from the user
        //loginButton.setReadPermissions("user_friends");
        //loginButton.setReadPermissions("email");
        //You can customize the properties of Login button
        //includes LoginBehavior, DefaultAudience, ToolTipPopup.Style and permissions on the LoginButton

        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) { //when the user newly logged in
                //String string = loginResult.getAccessToken().getUserId();
                //MainActivity.login(string);
                //loginResult.getAccessToken();

                while (!profileHasSet){ // wait til profile has set
                    //TODO : it may causes ANR. make it Asynchronous
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("waiting profile.. ");
                }
                String name = profile.getName();

                //send userInfo to server
                putDataIntoParams(loginResult,profile);
                myClickHandler();

                Intent a = new Intent(getActivity(),MainActivity.class);
                a.putExtra("name",name);
                a.putExtra("newlyLogged",true);
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //TODO : not sure if it's correct to use this flag
                startActivity(a);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { //얘가 없으면 로그인 하고나서 로그인 버튼이 "로그아웃"으로 바뀌지 않으ㅁ //it passes the result to the CallbackManager
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    public void putDataIntoParams(LoginResult result,Profile profile){
        String user_id = result.getAccessToken().getUserId();
        postDataParams.put("user_id",user_id);
        String user_name = profile.getName();
        postDataParams.put("user_name",user_name);
    }

    public void myClickHandler() { // check if the network has connected
        String stringUrl = "http://hanea8199.vps.phps.kr/fb_signin.php";
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            Toast.makeText(getContext(), "! No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
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

            int resultCode=99;
            String resultMsg="";
            String msg;
            try{
                JSONObject result = new JSONObject(s);
                resultCode = result.getInt("resultCode");
                //resultMsg = result.getString("resultMsg");
            }catch (JSONException e){
                e.printStackTrace();
            }

            if (resultCode==00){ //result is Okay
                msg = "fb info has passed to server db successfully";
               /*AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent aa = new Intent(getContext(),LogInActivity.class);
                                startActivity(aa);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();*/
                Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
            }else{ // error occurred
                switch (resultCode){
                    case 11 : msg = "no info";
                        break;
                    case 12: msg = "id already added on the server";
                        break;
                    default: msg = "unknown error";
                        break;
                }
                Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
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
