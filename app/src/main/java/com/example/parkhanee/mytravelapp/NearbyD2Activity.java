package com.example.parkhanee.mytravelapp;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


public class NearbyD2Activity extends AppCompatActivity {

    int radius;
    int cat;
    String strRadius;
    String strCat ;
    TextView tv;
    Button map;
    Button setting;

    LocationServices locationServices;
    private static final int PERMISSIONS_LOCATION = 0;
    String apiKey ;
    Location myLocation=null;
    URL apiREQ;
    AsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_d2);
        Intent intent = getIntent();
        radius = intent.getIntExtra("radius",500);
        cat = intent.getIntExtra("cat",-1);
        strRadius = intent.getStringExtra("strRadius");
        strCat = intent.getStringExtra("strCat");

        tv = (TextView) findViewById(R.id.textView4);
        tv.setText(strCat+" | "+strRadius);

        map = (Button) findViewById(R.id.button5);
        setting = (Button) findViewById(R.id.button4);
        locationServices = LocationServices.getLocationServices(NearbyD2Activity.this);
        apiKey = getString(R.string.travelApiKey);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NearbyD2Activity.this,NearbyD3Activity.class);
                i.putExtra("radius",radius);
                i.putExtra("cat",cat);
                startActivity(i);
            }
        });

        //toggleGps(true);
        if (!locationServices.areLocationPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
        }
        myLocation = locationServices.getLastLocation();
        Double Lat = myLocation.getLatitude();
        Toast.makeText(NearbyD2Activity.this, Double.toString(Lat), Toast.LENGTH_SHORT).show();
        // Async task  getPOIInfo(myLocation);
        task = new URLReader().execute(radius,cat);


    }

/*
    @UiThread
    public void toggleGps(boolean enableGps) { // set user location
        if (enableGps) {
            // Check if user has granted location permission
            if (!locationServices.areLocationPermissionsGranted()) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
        }
    }

    private void enableLocation(boolean enabled) { // set user location
        if (enabled) { //my location 켜기
            locationServices.addLocationListener(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Toast.makeText(NearbyD2Activity.this, "onLocationChanged", Toast.LENGTH_SHORT).show();
                    if (location != null) {
                        myLocation = location;
                        Double Lat = myLocation.getLatitude();
                        Toast.makeText(NearbyD2Activity.this, Double.toString(Lat), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(NearbyD2Activity.this,"location is null", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else { //my location 끄기
        }
    }
    */

    private class URLReader extends AsyncTask<Integer, JSONArray, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

            int radius = params[0];
            int cat = params[1];
            String  inputLine;
            String result="";
            BufferedReader in;



                Double Lat = myLocation.getLatitude();
                Double Lgt = myLocation.getLongitude();

                try {
                    apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&contentTypeId=" + cat + "&mapX=" + Lgt + "&mapY=" + Lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo=1&MobileOS=Android&MobileApp=TestApp&_type=json");
                    in = new BufferedReader(
                            new InputStreamReader(apiREQ.openStream()));
                    while ((inputLine = in.readLine()) != null)
                        result = inputLine;
                    in.close();
                    Toast.makeText(NearbyD2Activity.this, "api request sent successfully", Toast.LENGTH_SHORT).show();
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
            return null;
        }

        @Override
        protected void onProgressUpdate(JSONArray... values) {
            Double Lat = myLocation.getLatitude();
            Double Lgt = myLocation.getLongitude();


                Toast.makeText(NearbyD2Activity.this, Double.toString(Lat)+Double.toString(Lgt), Toast.LENGTH_SHORT).show();

            JSONArray item = values[0];
            System.out.println(item);

            try {
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

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
