package com.example.parkhanee.mytravelapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by parkhanee on 2016. 9. 5..
 */
public class MainContentFragment extends Fragment implements
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

    View a;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_main_content,container,false);
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        a = view.findViewById(R.id.fragment_fb);
        btn_search = (Button)view.findViewById(R.id.button);
        btn_folder = (Button) view.findViewById(R.id.button2);
        location = (TextView) view.findViewById(R.id.textView2);

        a.setVisibility(View.GONE); //hide facebook login fragment

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        tv_username = (TextView) findViewById(R.id.tv_test);
//        tv_login = (TextView) findViewById(R.id.tv_test2);
//        iv_icon = (ImageView) findViewById(R.id.icon);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(com.google.android.gms.location.LocationServices.API)
                    .build();
        }

    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                Intent i = new Intent(getContext(),NearbyD1Activity.class);
                i.putExtra("lat",lat);
                i.putExtra("lgt",lgt);
                startActivity(i);
            }
        });

        btn_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (false){ //TODO ifLogged
                    //TODO : go to travel folder activity
                }else {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                    adb.setTitle("로그인이 필요한 서비스 입니다");
                    adb.setIcon(android.R.drawable.ic_dialog_alert);
                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getContext(),LogInActivity.class);
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

    private class getGeoCode extends AsyncTask<String,Void,String> {

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
}
