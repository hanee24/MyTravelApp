package com.example.parkhanee.mytravelapp;

import android.Manifest;
import android.app.ProgressDialog;
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
    TextView tvTotalCount;

    String apiKey;
    Location myLocation;
    URL apiREQ;
   // AsyncTask task;

    GoogleApiClient mGoogleApiClient;

    private ListView listView;
    private myArrayListAdapter myAdapter;
    ProgressDialog dialog;

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
        tvTotalCount = (TextView) findViewById(R.id.totalCount);
        dialog = new ProgressDialog(NearbyD2Activity.this);

        mapBtn = (Button) findViewById(R.id.button5);
        settingBtn = (Button) findViewById(R.id.button4);
        apiKey = getString(R.string.travelApiKey);
        myAdapter = new myArrayListAdapter(NearbyD2Activity.this);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(myAdapter);

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

        new URLReader().execute(radius,cat);

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

    private class URLReader extends AsyncTask<Integer, JSONObject, Void> {

        @Override
        protected void onPreExecute() {
            dialog.setMessage("로딩중입니다");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            myAdapter.notifyDataSetChanged();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Integer... params) {

            // int i=0;
            int radius = params[0];
            int cat = params[1];

            String  inputLine;
            String result="";
            BufferedReader in;
            JSONObject body=null;

            //while (i==0) {    //AsyncTask runs once
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
                        apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&arrange=E&contentTypeId=&mapX=" + Lgt + "&mapY=" + Lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo=1&MobileOS=Android&MobileApp=TestApp&_type=json");
                    }else if (cat ==-2){
                        //cat 여러개?
                    }else{
                        apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&arrange=E&contentTypeId=" + cat + "&mapX=" + Lgt + "&mapY=" + Lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo=1&MobileOS=Android&MobileApp=TestApp&_type=json");
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
            //}
            return null;
        }

        @Override
        protected void onProgressUpdate(JSONObject... values) {
            JSONArray item = null;
            JSONObject body = values[0];
            String totalCount = "";


            try {
                JSONObject items = body.getJSONObject("items");
                item = items.getJSONArray("item");
                totalCount = body.getString("totalCount");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            tvTotalCount.setText(totalCount);

            String img;
            try {
                for(int i=0; i < item.length(); i++){ // null pointer error occurs here, when item is null !
                    JSONObject poi = item.getJSONObject(i);
                    String title = poi.getString("title");
                    String mapy = poi.getString("mapy");
                    Double y = Double.parseDouble(mapy);
                    String mapx = poi.getString("mapx");
                    Double x = Double.parseDouble(mapx);
                    if (poi.has("firstimage2")){
                        img = poi.getString("firstimage2");
                    }else{
                        img = "null";
                    }
                    int dist = poi.getInt("dist");
                    int contentTypeId = poi.getInt("contenttypeid"); // Needs "contentTypeId-Name" Array ??

                    System.out.print(i+" "+title+" ");
                    System.out.print(mapy+" ");
                    System.out.print(mapx+" ");
                    System.out.println(dist+" ");
                    System.out.println(contentTypeId);
                    System.out.println("img: "+img);


                    //Set ListView Items here
                    String desc = "description";
                    myAdapter.addItem(new Item(contentTypeId,title,img,desc,dist));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}