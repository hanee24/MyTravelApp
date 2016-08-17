package com.example.parkhanee.mytravelapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class NearbyD3Activity extends AppCompatActivity {

    int contentId=0;
    String apiKey;
    URL apiREQ;
    URL imgREQ;
    ProgressDialog dialog;
    String imageYN = "Y"; //Y=콘텐츠이미지조회   //N=”음식점”타입의음식메뉴이미지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_d3);

        //intent
        Intent i = getIntent();
        contentId = i.getIntExtra("contentId",0);
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

        new asyncTask().execute();

    }

    private class asyncTask extends AsyncTask<Void, JSONObject, Void>{

        @Override
        protected void onPreExecute() {
            dialog.setMessage("로딩중입니다");
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
        protected void onProgressUpdate(JSONObject... values) {
            JSONObject header = values[0];
            JSONObject body = values[1];

            String h = header.toString();
            String b = body.toString();

            System.out.println("header : "+h);
            System.out.println("body : "+b);

        }
    }
}
