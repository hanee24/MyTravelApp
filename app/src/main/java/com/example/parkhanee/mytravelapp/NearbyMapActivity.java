package com.example.parkhanee.mytravelapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
    private Double lng;
    private int radius;
    private int cat;
    ArrayList<Item> items = new ArrayList<>();
    String apiKey;
    URL apiREQ;
    ProgressDialog dialog;
    private int pageNo=0;
    Boolean loadMore=true;
    private String TAG = "NearbyMapActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token only needs to be configured once in your app
        MapboxAccountManager.start(this, getString(R.string.mapBoxAccessToken));

        // This contains the MapView in XML and needs to be called after the account manager
        setContentView(R.layout.activity_nearby_map);
        Intent i = getIntent();

        lat = i.getDoubleExtra("lat",0.0);
        lng = i.getDoubleExtra("lng",0.0);
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
                new MyAsyncTask().execute(); // run the first asyncTask (pageNo==1)

                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(lat,lng))
                        .zoom(13)
                        .build());

                // Create an Icon object for the marker to use
                        IconFactory iconFactory = IconFactory.getInstance(NearbyMapActivity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(NearbyMapActivity.this, R.drawable.purple_marker_20);
                Icon icon = iconFactory.fromDrawable(iconDrawable);

                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .title("내 위치")
                        .icon(icon)
                );

                //TODO: category에 따라 색깔이 다른 marker icon사용

                map.setOnInfoWindowClickListener(new MapboxMap.OnInfoWindowClickListener() {
                    @Override
                    public boolean onInfoWindowClick(@NonNull Marker marker) {
                        int id = (int)marker.getId();
                        if (id!=0){ // id==0이면 내위치 infowindow를 누른 것
                            // go to NearbyD3Activity
                            Intent i = new Intent(NearbyMapActivity.this,NearbyD3Activity.class);
                            i.putExtra("contentId",items.get(id-1).getContentId());
                            startActivity(i);
                            return true;
                        }
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
                for (int i=0;i<items.size();i++){
                    Double y = Double.parseDouble(items.get(i).getMapy());
                    Double x = Double.parseDouble(items.get(i).getMapx());
                    String category;
                    switch (items.get(i).getCat()){
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

                    Log.d("map", "onPostExecute: "+y+" "+x+items.get(i).getTitle());

                    map.addMarker(new MarkerViewOptions()
                            .position(new LatLng(y,x))
                            .title(items.get(i).getTitle())
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
                    apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&arrange=E&contentTypeId=&mapX=" + lng + "&mapY=" + lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo="+pageNo+"&MobileOS=Android&MobileApp=TestApp&_type=json");
                }else{
                    apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&arrange=E&contentTypeId=" + cat + "&mapX=" + lng + "&mapY=" + lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo="+pageNo+"&MobileOS=Android&MobileApp=TestApp&_type=json");
                }

                Log.d("map", "doInBackground: "+apiREQ);

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
            int tc;
            if (totalCount.equals("")){
                tc=0;
            }else {
                tc = Integer.parseInt(totalCount);
            }


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
                        int contentId = poi.getInt("contentid");

                        //set array-list of poi
                        items.add(new Item(contentTypeId,title,"","",dist,mapy,mapx,contentId));
                    }
                }else if (itemObject!=null){ //when there is only one item
                    JSONObject poi = itemObject;
                    String title = poi.getString("title");
                    String mapy = poi.getString("mapy");
                    String mapx = poi.getString("mapx");
                    int contentTypeId = poi.getInt("contenttypeid");
                    int dist = poi.getInt("dist");
                    int contentId = poi.getInt("contentid");

                    //set array-list of poi
                    items.add(new Item(contentTypeId,title,"","",dist,mapy,mapx,contentId));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

