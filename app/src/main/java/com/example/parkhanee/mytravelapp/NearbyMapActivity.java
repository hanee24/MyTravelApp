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

import java.util.ArrayList;


public class NearbyMapActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap map;
    private Double lat;
    private Double lgt;
    //private ArrayList<Item> itemArrayList = new ArrayList<>();
    ArrayList<String> titleArrayList = new ArrayList<>();
    ArrayList<Integer> catArrayList = new ArrayList<>();
    ArrayList<String> imgArrayList = new ArrayList<>();
    ArrayList<String> yArrayList = new ArrayList<>();
    ArrayList<String> xArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token only needs to be configured once in your app
        MapboxAccountManager.start(this, getString(R.string.accessToken));

        // This contains the MapView in XML and needs to be called after the account manager
        setContentView(R.layout.activity_nearby_map);
        Intent i = getIntent();
        //get result here
        //itemArrayList = i.getParcelableArrayListExtra("result");
        titleArrayList = i.getStringArrayListExtra("title");
        catArrayList = i.getIntegerArrayListExtra("cat");
        imgArrayList = i.getStringArrayListExtra("img");
        yArrayList = i.getStringArrayListExtra("mapy");
        xArrayList = i.getStringArrayListExtra("mapx");

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
                for (int i=0; i<titleArrayList.size(); i++){
                    Double y = Double.parseDouble(yArrayList.get(i));
                    Double x = Double.parseDouble(xArrayList.get(i));
                    map.addMarker(new MarkerOptions()
                    .position(new LatLng(y,x))
                    .title(titleArrayList.get(i))
                    .snippet(String.valueOf(catArrayList.get(i)))
                    );
                }
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
/*
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
    */
}
