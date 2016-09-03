package com.example.parkhanee.mytravelapp;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Button btn_search ;
    Button btn_folder;
    TextView location;

    //test
    TextView tv_username;
    TextView tv_login;
    ImageView iv_icon;

    GoogleApiClient mGoogleApiClient;
    Location myLocation;
    Double lat;
    Double lgt;

    public static Boolean ifLogged;
    public static Boolean ifFbLogged=false;

    public static SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String userIdKey = "userId";
    public static final String isFBKey = "isFB"; // "y", "n"
    public static String fbName="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(MainActivity.this);
        setContentView(R.layout.activity_main);

        //hide facebook login fragment
        View a = findViewById(R.id.fragment2);
        a.setVisibility(View.GONE);

        btn_search = (Button) findViewById(R.id.button);
        btn_folder = (Button) findViewById(R.id.button2);
        location = (TextView) findViewById(R.id.textView2);
        tv_username = (TextView) findViewById(R.id.tv_test);
        tv_login = (TextView) findViewById(R.id.tv_test2);
        iv_icon = (ImageView) findViewById(R.id.icon);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(com.google.android.gms.location.LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        System.out.println("Main Resume");

        Intent a = getIntent();
        System.out.println(a);
        Bundle extras = getIntent().getExtras();
        System.out.println(extras);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken!=null){ //if 페북로그인 되어있으면

//            Boolean is = extras.getBoolean("newlyLogged",false);
//            System.out.println(is);
//            String name = extras.getString("name");
//            System.out.println(name);
//            System.out.println("Main ifFbLogged");
//            //String name = "fb";

            // execute AsyncTask to get Profile
            new getProfile().execute(); //TODO 발표할때 이 한줄만 주석처리하면 똑같은가

            String name = accessToken.getUserId(); // set temp name while waiting for profile to come ?
            login(name,true);
        }


        String fb = sharedpreferences.getString(isFBKey,"");
        switch (fb){ //ifLogged랑 ifFbLogged는 mainActivity Create할 때 마다 매번 새로 만들어지는 변수들이므로 SP에서 매번 동기화 필요
            case "y": ifLogged=true; ifFbLogged=true;
                System.out.println("Main case1") ;
                break;
            case "n" : ifLogged=true; ifFbLogged=false;
                System.out.println("Main case2");
                break;
            default: ifLogged= false; ifFbLogged=false;
                System.out.println("Main case3");
                break;
        }

        //set Boolean islogged
        //if logged, set login info on textview
        //else, set
        if (ifLogged){ //TODO set ifLogged when just logged in from fb
            tv_login.setText("로그아웃");
            tv_username.setText(getLoginId());
            System.out.println("Main ifLogged");
            iv_icon.setVisibility(View.VISIBLE);
            if (ifFbLogged){
                iv_icon.setImageResource(R.drawable.com_facebook_button_icon_blue);
            }else {
                iv_icon.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }else {
            iv_icon.setVisibility(View.GONE);
            System.out.println("Main ! ifLogged");
            tv_login.setText("로그인");
            tv_username.setText("My Travel App");
        }

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ifLogged){ //log out onClick
                    logout();
                    Toast.makeText(MainActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                    tv_login.setText("로그인");
                    tv_username.setText("My Travel App");
                    iv_icon.setVisibility(View.GONE);
                }else { //log in onClick
                    Intent i = new Intent(MainActivity.this,LogInActivity.class);
                    startActivity(i);
                }
            }
        });


    }

    public static String getLoginId(){
        String str = sharedpreferences.getString(userIdKey,null);
        return str;
    }

    public static Boolean getisFB(){ // is it needed ???
        String str = sharedpreferences.getString(isFBKey,"n");
        return str.equals("y");
    }

    public static void login(String id,Boolean isFB){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(userIdKey, id);
        if (isFB){
            editor.putString(isFBKey,"y"); //FB login
        }else{
            editor.putString(isFBKey, "n");
        }

        editor.commit();
        ifLogged = true;
    }

    public static void logout(){ //removeLoginInfoFromSharedPreference
        if (getisFB()){
            LoginManager.getInstance().logOut(); // facebook logout !!
            ifFbLogged=false;
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(userIdKey);
        editor.remove(isFBKey);
        editor.commit();
        ifLogged = false;
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myLocation = com.google.android.gms.location.LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        lat = myLocation.getLatitude();
        lgt = myLocation.getLongitude();
        String Lat = String.valueOf(lat);
        String Lgt = String.valueOf(lgt);
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+Lat+","+Lgt+"&key=AIzaSyBZ9S7Eo3eaZ0ocOQTuJScvOw_xbXiM194&language=ko";
        //String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=37.4841774,126.9727024&language=ko&key=AIzaSyBZ9S7Eo3eaZ0ocOQTuJScvOw_xbXiM194" ;
        new getGeoCode().execute(url);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,NearbyD1Activity.class);
                i.putExtra("lat",lat);
                i.putExtra("lgt",lgt);
                startActivity(i);
            }
        });

        btn_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ifLogged){
                    //TODO : go to travel folder activity
                }else {
                    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                    adb.setTitle("로그인이 필요한 서비스 입니다");
                    adb.setIcon(android.R.drawable.ic_dialog_alert);
                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(MainActivity.this,LogInActivity.class);
                            startActivity(i);
                        } });
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        } });
                    adb.show();
                }

            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class getGeoCode extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String strUrl = strings[0];

            JSONArray results=null;
            String formatted_address=null;

            String buf;
            String jsonString ="";
            try {
                URL url = new URL(strUrl);
                URLConnection conn = url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                while((buf=br.readLine())!=null){
                    jsonString +=buf;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // System.out.println("jsonString"+jsonString);

            try {
                JSONObject object = new JSONObject(jsonString);
                //JSONObject response = object.getJSONObject("response");
                results = object.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject item = results.getJSONObject(0); //NullPointerException when network doesn't work well?
                formatted_address = item.getString("formatted_address");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return formatted_address;
        }

        @Override
        protected void onPostExecute(String s) {
            //split "s" , get second, third, forth words
            String[] str = s.split(" ");
            String ss = str[1]+" "+str[2]+" "+str[3];
            location.setText(ss); //TODO : set text "cannot find location without network" when there is no network connection
        }
    }

    private class getProfile extends AsyncTask<Void,Void,String>{
        myProfile myProfile = new myProfile();
        ProfileTracker tracker;
        @Override
        protected void onPostExecute(String s) {
            if (tracker!=null){
                tracker.stopTracking();
            }
            login(myProfile.getName(),true);
            tv_username.setText(myProfile.getName());
        }

        @Override
        protected String doInBackground(Void... voids) {

            Profile profile = Profile.getCurrentProfile();
            if (profile!=null){
                myProfile.setName(profile.getName());
            }else {

                tracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        if (currentProfile != null) { //the user may have logged in or changed some of his profile settings
                            myProfile.setName(currentProfile.getName());
                        } else {
                            myProfile.setName(oldProfile.getName());
                        }
                    }
                };
                tracker.startTracking();

                while (!myProfile.profileHasSet()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("waiting profile.. ");
                }
            }
            return myProfile.getName();
        }

        private class myProfile {
            String name;

            public void setName(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public Boolean profileHasSet(){
                return name!=null;
            }
        }
    }




}
