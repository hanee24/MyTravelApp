package com.example.parkhanee.mytravelapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.MapboxAccountManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class NearbyMapActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap map;
    private Double lat;
    private Double lgt;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token only needs to be configured once in your app
        MapboxAccountManager.start(this, getString(R.string.accessToken));

        // This contains the MapView in XML and needs to be called after the account manager
        setContentView(R.layout.activity_nearby_map);
        Intent i = getIntent();
        result = i.getStringExtra("result");
        System.out.println("result"+result);

        lat = i.getDoubleExtra("Lat",0.0);
        lgt = i.getDoubleExtra("Lgt",0.0);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState); // null

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                map.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(lat,lgt))
                        .zoom(13)
                        //.bearing(300)
                        //.tilt(30)
                        .build());
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lgt))
                        .title("내 위치"));
                new asyncTask().execute(result);
            }
        });
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private class asyncTask extends android.os.AsyncTask<String, JSONObject, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String result = strings[0];
            JSONObject body=null;
            try {
                JSONObject object = new JSONObject(result); //null
                JSONObject response = object.getJSONObject("response");
                body = response.getJSONObject("body");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            publishProgress(body);

            return null;
        }

        @Override
        protected void onProgressUpdate(JSONObject... values) {
            JSONArray itemArray = null;
            JSONObject itemObject = null;
            JSONObject body = values[0];
            int length = 0;

            try {
                JSONObject items = body.getJSONObject("items");
                Object item = items.get("item");
                if (item instanceof JSONArray) {// It's an array
                    itemArray = (JSONArray)item;
                } else if (item instanceof JSONObject) {// It's an object
                    itemObject = (JSONObject)item;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (itemArray==null){
                length = 1;
            }else{
                length = itemArray.length();
            }
                for(int i=0; i < length; i++){
                    String title="";
                    String img="";
                    Double y=0.0;
                    Double x=0.0;
                    int contentTypeId=0;

                    try {
                        JSONObject poi = itemArray.getJSONObject(i);
                        title = poi.getString("title");
                        String mapy = poi.getString("mapy");
                        y = Double.parseDouble(mapy);
                        String mapx = poi.getString("mapx");
                        x = Double.parseDouble(mapx);
                        if (poi.has("firstimage2")){
                            img = poi.getString("firstimage2");
                        }else{
                            img = "null";
                        }
                        contentTypeId = poi.getInt("contenttypeid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    System.out.println("title"+title);
                    System.out.print("y,x = "+y); System.out.println(x);
                    System.out.println("img "+img);
                    map.addMarker(new MarkerOptions()
                        .title(title)
                        .position(new LatLng(y,x)));
                }

        }
    }
}
