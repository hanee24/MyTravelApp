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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Button btn_search ;
    Button btn_folder;
    TextView location;
    Button btn_login;

    GoogleApiClient mGoogleApiClient;
    Location myLocation;
    Double lat;
    Double lgt;

    Boolean ifLogged=false;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String userIdKey = "userId";
    public static final String ifLoggedKey = "ifLogged"; // "y", "n"



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide facebook login fragment
        View a = findViewById(R.id.fragment2);
        a.setVisibility(View.GONE);


        btn_search = (Button) findViewById(R.id.button);
        btn_folder = (Button) findViewById(R.id.button2);
        location = (TextView) findViewById(R.id.textView2);
        btn_login = (Button) findViewById(R.id.button8);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(com.google.android.gms.location.LocationServices.API)
                    .build();
        }

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        Boolean ifNewlyLogged = intent.getBooleanExtra("ifNewlyLogged",false);
        Boolean ifFbLogged = intent.getBooleanExtra("ifFbLogged",false);

        if (ifNewlyLogged){ //방금 로그인함


            String savedId="";
            if (ifFbLogged){ //페이스북 로그인
                //get user info
                //AccessToken token = FbLoginFragment.getCurrentAccessToken();
                Profile profile = FbLoginFragment.getCurrentProfile();
                savedId = profile.getName();
                System.out.println("newly fb login");
            }else{ //일반로그인
                //get user info through intent from LoginActivity
                savedId = intent.getStringExtra("userId");
                System.out.println("newly login");
            }

            //save user info on shared preference
            if (savedId.equals("")){
                //TODO error ???
                System.out.println("no saved id");
            }else {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(userIdKey, savedId);
                editor.putString(ifLoggedKey, "y");
                editor.commit();
                System.out.println("sp save");
                ifLogged = true;
            }

        }else{ // !ifNewlyLogged

            //sp에서 로그인정보 가져와서 set ifLogged
            String IfLoggedSP = sharedpreferences.getString(ifLoggedKey,"n");
            ifLogged = !IfLoggedSP.equals("n");
            String userIdSP = sharedpreferences.getString(userIdKey,null);
            Toast.makeText(MainActivity.this, "sp "+userIdSP, Toast.LENGTH_SHORT).show();
            System.out.println("already logged in");

        }

        if (ifLogged){
            btn_login.setVisibility(View.GONE);

        }else { //set login button onClick method
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this,LogInActivity.class);
                    startActivity(i);
                }
            });
        }
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
                JSONObject item = results.getJSONObject(0); //NullPointerException
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





}
