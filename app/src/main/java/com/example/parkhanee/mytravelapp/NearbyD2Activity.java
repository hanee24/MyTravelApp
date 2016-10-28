package com.example.parkhanee.mytravelapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;


public class NearbyD2Activity extends AppCompatActivity  {

    int radius;
    int cat;
    String strRadius;
    String strCat;
    TextView tvDesc;
    Button mapBtn;
    ImageButton settingBtn;
    TextView tvTotalCount;
    TextView tvTitle;

    String apiKey;

    private ListView listView;
    private myArrayListAdapter myAdapter;
    ProgressDialog dialog;
    int pageNo = 0;
    Button btnLoadMore;

    Double lat;
    Double lng;

    Boolean isNearby;
    int areaCode;
    String area;
    int sigunguCode;
    String sigungu;
    private String TAG = "NearbyD2Activity";

    int radiusTemp, catTemp;
    String orderTemp; // Temp variables are used as temporary values in the search-setting dialog
    String strRadiusTemp, strCatTemp, strOrderTemp;
    String strOrder, order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_d2);

        tvDesc = (TextView) findViewById(R.id.textView4);
        tvTitle = (TextView) findViewById(R.id.textView3);

        tvTotalCount = (TextView) findViewById(R.id.totalCount);
        dialog = new ProgressDialog(NearbyD2Activity.this);

        Intent intent = getIntent();
        isNearby = intent.getBooleanExtra("isNearby",true);
        Toast.makeText(NearbyD2Activity.this, "isNearby "+isNearby, Toast.LENGTH_SHORT).show();

        mapBtn = (Button) findViewById(R.id.button5);
        settingBtn = (ImageButton) findViewById(R.id.button4);
        apiKey = getString(R.string.travelApiKey);
        myAdapter = new myArrayListAdapter(NearbyD2Activity.this,isNearby);
        listView = (ListView) findViewById(R.id.listView);

        btnLoadMore = new Button(this);
        btnLoadMore.setText("더 불러오기");
        listView.addFooterView(btnLoadMore);
        listView.setAdapter(myAdapter);

        if (isNearby){
            radius = intent.getIntExtra("radius", 500);
            cat = intent.getIntExtra("cat", -1);
            strRadius = intent.getStringExtra("strRadius");
            strCat = intent.getStringExtra("strCat");
            strOrder="거리순";
            order = "E"; //거리순
            lng = intent.getDoubleExtra("lng",0.0);
            lat = intent.getDoubleExtra("lat",0.0);
            tvDesc.setText(strCat + " • " + strRadius);
            tvTitle.setText("주변 탐색");


            new URLReader().execute(radius,cat);

            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    new URLReader().execute(radius,cat);
                }
            });
        } else {
            cat = -1;
            strCat = "전체";
            strOrder="제목순";
            order = "A"; //제목순
            area = intent.getStringExtra("area");
            switch (area){
                case "서울" : areaCode = 1;
                    break;
                case "인천" : areaCode = 2;
                    break;
                case "대전" : areaCode = 3;
                    break;
                case "대구" : areaCode = 4;
                    break;
                case "광주" : areaCode = 5;
                    break;
                case "부산" : areaCode = 6;
                    break;
                case "울산" : areaCode = 7;
                    break;
                case "세종특별자치시" : areaCode = 8;
                    break;
                case "경기도" : areaCode = 31;
                    break;
                case "강원도" : areaCode = 32;
                    break;
                case "충청북도" : areaCode = 33;
                    break;
                case "충청남도" : areaCode = 34;
                    break;
                case "경상북도" : areaCode = 35;
                    break;
                case "경상남도" : areaCode = 36;
                    break;
                case "전라북도" : areaCode = 37;
                    break;
                case "전라남도" : areaCode = 38;
                    break;
                case "제주도" : areaCode = 39;
                    break;
                default: areaCode = 0;
                    break;
            }
            sigunguCode = intent.getIntExtra("sigunguCode",0);
            sigungu = intent.getStringExtra("sigungu");

            tvTitle.setText("지역 검색");
            tvDesc.setText(area+" "+sigungu);
            new URLReader().execute(areaCode,sigunguCode);

            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    new URLReader().execute(areaCode,sigunguCode);
                }
            });
        }

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
                Item a = (Item) myAdapter.getItem(i);
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

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(NearbyD2Activity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_search_setting);

                final Spinner orderSpn = (Spinner) dialog.findViewById(R.id.spinner3);
                final Spinner catSpn = (Spinner) dialog.findViewById(R.id.spinner4);
                final Spinner distSpn = (Spinner) dialog.findViewById(R.id.spinner5);

                // 2016. 10. 28. isNearby 따라서 정렬기준 orderSpinner item 바뀌도록
                final ArrayList<String> items = new ArrayList<>();
                if (isNearby){
                    distSpn.setEnabled(true);
                    distSpn.setClickable(true);
                    Collections.addAll(items,getResources().getStringArray(R.array.order_nearby));
                } else {
                    distSpn.setEnabled(false); // 지역검색 일때는 반경 스피너를 disable
                    distSpn.setClickable(false);
                    Collections.addAll(items,getResources().getStringArray(R.array.order_area));
                }
                ArrayAdapter orderAdapter = new ArrayAdapter<>(NearbyD2Activity.this,R.layout.support_simple_spinner_dropdown_item,items);
                orderSpn.setAdapter(orderAdapter);

                // set default item for spinners
                // 원래 str들에 저장되어 있는 값에 해당되는 스피너 아이템을 Adapter통해서 position을 찾아서 setSelection
                orderSpn.setSelection(orderAdapter.getPosition(strOrder));
                int position=((ArrayAdapter)catSpn.getAdapter()).getPosition(strCat);
                catSpn.setSelection(position);
                if (isNearby){
                    position = ((ArrayAdapter)distSpn.getAdapter()).getPosition(strRadius);
                    distSpn.setSelection(position);
                }



                // 2016. 10. 28.  set on item selected listener for each spinner
                orderSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        strOrderTemp = (String) adapterView.getItemAtPosition(i);
                        if (!isNearby){ i ++; }
                        switch (i){ //(A=제목순, B=조회순, C=수정일순, D=생성일순, E=거리순)
                            case 0 : //거리순
                                orderTemp = "E";
                                break;
                            case 1 : //제목순
                                orderTemp = "A";
                                break;
                            case 2 : //조회순
                                orderTemp = "B";
                                break;
                            case 3:  //수정일
                                orderTemp = "C";
                                break;
                            case 4 : //생성일순
                                orderTemp = "D";
                                break;
                            default:
                                orderTemp = order;
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        strOrderTemp = strOrder;
                        orderTemp = order;
                    }
                });

                catSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        strCatTemp = (String) adapterView.getItemAtPosition(i);
                        switch (i){
                            case 0 : catTemp=-1;
                                break;
                            case 1 : catTemp=12;
                                break;
                            case 2 : catTemp=39;
                                break;
                            case 3 : catTemp=32;
                                break;
                            case 4 : catTemp=15;
                                break;
                            case 5 : catTemp = 25;
                                break;
                            default: catTemp = cat;
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        strCatTemp = strCat;
                        catTemp = cat;
                    }
                });

                distSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        strRadiusTemp = (String) adapterView.getItemAtPosition(i);
                        switch (i){
                            case 0 : radiusTemp = 500;
                                break;
                            case 1: radiusTemp = 750;
                                break;
                            case 2 : radiusTemp = 1000;
                                break;
                            case 3 : radiusTemp = 2000;
                                break;
                            case 4 : radiusTemp = 3000;
                                break;
                            default: radiusTemp = radius;
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        strRadiusTemp = strRadius;
                        radiusTemp = radius;
                    }
                });

                // OK button Clicked
                Button dialogButton = (Button) dialog.findViewById(R.id.button10);
                dialogButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageNo = 0;
                        myAdapter.clearItem();
                        // 2016. 10. 28.  apply the changed setting
                        if (isNearby){
                            radius = radiusTemp;
                            strRadius = strRadiusTemp;
                            cat = catTemp;
                            strCat = strCatTemp;
                            order = orderTemp;
                            strOrder = strOrderTemp;

                            tvDesc.setText(strCat + " • " + strRadius);
                            new URLReader().execute(radius,cat);

                            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    new URLReader().execute(radius,cat);
                                }
                            });
                        }else {
                            order = orderTemp;
                            strOrder = strOrderTemp;
                            cat = catTemp;
                            strCat = strCatTemp;

                            new URLReader().execute(areaCode,sigunguCode);

                            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    new URLReader().execute(areaCode,sigunguCode);
                                }
                            });
                        }

                        dialog.dismiss();
                    }
                });

                // Cancel Button Clicked
                Button button = (Button) dialog.findViewById(R.id.button11);
                button.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
    }


    private class URLReader extends AsyncTask<Integer, JSONObject, String> {
        URL apiREQ;

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

            String  inputLine;
            String result="";
            BufferedReader in;
            JSONObject body=null;

                try {
                    if (isNearby){
                        int radius = params[0];
                        int cat = params[1];
                        if (cat==-1){
                            apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&arrange="+order+"&contentTypeId=&mapX=" + lng + "&mapY=" + lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&numOfRows=12&pageNo="+pageNo+"&MobileOS=Android&MobileApp=TestApp&_type=json");
                        }else{
                            apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey=" + apiKey + "&arrange="+order+"&contentTypeId=" + cat + "&mapX=" + lng + "&mapY=" + lat + "&radius=" + radius + "&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&numOfRows=12&pageNo="+pageNo+"&MobileOS=Android&MobileApp=TestApp&_type=json");
                        }
                    }else {
                        int areaCode = params[0];
                        int sigunguCode = params[1];
                        if (cat==-1){
                            apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=" + apiKey +"&areaCode="+areaCode+"&sigunguCode="+sigunguCode+"&arrange="+order+"&contentTypeId=&numOfRows=12&pageNo="+pageNo+"&MobileOS=ETC&MobileApp=AppTesting&_type=json");
                        }else {
                            apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=" + apiKey +"&areaCode="+areaCode+"&sigunguCode="+sigunguCode+"&arrange="+order+"&contentTypeId="+cat+"&numOfRows=12&pageNo="+pageNo+"&MobileOS=ETC&MobileApp=AppTesting&_type=json");
                        }

                    }

                    Log.d(TAG, "doInBackground: apiREQ "+apiREQ);
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
                        String mapy , mapx;
                        if (poi.has("mapy")){
                            mapy = poi.getString("mapy");
                            mapx = poi.getString("mapx");
                        }else{
                            mapy = "0";
                            mapx = "0";
                        }

                        Double y = Double.parseDouble(mapy);
                        Double x = Double.parseDouble(mapx);
                        if (poi.has("firstimage2")){
                            img = poi.getString("firstimage2");
                        }else{
                            img = "null";
                        }

                        int dist;
                        if (poi.has("dist")){
                            dist  = poi.getInt("dist");
                        }else {
                            dist = 0;
                        }
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
                    int dist;
                    if (poi.has("dist")){
                        dist  = poi.getInt("dist");
                    }else {
                        dist = 0;
                    }

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