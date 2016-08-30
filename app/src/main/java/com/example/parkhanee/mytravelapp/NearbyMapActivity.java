package com.example.parkhanee.mytravelapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


public class NearbyMapActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap map;
    private Double lat;
    private Double lgt;
    private int radius;
    private int cat;
    ArrayList<String> titleArrayList = new ArrayList<>();
    ArrayList<Integer> catArrayList = new ArrayList<>();
    ArrayList<String> yArrayList = new ArrayList<>();
    ArrayList<String> xArrayList = new ArrayList<>();
    ArrayList<Integer> distArrayList = new ArrayList<>();
    String apiKey;
    URL apiREQ;
    ProgressDialog dialog;
    private int pageNo=0;
    Boolean loadMore=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token only needs to be configured once in your app
        MapboxAccountManager.start(this, getString(R.string.accessToken));

        // This contains the MapView in XML and needs to be called after the account manager
        setContentView(R.layout.activity_nearby_map);
        Intent i = getIntent();

        lat = i.getDoubleExtra("lat",0.0);
        lgt = i.getDoubleExtra("lgt",0.0);
        radius = i.getIntExtra("radius",0);
        cat = i.getIntExtra("cat",0);

        dialog = new ProgressDialog(NearbyMapActivity.this);
        apiKey = getString(R.string.travelApiKey);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        // Create an Icon object for the marker to use  // this process takes too long time.....
        /*IconFactory iconFactory = IconFactory.getInstance(NearbyMapActivity.this);
        Drawable iconDrawable = ContextCompat.getDrawable(NearbyMapActivity.this, R.drawable.purple_marker);
        iconDrawable.setBounds(0,0,10,10);
        final Icon icon = iconFactory.fromDrawable(iconDrawable);*/

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                new MyAsyncTask().execute(); // run the first asyncTask

                map.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(lat,lgt))
                        .zoom(13)   //.bearing(300)//.tilt(30)
                        .build());
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lgt))
                        .title("내 위치")
                        //.icon(icon)
                );

                //TODO: category에 따라 색깔이 다른 marker icon사용

                map.setOnInfoWindowClickListener(new MapboxMap.OnInfoWindowClickListener() {
                    @Override
                    public boolean onInfoWindowClick(@NonNull Marker marker) {
                        int id = (int)marker.getId();
                        //TODO : make onclick method to go to NearbyD3Activity
                        return false;
                    }
                });
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

    private class MyAsyncTask extends AsyncTask<Void,JSONObject,Void>{

        @Override
        protected void onPreExecute() {
            dialog.setMessage("로딩중입니다");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (loadMore){
                new MyAsyncTask().execute();
            }else{
                for (int i=0;i<titleArrayList.size();i++){
                    Double y = Double.parseDouble(yArrayList.get(i));
                    Double x = Double.parseDouble(xArrayList.get(i));
                    String category;
                    switch (catArrayList.get(i)){
                        case 12 : category="관광지";
                            break;
                        case 39 : category="음식점";
                            break;
                        case 32 : category="숙박";
                            break;
                        case 14: category = "문화시설";
                            break;
                        case 15 :category="행사|공연|축제";
                            break;
                        case 25 : category="여행코스";
                            break;
                        case 28 : category="레포츠";
                            break;
                        case 38 : category="쇼핑";
                            break;
                        default:category="기타";
                            break;
                    }

                    map.addMarker(new MarkerViewOptions()
                            .position(new LatLng(y,x))
                            .title(titleArrayList.get(i))
                            .snippet(category)
                    );

                }
                if (dialog.isShowing()) {
                dialog.dismiss();
                }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            pageNo ++;
            String  inputLine;
            String result="";
            BufferedReader in;
            JSONObject body=null;

            try {
                if (cat==-1){ //전체 카체고리
                    apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&arrange=E&contentTypeId=&mapX=" + lgt + "&mapY=" + lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo="+pageNo+"&MobileOS=Android&MobileApp=TestApp&_type=json");
                }else{
                    apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&arrange=E&contentTypeId=" + cat + "&mapX=" + lgt + "&mapY=" + lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo="+pageNo+"&MobileOS=Android&MobileApp=TestApp&_type=json");
                }

                in = new BufferedReader(
                        new InputStreamReader(apiREQ.openStream()));
                while ((inputLine = in.readLine()) != null)
                    result = inputLine;
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONObject object = new JSONObject(result);
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
            JSONArray itemArray=null;
            JSONObject itemObject = null;
            JSONObject body = values[0];
            String totalCount="";

            try {
                JSONObject items = body.getJSONObject("items");
                Object item = items.get("item");
                if (item instanceof JSONArray) {// It's an array
                    itemArray = (JSONArray)item;
                } else if (item instanceof JSONObject) {// It's an object
                    itemObject = (JSONObject)item;
                }
                totalCount = body.getString("totalCount");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //paging 처리
            int tc = Integer.parseInt(totalCount);

            if (tc%12==0){ //totalCount = 12개,24개, ...
                loadMore = tc/12 >pageNo;
            }else{ //totalCount = 15개, 27개, ...
                loadMore = tc/12 >=pageNo;
            }
            try {
                if (itemArray!=null){
                    for(int i=0; i < itemArray.length(); i++){
                        JSONObject poi = itemArray.getJSONObject(i);
                        String title = poi.getString("title");
                        String mapy = poi.getString("mapy");
                        String mapx = poi.getString("mapx");
                        int contentTypeId = poi.getInt("contenttypeid");
                        int dist = poi.getInt("dist");

                        //set array-lists of poi
                        titleArrayList.add(title);
                        yArrayList.add(mapy);
                        xArrayList.add(mapx);
                        catArrayList.add(contentTypeId);
                        distArrayList.add(dist);
                    }
                }else if (itemObject!=null){ //when there is only one item
                    JSONObject poi = itemObject;
                    String title = poi.getString("title");
                    String mapy = poi.getString("mapy");
                    String mapx = poi.getString("mapx");
                    int contentTypeId = poi.getInt("contenttypeid");
                    int dist = poi.getInt("dist");

                    //set array-lists of poi
                    titleArrayList.add(title);
                    yArrayList.add(mapy);
                    xArrayList.add(mapx);
                    catArrayList.add(contentTypeId);
                    distArrayList.add(dist);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

