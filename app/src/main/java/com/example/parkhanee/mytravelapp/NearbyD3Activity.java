package com.example.parkhanee.mytravelapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class NearbyD3Activity extends FragmentActivity { //AppCompatActivity

    int contentId=0;
    String apiKey;
    URL apiREQ;
    URL imgREQ;
    ProgressDialog dialog;
    String imageYN = "Y"; //Y=콘텐츠이미지조회   //N=”음식점”타입의음식메뉴이미지
    TextView tvTitle, tvCat, tvDist, tvOverview, tvTel, tvZipcode, tvAddr1; //, tvAddr2;
    TextView tv_tel, tv_addr, tv_addr1; //additional views
    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;
    int size=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_d3);

        //intent
        Intent i = getIntent();
        contentId = i.getIntExtra("contentId",0);
        int dist = i.getIntExtra("dist",0);
        int cat = i.getIntExtra("cat",0);
        if (cat==39){
            imageYN = "N";
        }

        System.out.println("contentId : "+contentId);
        //get api
        apiKey = getString(R.string.travelApiKey);
        try {
            apiREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey="+apiKey+"&contentId="+contentId+"&defaultYN=Y&firstImageYN=Y&addrinfoYN=Y&overviewYN=Y&MobileOS=ETC&MobileApp=AppTesting&_type=json");
            imgREQ = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailImage?ServiceKey="+apiKey+"&contentId="+contentId+"&imageYN="+imageYN+"&MobileOS=ETC&MobileApp=AppTesting&_type=json");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //define
        dialog = new ProgressDialog(NearbyD3Activity.this);
        tvTitle = (TextView) findViewById(R.id.title);
        tvCat = (TextView) findViewById(R.id.cat);
        tvDist = (TextView) findViewById(R.id.dist);
        tvOverview = (TextView) findViewById(R.id.overview);
        tvTel = (TextView) findViewById(R.id.tel);
        tvZipcode = (TextView) findViewById(R.id.zipcode);
        tvAddr1 = (TextView) findViewById(R.id.addr1);
        tv_tel = (TextView) findViewById(R.id.tv_tel);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        tv_addr1 = (TextView) findViewById(R.id.tv_addr1);

        tvDist.setText(String.valueOf(dist));
        String strCat="기타";
        switch (cat){
            case -1 : strCat="전체";
                break;
            case 12: strCat="관광지";
                break;
            case 39 : strCat="음식";
                break;
            case 32 : strCat="숙박";
                break;
            case 15 : strCat="행사|공연|축제";
                break;
            case 14: strCat = "문화시설";
                break;
            case 25: strCat = "여행코스";
                break;
            case 28 : strCat="레포츠";
                break;
            case 38 : strCat="쇼핑";
                break;
            default: strCat=String.valueOf(cat);
                break;
        }
        tvCat.setText(strCat);

        new asyncTask().execute();

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new myPagerAdapter(getSupportFragmentManager());//,size); // TODO: size should be initialized beforehand
        mViewPager.setAdapter(mPagerAdapter);
    }

    private class myPagerAdapter extends FragmentStatePagerAdapter{
        private final int size=5;

        public myPagerAdapter(FragmentManager fm) { //Add a parameter int size
            super(fm);
            //this.size = size;
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public Fragment getItem(int position) {
            //return null;
            return TextSlideFragment.newInstance(position);
              //  return ImageSlideFragment.newInstance(position);


        }
    }

    private class asyncTask extends AsyncTask<Void, JSONObject, Void>{

        @Override
        protected void onPreExecute() {
            dialog.setMessage("잡시만 기다려주세요");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int a=0;a<2;a++) {
                String inputLine;
                String result = "";
                BufferedReader in;
                JSONObject body = null;
                JSONObject header = null;
                URL request;
                if (a==0){
                    request = apiREQ;
                }else{
                    request = imgREQ;
                }
                try {
                    in = new BufferedReader(new InputStreamReader(request.openStream()));
                    while ((inputLine = in.readLine()) != null)
                        result = inputLine;
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject response = object.getJSONObject("response");
                    header = response.getJSONObject("header");
                    body = response.getJSONObject("body");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                publishProgress(header, body);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(JSONObject... values) { // it gets both imgREQ and apiREQ
            JSONObject header = values[0];
            JSONObject body = values[1];

           /* String h = header.toString();
            String b = body.toString();

            System.out.println("header : "+h);
            System.out.println("body : "+b); */

            JSONArray itemArray = null;
            JSONObject itemObject = null;
            String totalCount = "";


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

            if (totalCount.equals("")){ //조건에 맞는 아이템 없음
                totalCount = "0"; //TODO : ?

            }
            int tc = Integer.parseInt(totalCount);
            for(int i=0; i<tc ; i++){
                try {
                JSONObject poi=null;
                    if (tc == 1){
                        poi = itemObject;
                    }else if (itemArray.length()==tc){
                        poi = itemArray.getJSONObject(i);
                    }

                    if (poi.has("title")){// if poi has "title" it's from apiREQ, else it's from imgREQ
                        String title = poi.getString("title");
                        String overview = poi.getString("overview");
                        overview = overview.replace("<br>"," ");
                        overview = overview.replace("<br />"," ");
                        overview = overview.replace("&nbsp;"," ");
                        if (poi.has("tel")){
                            String tel = poi.getString("tel");
                            tvTel.setText(tel);
                        }else{
                            tvTel.setText("표시할 전화번호가 없습니다");
                            //tvTel.setVisibility(View.GONE);
                            //tv_tel.setVisibility(View.GONE);
                        }
                        tvTitle.setText(title);
                        tvOverview.setText(overview);
                        if (poi.has("zipcode")){
                            String zipcode = poi.getString("zipcode");
                            String addr1 = poi.getString("addr1");
                            if (poi.has("addr2")){
                                addr1 += poi.getString("addr2");
                            }

                            tvZipcode.setText(zipcode);
                            tvAddr1.setText(addr1);
                            //tvAddr2.setText(addr2);
                        }else{
                            tvZipcode.setVisibility(View.GONE);
                            //tvAddr2.setVisibility(View.GONE);
                            tvAddr1.setVisibility(View.GONE);
                            tv_addr1.setVisibility(View.GONE);
                            tv_addr.setText("주소 정보가 없습니다");
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        }
    }
}
