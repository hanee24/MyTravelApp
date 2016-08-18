package com.example.parkhanee.mytravelapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.ArrayList;


public class NearbyMapActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap map;
    private Double lat;
    private Double lgt;
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
                map.addMarker(new MarkerOptions() //TODO: 내위치 마커 - 글씨없이 아이콘 쓰기
                        .position(new LatLng(lat,lgt))
                        .title("내 위치"));
                for (int i=0; i<titleArrayList.size(); i++){
                    Double y = Double.parseDouble(yArrayList.get(i));
                    Double x = Double.parseDouble(xArrayList.get(i));
                    map.addMarker(new MarkerViewOptions()
                    .position(new LatLng(y,x))
                    .title(titleArrayList.get(i))
                    //.snippet(String.valueOf(catArrayList.get(i))) //TODO: category에 따라 색깔이 다른 marker icon사용
                    );
                }
                map.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {

                    @Nullable
                    @Override
                    public View getInfoWindow(@NonNull Marker marker) {
                        // container
                        LinearLayout parent = new LinearLayout(NearbyMapActivity.this);
                        parent.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        parent.setBackgroundColor(getResources().getColor(R.color.myWhite));
                        parent.setOrientation(LinearLayout.VERTICAL);

                        ImageView imageView = new ImageView(NearbyMapActivity.this);
                        TextView tvTitle = new TextView(NearbyMapActivity.this);

                        for (int i=0; i<titleArrayList.size(); i++){

                            String img = imgArrayList.get(i);

                            if (marker.getTitle().equals(titleArrayList.get(i))){
                                if (!img.equals("null")){
                                    Picasso.with(NearbyMapActivity.this).load(img).into(imageView);
                                }else{
                                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.compass));
                                }
                                tvTitle.setText(titleArrayList.get(i));
                            }
                        }

                        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
                        tvTitle.setLayoutParams(new android.view.ViewGroup.LayoutParams(300,ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvTitle.setBackgroundColor(getResources().getColor(R.color.myWhite));

                        // Set the size of the image
                        imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 230));

                        // add the image view to the parent layout
                        parent.addView(imageView);
                        parent.addView(tvTitle);

                        return parent;
                    }
                });

                map.setOnInfoWindowClickListener(new MapboxMap.OnInfoWindowClickListener() {
                    @Override
                    public boolean onInfoWindowClick(@NonNull Marker marker) {
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
}
