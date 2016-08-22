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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


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

    GoogleApiClient mGoogleApiClient;

    private ListView listView;
    private myArrayListAdapter myAdapter;
    ProgressDialog dialog;
    int pageNo = 0;
    Button btnLoadMore;

    Double Lat;
    Double Lgt;

    //String resultPassToMap = "";

    //arrayLists for passing poi data to MapActivity
    ArrayList<String> titleArrayList = new ArrayList<>();
    ArrayList<Integer> catArrayList = new ArrayList<>();
    ArrayList<String> imgArrayList = new ArrayList<>();
    ArrayList<String> yArrayList = new ArrayList<>();
    ArrayList<String> xArrayList = new ArrayList<>();

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
        tv.setText(strCat + " • " + strRadius);
        tvTotalCount = (TextView) findViewById(R.id.totalCount);
        dialog = new ProgressDialog(NearbyD2Activity.this);

        mapBtn = (Button) findViewById(R.id.button5);
        settingBtn = (Button) findViewById(R.id.button4);
        apiKey = getString(R.string.travelApiKey);
        myAdapter = new myArrayListAdapter(NearbyD2Activity.this);
        listView = (ListView) findViewById(R.id.listView);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(com.google.android.gms.location.LocationServices.API)
                    .build();
        }

        btnLoadMore = new Button(this);
        btnLoadMore.setText("더 불러오기");
        listView.addFooterView(btnLoadMore);
        listView.setAdapter(myAdapter);

        new URLReader().execute(radius,cat);

        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new URLReader().execute(radius,cat);
                // Starting a new async task
               /* try {
                    resultPassToMap = new URLReader().execute(radius,cat).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }*/
            }
        });
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
        /*if (myLocation != null) {
            Toast.makeText(NearbyD2Activity.this, String.valueOf(myLocation.getLatitude())+String.valueOf(myLocation.getLongitude()), Toast.LENGTH_SHORT).show();
        }*/


        Lat = myLocation.getLatitude();
        Lgt = myLocation.getLongitude();


        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0;i<myAdapter.getCount();i++){
                    Item a = (Item)myAdapter.getItem(i);
                    titleArrayList.add(i,a.getTitle());
                    catArrayList.add(i,a.getCat());
                    imgArrayList.add(i,a.getPicture());
                    yArrayList.add(i,a.getMapy());
                    xArrayList.add(i,a.getMapx());
                }

                Intent i = new Intent(NearbyD2Activity.this, NearbyMapActivity.class);
                i.putExtra("radius", radius);
                i.putExtra("cat", cat);
                i.putExtra("Lat",Lat);  // my location
                i.putExtra("Lgt",Lgt);
                i.putExtra("title",titleArrayList);
                i.putExtra("cat",catArrayList);
                i.putExtra("img",imgArrayList);
                i.putExtra("mapy",yArrayList);
                i.putExtra("mapx",xArrayList);
                startActivity(i);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item a = (Item)myAdapter.getItem(i);
                int contentId = a.getContentId();
                int cat = a.getCat();
                int dist = a.getDist();
                Intent intent = new Intent(NearbyD2Activity.this,NearbyD3Activity.class);
                intent.putExtra("contentId",contentId);
                intent.putExtra("cat",cat);
                intent.putExtra("dist",dist);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class URLReader extends AsyncTask<Integer, JSONObject, String> {

        @Override
        protected void onPreExecute() {
            dialog.setMessage("로딩중입니다");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String aVoid) {
            myAdapter.notifyDataSetChanged();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected String doInBackground(Integer... params) {

            pageNo = pageNo + 1;
            // int i=0;
            int radius = params[0];
            int cat = params[1];

            String  inputLine;
            String result="";
            BufferedReader in;
            JSONObject body=null;

            //AsyncTask runs once  // while (i==0) {
            if (pageNo==1){
                Location my = myLocation;
                while (my == myLocation) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


                try {
                    if (cat==-1){
                        apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&arrange=E&contentTypeId=&mapX=" + Lgt + "&mapY=" + Lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo="+pageNo+"&MobileOS=Android&MobileApp=TestApp&_type=json");
                    }else{
                        apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&arrange=E&contentTypeId=" + cat + "&mapX=" + Lgt + "&mapY=" + Lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo="+pageNo+"&MobileOS=Android&MobileApp=TestApp&_type=json");
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
            return result;
        }

        @Override
        protected void onProgressUpdate(JSONObject... values) {
            JSONArray itemArray = null;
            JSONObject itemObject = null; //in case the result has only one item
            JSONObject body = values[0];
            String totalCount = "";

            try {
                JSONObject items = body.getJSONObject("items"); //TODO: occurs null pointer error when it takes longer time to load ?왜..
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

            if (totalCount.equals("")){ //조건에 맞는 아이템 없음 --> 에러처리 하기
                totalCount = "0";
                btnLoadMore.setVisibility(View.GONE);
            }else{
                int tc = Integer.parseInt(totalCount);
                if (tc >12){ // totalCount 12 이상
                    btnLoadMore.setVisibility(View.VISIBLE);
                    if (tc/12 <= pageNo && tc%12==0){ //totalCount = 12개,24개, ...
                        btnLoadMore.setVisibility(View.GONE);
                    }else if(tc/12 < pageNo&& tc%12>0){ //totalCount = 15개, 27개, ...
                        btnLoadMore.setVisibility(View.GONE);
                    }
                }else{ //totalCount 12 이하
                    btnLoadMore.setVisibility(View.GONE);
                }
            }
            tvTotalCount.setText(totalCount);

            String img;
            try {
                if (itemObject == null&&itemArray==null) {
                    // exit asyncTask
                }else if (itemArray!=null){
                    for(int i=0; i < itemArray.length(); i++){ // null pointer error occurs here, when item is null !
                        JSONObject poi = itemArray.getJSONObject(i);
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
                        int contentId = poi.getInt("contentid");

                        //Set ListView Items
                        String desc = "description"; //poi.getString("overview");
                        myAdapter.addItem(new Item(contentTypeId,title,img,desc,dist,mapy,mapx,contentId));

                    }
                } else {
                    JSONObject poi = itemObject;
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
                    int contentTypeId = poi.getInt("contenttypeid");
                    int contentId = poi.getInt("contentid");
                    //Set ListView Items
                    String desc = "description"; //poi.getString("overview");
                    myAdapter.addItem(new Item(contentTypeId,title,img,desc,dist,mapy,mapx,contentId));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}