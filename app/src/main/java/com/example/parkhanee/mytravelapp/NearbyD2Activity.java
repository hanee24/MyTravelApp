package com.example.parkhanee.mytravelapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


public class NearbyD2Activity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    int radius;
    int cat;
    String strRadius;
    String strCat;
    TextView tv;
    Button mapBtn;
    Button settingBtn;

    String apiKey;
    Location myLocation;
    URL apiREQ;
    AsyncTask task;

    GoogleApiClient mGoogleApiClient;

    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_d2);
        Intent intent = getIntent();
        radius = intent.getIntExtra("radius", 500);
        cat = intent.getIntExtra("cat", -1);
        strRadius = intent.getStringExtra("strRadius");
        strCat = intent.getStringExtra("strCat");

        tv = (TextView) findViewById(R.id.textView4);
        tv.setText(strCat + " | " + strRadius);

        mapBtn = (Button) findViewById(R.id.button5);
        settingBtn = (Button) findViewById(R.id.button4);
        apiKey = getString(R.string.travelApiKey);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NearbyD2Activity.this, NearbyD3Activity.class);
                i.putExtra("radius", radius);
                i.putExtra("cat", cat);
                startActivity(i);
            }
        });


        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(com.google.android.gms.location.LocationServices.API)
                    .build();
        }

        task = new URLReader().execute(radius,cat);

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myLocation = com.google.android.gms.location.LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (myLocation != null) {
            Toast.makeText(NearbyD2Activity.this, String.valueOf(myLocation.getLatitude())+String.valueOf(myLocation.getLongitude()), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    private class URLReader extends AsyncTask<Integer, JSONArray, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

            int i=0;
            int radius = params[0];
            int cat = params[1];

            String  inputLine;
            String result="";
            BufferedReader in;

            while (i==0) {
                Location my = myLocation;
                while (my == myLocation) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Double Lat = myLocation.getLatitude();
                Double Lgt = myLocation.getLongitude();

                try {
                    if (cat==-1){
                        apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&contentTypeId=&mapX=" + Lgt + "&mapY=" + Lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo=1&MobileOS=Android&MobileApp=TestApp&_type=json");
                    }else if (cat ==-2){
                        //cat 여러개?
                    }else{
                        apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&contentTypeId=" + cat + "&mapX=" + Lgt + "&mapY=" + Lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo=1&MobileOS=Android&MobileApp=TestApp&_type=json");
                    }


                    in = new BufferedReader(
                            new InputStreamReader(apiREQ.openStream()));
                    while ((inputLine = in.readLine()) != null)
                        result = inputLine;
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONArray item = null;

                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject response = object.getJSONObject("response");
                    JSONObject body = response.getJSONObject("body");
                    JSONObject items = body.getJSONObject("items");
                    item = items.getJSONArray("item");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                publishProgress(item);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(JSONArray... values) {
            JSONArray item = values[0];
            System.out.println("onProgressUpdate");
            try {
                System.out.println("try");
                for(int i=0; i < item.length(); i++){
                    JSONObject poi = item.getJSONObject(i);
                    String title = poi.getString("title");
                    String mapy = poi.getString("mapy");
                    Double y = Double.parseDouble(mapy);
                    String mapx = poi.getString("mapx");
                    Double x = Double.parseDouble(mapx);
                    //String img = poi.getString("firstimage2");
                    int contentTypeId = poi.getInt("contenttypeid"); // Needs "contentTypeId-Name" Array

                    System.out.print(i+" "+title+" ");
                    System.out.print(mapy+" ");
                    System.out.print(mapx+" ");
                    System.out.println(contentTypeId);
                    //Get ListView HERE

                    adapter.add(title);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
