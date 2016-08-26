package com.example.parkhanee.mytravelapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NearbyD3Activity extends FragmentActivity { //AppCompatActivity

    int contentId=0;
    String apiKey;
    URL apiREQ;
    URL imgREQ;
    ProgressDialog dialog;
    String imageYN = "Y"; //Y=콘텐츠이미지조회   //N=”음식점”타입의음식메뉴이미지
    TextView tvTitle, tvCat, tvDist, tvOverview, tvTel, tvZipcode, tvAddr1;
    TextView tv_tel, tv_addr, tv_addr1, tvM, tvC; //additional views
    TextView tvHomepage, tvModified, tvCreated ;

    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;
    int size=0;
    ArrayList<String> imgArrayList ;
    private ViewPagerIndicator indicator;

    Intent callingIntent;
    Button btnShowMore;
    private String shortOverview;
    private String fullOverview;
    private Boolean isShowFull;

    Boolean isImage=false;


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
        btnShowMore = (Button) findViewById(R.id.showMore);
        tvHomepage = (TextView) findViewById(R.id.homepage);
        tvModified = (TextView) findViewById(R.id.modifiedtime);
        tvCreated = (TextView) findViewById(R.id.createdtime);
        tvM = (TextView) findViewById(R.id.tv_modified);
        tvC = (TextView) findViewById(R.id.tv_created);

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

        imgArrayList = new ArrayList<>();
        imgArrayList.add(0,"null");

        indicator = (ViewPagerIndicator) findViewById(R.id.indicator);

        new asyncTask().execute();


    }

    private class myPagerAdapter extends FragmentStatePagerAdapter{
        //private final int size=5;

        public myPagerAdapter(FragmentManager fm) { //Add a parameter int size
            super(fm);
            //this.size = size;
        }

        @Override
        public int getCount() {
            return imgArrayList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return ImageSlideFragment.newInstance(position,imgArrayList);
        }
    }

    public void btnOnClick(View v){
        int position;

        switch( v.getId() ){
            case R.id.previous://pager 이전버튼 클릭

                position=mViewPager.getCurrentItem();
                mViewPager.setCurrentItem(position-1,true);
                break;

            case R.id.next://pager 다음버튼 클릭

                position=mViewPager.getCurrentItem();
                mViewPager.setCurrentItem(position+1,true);
                break;

            case R.id.showMore:
                SetOverviewDisplay(tvOverview);
                isShowFull = true; //더보기 눌렀으니까 이제 full overview 보여야지
                break;
        }
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            indicator.selectDot(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private class asyncTask extends AsyncTask<Void, JSONObject, Void>{

        @Override
        protected void onPreExecute() {
            dialog.setMessage("잠시만 기다려주세요");
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
                    //isImage=false;
                }else{
                    request = imgREQ;
                    //isImage=true;
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
            //JSONObject header = values[0];
            JSONObject body = values[1];
            JSONArray itemArray = null;
            JSONObject itemObject = null;
            String totalCount = "";
            String numOfRows="";
            //String strItems="";


            try {
                //org.json.JSONException: Value  at items of type java.lang.String cannot be converted to JSONObject //사진없을때 .
                //JSONObject items = body.getJSONObject("items");
                JSONObject items;
                Object objectItems = body.get("items");
                totalCount = body.getString("totalCount");
                numOfRows = body.getString("numOfRows");
                if (objectItems instanceof String){ //조건에 맞는 아이템 없음 //when there is no item == when there is no Image!
                    //strItems = (String) objectItems;
                    isImage=true;
                }else if(objectItems instanceof JSONObject) {
                    items = (JSONObject) objectItems;
                    Object item = items.get("item");

                    if (item instanceof JSONArray) {// It's an array
                        itemArray = (JSONArray) item;
                    } else if (item instanceof JSONObject) {// It's an object
                        itemObject = (JSONObject) item;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (totalCount.equals("")){ //조건에 맞는 아이템 없음
                totalCount = "0"; //TODO : 이미지 정보가 없는건 괜찮은데 다른 상세정보가 없으면 안돼는데? 이거 예외처리?
            }
            int tc = Integer.parseInt(totalCount);

            if (isImage){ //when there are more images than numOfRows, discard redundant images
                if (numOfRows.equals("")){
                    numOfRows="0";
                }
                int rows = Integer.parseInt(numOfRows);
                if (tc>rows){
                    tc = rows;
                }
            }

                try {
                JSONObject poi;
                    for(int i=0; i<tc ; i++) {
                        if (tc == 1) {
                            poi = itemObject;
                            System.out.println("tc==1; "+poi);
                        } else if (itemArray.length() == tc) {
                            poi = itemArray.getJSONObject(i);
                            System.out.println("poi "+isImage.toString()+poi);
                        }else{
                            poi = itemArray.getJSONObject(i);
                            System.out.println("else "+isImage.toString()+poi);
                        }

                        //String pp = poi.toString();
                        //System.out.println("pp"  + pp);

                        if (poi.has("title")) {// if poi has "title" it's from apiREQ, else it's from imgREQ

                            String title = poi.getString("title");
                            String overview = poi.getString("overview");
                            overview = overview.replace("<br>", " ");
                            overview = overview.replace("<br />", " ");
                            overview = overview.replace("&nbsp;", " ");
                            if (poi.has("tel")) {
                                String tel = poi.getString("tel");
                                callingIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+tel));
                                tvTel.setText(tel);
                                tvTel.setTextColor(Color.BLUE);
                                    tvTel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            call();
                                        }
                                    });
                            } else {
                                //tvTel.setText("표시할 전화번호가 없습니다");
                                tvTel.setVisibility(View.GONE);
                                tv_tel.setVisibility(View.GONE);
                            }
                            tvTitle.setText(title);

                            //overview textview 처리
                            //count char
                            int charOverview = overview.length();
                            //set showfull boolean
                            fullOverview = overview;
                            isShowFull = charOverview <= 200;
                            //if not showfull, get short overview
                            if (!isShowFull){
                                //get first 200 chars from overview
                                shortOverview = fullOverview.substring(0,200);
                            }
                            //set overviews
                            SetOverviewDisplay(tvOverview);


                            if (poi.has("zipcode")) {
                                String zipcode = poi.getString("zipcode");
                                String addr1 = poi.getString("addr1");
                                if (poi.has("addr2")) {
                                    addr1 += poi.getString("addr2");
                                }

                                tvZipcode.setText(zipcode);
                                tvAddr1.setText(addr1);
                                //tvAddr2.setText(addr2);
                            } else {
                                tvZipcode.setVisibility(View.GONE);
                                tvAddr1.setVisibility(View.GONE);
                                tv_addr1.setVisibility(View.GONE);
                                tv_addr.setText("주소 정보가 없습니다");
                            }

                            //homepage
                            if (poi.has("homepage")){

                                String hp = poi.getString("homepage");
                                Document doc = Jsoup.parse(hp);

                                Element link = doc.getElementsByTag("a").first();

                                final String linkHref = link.attr("href"); //href attr of the aTag
                                String linkText = link.text();

                                tvHomepage.setText(linkText); //set text for homepage textview
                                tvHomepage.setTextColor(Color.BLUE);
                                tvHomepage.setOnClickListener(new View.OnClickListener() { //set onClick method for the textview
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(linkHref));
                                        startActivity(intent);
                                    }
                                });

                            }else{
                                tvHomepage.setVisibility(View.GONE);
                            }
                            if (poi.has("createdtime")){
                                String ct = poi.getString("createdtime");
                                String year = ct.substring(0,4);
                                String month = ct.substring(4,6);
                                String day = ct.substring(6,8);
                                String aa = year+"-"+month+"-"+day;
                                tvCreated.setText(aa);
                            }else{
                                tvCreated.setVisibility(View.GONE);
                                tvC.setVisibility(View.GONE);
                            }
                            if (poi.has("modifiedtime")){
                                String mt = poi.getString("modifiedtime");
                                String year = mt.substring(0,4);
                                String month = mt.substring(4,6);
                                String day = mt.substring(6,8);
                                String aa = year+"-"+month+"-"+day;
                                tvModified.setText(aa);
                            }else {
                                tvM.setVisibility(View.GONE);
                                tvModified.setVisibility(View.GONE);
                            }

                            //first image
                            if (poi.has("firstimage")){
                                imgArrayList.clear();//In order to prevent creating extra loading image at the end
                                String url = poi.getString("firstimage");
                                imgArrayList.add(0,url);
                            }
                        } else {
                            if (imgArrayList.get(0).equals("null")){
                                imgArrayList.clear(); //In order to prevent creating extra loading image at the end
                            }
                            String url = poi.getString("originimgurl");
                            imgArrayList.add(url);
                            isImage=true;
                        }
                    }

                    if (isImage){
                        SetArrowsVisibility(true);
                        if (imgArrayList.size()==1){
                            SetArrowsVisibility(false);
                        }
                        initViewPager();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }


    private void initViewPager(){
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new myPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        int size = imgArrayList.size();
        if (size>1){
            indicator.createDot(size);
        }
    }


    private void call(){
        // if calling intent has been initialized,
        startActivity(callingIntent);
    }


    private void SetOverviewDisplay(TextView view){ //not really efficient to have parameter of the view ?? <- this method is only for overview text
        if (isShowFull){
            view.setText(fullOverview);
            btnShowMore.setVisibility(View.GONE);
        }else{
            view.setText(shortOverview);
            btnShowMore.setVisibility(View.VISIBLE);
        }
    }

    private void SetArrowsVisibility(Boolean show){
        ImageButton previous = (ImageButton) findViewById(R.id.previous);
        ImageButton next = (ImageButton) findViewById(R.id.next);
        if (show){
            previous.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        }else{
            previous.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        }

    }

}
