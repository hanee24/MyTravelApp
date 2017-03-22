package com.example.parkhanee.mytravelapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by parkhanee on 2016. 9. 5..
 */
public class MainContentFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ImageButton btn_nearby;
    ImageButton btn_folder;
    TextView location;
    ProgressBar progressBar;

    GoogleApiClient mGoogleApiClient;
    Location myLocation;
    Double lat;
    Double lng;

    View a;
    final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 111;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 112;

    // to check if the mainContentFragment is now active. --> MainActivity.OnBackPressed
    public static Boolean isMain;
    String TAG = "MainContentFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_main_content, container, false);
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        a = view.findViewById(R.id.fragment_fb);
        btn_nearby = (ImageButton) view.findViewById(R.id.button);
        btn_folder = (ImageButton) view.findViewById(R.id.button2);
        location = (TextView) view.findViewById(R.id.textView2);
        progressBar = (ProgressBar) view.findViewById(R.id.weatherProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        a.setVisibility(View.GONE); //hide facebook login fragment

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(com.google.android.gms.location.LocationServices.API)
                    .build();
        }

    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle connectionHint) {

        btn_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.ifLogged) {
                    //go to folderFragment if user has been logged in
                    Class fragmentClass = FolderListFragment.class;
                    Fragment fragment = null;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
                    //set action bar title
//                    getActivity().setTitle(R.string.string_folder);
                    //set navigation view item checked
                    MainActivity.navigationView.setCheckedItem(R.id.folder);
                } else {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                    adb.setTitle("로그인이 필요한 서비스 입니다");
                    adb.setIcon(android.R.drawable.ic_dialog_alert);
                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getContext(), LogInActivity.class);
                            startActivity(i);
                        }
                    });
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    adb.show();
                }

            }
        });


        // case 1 : 권한승인 필요없는 경우 (api 23 이하) 또는 이전에 권한 승인 한 경우 (api 23 이상)
        //          onConnected메소드가 끝까지 실행 됨
        // case 2 : 권한승인여부 물어봐서 승인 된 경우
        //          onRequestPermissionResult 에서 onPermissionGranted가 실행됨
        // case 3 : 권한승인여부 물어봐서 거절 된 경우
        //          onRequestPermissionResult 에서 onPermissionDenied가 실행됨


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onConnected: ask coarse location permission");

            // 위치 권한 승인여부 사용자에게 물어보기
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            return;
        }

        Log.d(TAG, "onConnected: permission CASE 1 ");
        onPermissionGranted();

    }


    private void onPermissionGranted() throws SecurityException {
        myLocation = com.google.android.gms.location.LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient); // this line of code requires security exception process

        if (MainActivity.lat==null) {
            // 메인 액티비티에 위치정보 저장되어있는것이 없으면 새로 불러온다
            Log.d(TAG, "onConnected:  1313 new location");
            if (myLocation==null){
                Toast.makeText(getActivity(),"location is null",Toast.LENGTH_SHORT).show();
            }else{
                lat = myLocation.getLatitude();
                lng = myLocation.getLongitude();
                MainActivity.lat = lat;
                MainActivity.lng = lng;
            }
        }else{
            Log.d(TAG, "onConnected: 1313 location from main");
            lat = MainActivity.lat;
            lng = MainActivity.lng;
        }

        String Lat = String.valueOf(lat);
        String Lng = String.valueOf(lng);

        myClickHandler(Lat,Lng);


        btn_nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open nearbyFragment with extras (lat, lgt)
                Class fragmentClass = NearbyFragment.class;
                Fragment fragment=null;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", lat);
                    bundle.putDouble("lng",lng);
                    fragment.setArguments(bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
                //set action bar title
//                getActivity().setTitle(R.string.string_nearby);
                //set navigation view item checked
                MainActivity.navigationView.setCheckedItem(R.id.nearby);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "onConnected: permission CASE 2 ");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    onPermissionGranted();

                } else {

                    Log.d(TAG, "onConnected: permission CASE 3 ");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    // 주변탐색 기능을 사용할 수 없습니다 ~~
                    // 거절 한 경우 nearbyFragment 접근 불가 처리(메인액티비티버튼&네비게이션뷰)
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void myClickHandler(String Lat, String Lng) { // check if the network has connected
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+Lat+","+Lng+"&key=AIzaSyBZ9S7Eo3eaZ0ocOQTuJScvOw_xbXiM194&language=ko";
            if (MainActivity.geoCode!=null && MainActivity.weatherHashMap!=null){
                Log.d(TAG, "myClickHandler: 1313 geoCode, weather from Main");
                location.setText(MainActivity.geoCode);
                setWeatherIntoViews(MainActivity.weatherHashMap);
            } else {
                Log.d(TAG, "myClickHandler: 1313 new geoCode and weather");
                new getGeoCode().execute(url);
                // TODO: 2017. 2. 11.   new getWeather().execute(Lat,Lng);
            }



        } else {
            // TODO: 2017. 2. 11.
            location.setText("위치 정보를 찾을 수 없습니다. 네트워크 연결을 확인 해 주세요.");
            Toast.makeText(getActivity(), "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class getGeoCode extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String strUrl = strings[0];

            JSONArray results=null;
            String formatted_address=null;

            String buf;
            String jsonString ="";
            try {
                URL url = new URL(strUrl);
                URLConnection conn = url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                while((buf=br.readLine())!=null){
                    jsonString +=buf;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // System.out.println("jsonString"+jsonString);

            try {
                JSONObject object = new JSONObject(jsonString);
                //JSONObject response = object.getJSONObject("response");
                results = object.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject item = results.getJSONObject(0); //NullPointerException when network doesn't work well?
                formatted_address = item.getString("formatted_address");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return formatted_address;
        }

        @Override
        protected void onPostExecute(String s) {
            //split "s" , get second, third, forth words
            String[] str = s.split(" ");
            String ss = str[1]+" "+str[2]+" "+str[3];
            location.setText(ss);
            MainActivity.geoCode = ss;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isMain = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isMain = false;
    }

    public class WeatherHttpClient {

        private String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
        private String IMG_URL = "http://openweathermap.org/img/w/";

        public String getWeatherData(String Lat, String Lgt) {
            HttpURLConnection con = null ;
            InputStream is = null;

            try {
                String url = BASE_URL+"lat="+Lat+"&lon="+Lgt+"&APPID="+getString(R.string.weatherApiKey);
                con = (HttpURLConnection) ( new URL(url)).openConnection();
                con.setRequestMethod("POST"); // fixme :  권한 허용 하고나서 바로 실행했을 때  throws HTTP 405 "Method not allowed"
                con.setDoInput(true);
                con.setDoOutput(true);
                con.connect();

                int response = con.getResponseCode();
                Log.d(TAG, "The server response is: " + response);

                // Let's read the response
                StringBuffer buffer = new StringBuffer();
                is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ( (line = br.readLine()) != null )
                    buffer.append(line + "rn");

                is.close();
                con.disconnect();
                return buffer.toString();
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            finally {
                try { is.close(); } catch(Throwable t) {}
                try {
                    assert con != null;
                    con.disconnect(); } catch(Throwable t) {}
            }

            return null;

        }

    }

    private class getWeather extends AsyncTask<String,Void,HashMap<String,String>> {

        protected HashMap<String,String> doInBackground(String... params) {
            if (MainActivity.weatherHashMap==null){
                // 메인 액티비티에 미리 저장된 날씨정보가 없을때만 새로 불러오기
                String data = ( (new WeatherHttpClient()).getWeatherData(params[0],params[1]));
                HashMap<String, String> weatherHashMap = new HashMap<>();

                Log.d(TAG, "doInBackground: data "+data);

                try {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
                    //weatherHashMap.put("icon",weather.getString("icon"));
                    weatherHashMap.put("main",weather.getString("main"));
                    weatherHashMap.put("description",weather.getString("description"));
                    JSONObject main = jsonObject.getJSONObject("main");
                    weatherHashMap.put("temp",main.getString("temp"));
                    weatherHashMap.put("humidity",main.getString("humidity"));

                    // put resId of icon in the hashMap
                    weatherHashMap.put("icon",String.valueOf(setWeatherIcon(weather.getString("icon"))));

                } catch (Exception e){
                    e.printStackTrace();
                }
                MainActivity.weatherHashMap = weatherHashMap;
                Log.d(TAG, "doInBackground: 1313 new weather");
                return weatherHashMap;
            }else { // TODO: 2016. 10. 20. 필요없음
                // 메인 액티비티에 미리 저장된 날씨정보 불러오기.
                Log.d(TAG, "doInBackground: 1313 weather from main");
                return MainActivity.weatherHashMap;
            }

        }

        @Override
        protected void onPostExecute(HashMap<String, String> weatherHashMap) {
            // check the data
            //Log.d(TAG, "onPostExecute: hashMap "+weatherHashMap.toString());

            setWeatherIntoViews(weatherHashMap);
        }
    }

    private int setWeatherIcon(String code){
        int resId=R.drawable.d1;
        switch (code){
            case "01d" : resId = R.drawable.d1;
                break;
            case "01n" : resId = R.drawable.n1;
                break;
            case "02d" : resId = R.drawable.cloud;
                break;
            case "02n" : resId = R.drawable.cloud;
                break;
            case "03d" : resId = R.drawable.cloud;
                break;
            case "03n" : resId = R.drawable.cloud;
                break;
            case "04d" : resId = R.drawable.cloud;
                break;
            case "04n" : resId = R.drawable.cloud;
                break;
            case "09d" : resId = R.drawable.rain;
                break;
            case "09n" : resId = R.drawable.rain;
                break;
            case "10d" : resId = R.drawable.rain;
                break;
            case "10n" : resId = R.drawable.rain;
                break;
            case "11d" : resId = R.drawable.thunder;
                break;
            case "11n" : resId = R.drawable.thunder;
                break;
            case "13d" : resId = R.drawable.snow;
                break;
            case "13n" : resId = R.drawable.snow;
                break;
            case "50d" : resId = R.drawable.cloud;
                break;
            case "50n" : resId = R.drawable.cloud;
                break;
        }
        return resId;
    }

    private String getDesc(String desc){
        switch (desc){
            //800
            case "clear sky" : return "맑음";
            //80x
            case "few clouds" : return "구름 조금";
            case "scattered clouds" : return  "구름 조금";
            case "broken clouds" : return "흐림";
            case "overcast clouds" : return "구름 많음";
            //5xx
            case "light rain" : return "가랑비";
            case "moderate rain" : return "가랑비";
            case "heavy intensity rain" : return "폭우";
            case "very heavy rain" : return "폭우";
            case "extreme rain" : return "폭우";
            case "shower rain" : return "소나기";
            case "freezing rain" : return "비";
            case "light intensity shower rain" : return "가벼운 소나기";
            case "ragged shower rain" : return "소나기";
            case "heavy intensity shower rain" : return "강한 소나기";
            //3xx
            case "light intensity drizzle" : return "가벼운 이슬비";
            case "drizzle" : return "이슬비";
            case "heavy intensity drizzle" : return "가랑비";
            case "light intensity drizzle rain" : return "가랑비";
            case "drizzle rain" : return "가랑비";
            case "heavy intensity drizzle rain" : return "비";
            case "shower rain and drizzle" : return "소나기";
            case "heavy shower rain and drizzle" : return "소나기";
            case "shower drizzle" : return "소나기";

            case "haze" : return "옅은 안개";
            default: return desc;
        }
    }

    // 온도가 캘빈온도 이므로 -273.15 해야 섭씨 온도
    private String KelvinIntoCelsius(String kelvin){
        double kelvinDouble = Double.parseDouble(kelvin);
        double celsiusDouble = kelvinDouble - 273.15;
        String celsius = String.format("%.1f", celsiusDouble);
        return celsius;
    }

    private void setWeatherIntoViews(HashMap<String, String> weatherHashMap){
        TextView tv_temp = (TextView) getActivity().findViewById(R.id.tv_temp); // FIXME: 2016. 10. 25. 날씨 세팅되기 전에 다른거 들어갔다 나오면 여기서 널포인터 에러
        tv_temp.setText(KelvinIntoCelsius(weatherHashMap.get("temp")));
        TextView tv_desc = (TextView) getActivity().findViewById(R.id.tv_description);
        String desc = weatherHashMap.get("description");
        tv_desc.setText(getDesc(desc));
        int resId = Integer.parseInt(weatherHashMap.get("icon"));
        ImageView iv = (ImageView) getActivity().findViewById(R.id.iv_weather);
        iv.setImageResource(resId);
        progressBar.setVisibility(View.GONE);
    }

}
