package com.example.parkhanee.mytravelapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


public class NearbyD2Activity extends AppCompatActivity  {

    int radius;
    int cat;
    String strRadius;
    String strCat;
    TextView tv;
    Button mapBtn;
    Button settingBtn;
    TextView tvTotalCount;

    String apiKey;
    URL apiREQ;

    private ListView listView;
    private myArrayListAdapter myAdapter;
    ProgressDialog dialog;
    int pageNo = 0;
    Button btnLoadMore;

    Double lat;
    Double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_d2);
        Intent intent = getIntent();
        radius = intent.getIntExtra("radius", 500);
        cat = intent.getIntExtra("cat", -1);
        strRadius = intent.getStringExtra("strRadius");
        strCat = intent.getStringExtra("strCat");
        lng = intent.getDoubleExtra("lng",0.0);
        lat = intent.getDoubleExtra("lat",0.0);


        tv = (TextView) findViewById(R.id.textView4);
        tv.setText(strCat + " • " + strRadius);
        tvTotalCount = (TextView) findViewById(R.id.totalCount);
        dialog = new ProgressDialog(NearbyD2Activity.this);

        mapBtn = (Button) findViewById(R.id.button5);
        settingBtn = (Button) findViewById(R.id.button4);
        apiKey = getString(R.string.travelApiKey);
        myAdapter = new myArrayListAdapter(NearbyD2Activity.this);
        listView = (ListView) findViewById(R.id.listView);

        btnLoadMore = new Button(this);
        btnLoadMore.setText("더 불러오기");
        listView.addFooterView(btnLoadMore);
        listView.setAdapter(myAdapter);

        new URLReader().execute(radius,cat);

        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new URLReader().execute(radius,cat);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(NearbyD2Activity.this, NearbyMapActivity.class);
                i.putExtra("radius", radius);
                i.putExtra("cat", cat);
                i.putExtra("lat",lat);  // my location
                i.putExtra("lng",lng);
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

                try {
                    if (cat==-1){
                        apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&arrange=E&contentTypeId=&mapX=" + lng + "&mapY=" + lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo="+pageNo+"&MobileOS=Android&MobileApp=TestApp&_type=json");
                    }else{
                        apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&arrange=E&contentTypeId=" + cat + "&mapX=" + lng + "&mapY=" + lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo="+pageNo+"&MobileOS=Android&MobileApp=TestApp&_type=json");
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
                JSONObject items = body.getJSONObject("items"); //TODO: occurs null pointer error here, when it takes longer time to load ?왜..
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
                    for(int i=0; i < itemArray.length(); i++){
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
                        int contentTypeId = poi.getInt("contenttypeid");
                        int contentId = poi.getInt("contentid");

                        //Set ListView Items
                        String desc = "description";
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
                    String desc = "description";
                    myAdapter.addItem(new Item(contentTypeId,title,img,desc,dist,mapy,mapx,contentId));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}