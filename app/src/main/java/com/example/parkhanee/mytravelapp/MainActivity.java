package com.example.parkhanee.mytravelapp;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    public static Boolean ifLogged;
    public static Boolean ifFbLogged=false;

    public static SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String userIdKey = "userId";
    public static final String userNameKey = "userName";
    public static final String isFBKey = "isFB"; // "y", "n"

    //navigation view
    private Toolbar toolbar;
    public static NavigationView navigationView;
    private DrawerLayout drawerLayout;

    TextView tv_login;
    TextView tv_username;
    ImageView iv_icon;

    HashMap<String, String> postDataParams;
    private static final String DEBUG_TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(MainActivity.this);
        setContentView(R.layout.activity_temp);


        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Init main fragment as default
        Class fragmentClass = MainContentFragment.class;
        Fragment fragment=null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

        setupDrawerContent(navigationView);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.string_map){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //TODO add code
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //TODO add code
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void setupDrawerContent(NavigationView navigationView) {

        // init Views in Header.xml
        View headerLayout = navigationView.getHeaderView(0);
        iv_icon = (ImageView) headerLayout.findViewById(R.id.profile_image);
        tv_login = (TextView) headerLayout.findViewById(R.id.textView18);
        tv_username = (TextView) headerLayout.findViewById(R.id.username);

        // init button onclick in MainContentFragment.xml


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (menuItem.getItemId()==R.id.folder){
                            if (!ifLogged){
                                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                                adb.setTitle("로그인이 필요한 서비스 입니다");
                                adb.setIcon(android.R.drawable.ic_dialog_alert);
                                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(MainActivity.this,LogInActivity.class);
                                        startActivity(i);
                                    } });
                                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    } });
                                adb.show();
                                return false;
                            }
                        }
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;

        Double lat=0.0;
        Double lng=0.0; // location data appending to nearbyFragment
        switch(menuItem.getItemId()) {
            case R.id.main:
                fragmentClass = MainContentFragment.class;
                break;
            case R.id.nearby:
                fragmentClass = NearbyFragment.class;
                //get location data from MainContentFragment
                // in order to pass the data to nearbyFragment
                lat = MainContentFragment.lat;
                lng = MainContentFragment.lng;
                break;
            case R.id.area:
                fragmentClass = AreaFragment.class;
                break;
            case R.id.folder:
                // TODO: 2016. 9. 8.  folderlist_process php 파일 실행시키고 그 데이터 받아와서 리스트뷰 통해서 뿌려주기
                fragmentClass = FolderListFragment.class;
                break;
            // TODO: 2016. 9. 8. create poi, map fragment
//            case R.id.poi:
//                break;
//            case R.id.map:
//                break;
            default:
                fragmentClass = MainContentFragment.class;
                System.out.println("selectDrawerItem default ?");
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
            if (lat!=0.0){
                //pass location data to nearbyFragment
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", lat);
                bundle.putDouble("lng",lng);
                fragment.setArguments(bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        //menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }



    @Override
    protected void onResume() {
        super.onResume();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken!=null){ //if 페북로그인 되어있으면



            String id = accessToken.getUserId();
            String name = "null"; // TODO: 2016. 9. 15. could be a problem?

            login(id,name,true);

            // execute AsyncTask to get Profile asynchronously
            myClickHandler();
        }

        String fb = sharedpreferences.getString(isFBKey,"");
        switch (fb){ //ifLogged랑 ifFbLogged는 mainActivity Create할 때 마다 매번 새로 만들어지는 변수들이므로 SP에서 매번 동기화 필요
            case "y": ifLogged=true; ifFbLogged=true;
                System.out.println("Main case1") ;
                break;
            case "n" : ifLogged=true; ifFbLogged=false;
                System.out.println("Main case2");
                break;
            default: ifLogged= false; ifFbLogged=false;
                System.out.println("Main case3");
                break;
        }

        if (ifLogged){

            tv_login.setText("로그아웃");
            tv_username.setText(getUserName());
            System.out.println("Main ifLogged");
            iv_icon.setVisibility(View.VISIBLE);
            tv_username.setVisibility(View.VISIBLE);
            if (ifFbLogged){
                iv_icon.setImageResource(R.drawable.com_facebook_button_icon_blue);
            }else {
                iv_icon.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }else {
            tv_username.setVisibility(View.GONE);
            iv_icon.setVisibility(View.GONE);
            System.out.println("Main ! ifLogged");
            tv_login.setText("로그인 해 주세요");
            tv_username.setText("My Travel App");
        }

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ifLogged){ //log out onClick
                    logout();
                    Toast.makeText(MainActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                    tv_login.setText("로그인 해 주세요");
                    tv_username.setVisibility(View.GONE);
                    iv_icon.setVisibility(View.GONE);
                }else { //log in onClick
                    Intent i = new Intent(MainActivity.this,LogInActivity.class);
                    startActivity(i);
                }
            }
        });


    }

    public static String getUserId(){
        String str = sharedpreferences.getString(userIdKey,null);
        return str;
    }

    public static String getUserName(){
        String str = sharedpreferences.getString(userNameKey,null);
        return str;
    }

    public static Boolean getisFB(){ // is it needed ???
        String str = sharedpreferences.getString(isFBKey,"n");
        return str.equals("y");
    }

    public static void login(String id,String name,Boolean isFB){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(userIdKey, id);
        editor.putString(userNameKey,name);
        if (isFB){
            editor.putString(isFBKey,"y"); //FB login
        }else{
            editor.putString(isFBKey, "n");
        }

        editor.commit();
        ifLogged = true;
    }

    public static void logout(){ //removeLoginInfoFromSharedPreference
        if (getisFB()){
            LoginManager.getInstance().logOut(); // facebook logout !!
            ifFbLogged=false;
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(userIdKey);
        editor.remove(isFBKey);
        editor.remove(userNameKey);
        editor.commit();
        ifLogged = false;
    }





    private class getProfile extends AsyncTask<Void,Void,String>{
        myProfile myProfile = new myProfile();
        ProfileTracker tracker;
        @Override
        protected void onPostExecute(String s) {
            if (tracker!=null){
                tracker.stopTracking();
            }
            String user_name = myProfile.getName();
            String user_id = myProfile.getId();
            login(user_id,user_name,true);
            tv_username.setText(myProfile.getName());

            postDataParams = new HashMap<>();
            putDataIntoParams(user_id,user_name);
            String stringUrl = "http://hanea8199.vps.phps.kr/fb_login.php";
            new FacebookServerLogIn().execute(stringUrl);
        }

        @Override
        protected String doInBackground(Void... voids) {

            Profile profile = Profile.getCurrentProfile();
            if (profile!=null){
                myProfile.setName(profile.getName());
                myProfile.setId(profile.getId());
            }else {

                tracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        if (currentProfile != null) { //the user may have logged in or changed some of his profile settings
                            myProfile.setName(currentProfile.getName());
                            myProfile.setId(currentProfile.getId());
                        } else {
                            myProfile.setName(oldProfile.getName());
                            myProfile.setId(oldProfile.getId());
                        }
                    }
                };
                tracker.startTracking();

                while (!myProfile.profileHasSet()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("waiting profile.. ");
                }
            }
            return myProfile.getName();
        }

        private class myProfile {
            String name;
            String id;

            public void setName(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public Boolean profileHasSet(){
                return name!=null;
            }

            public void setId(String id){
                this.id = id;
            }

            public String getId() {
                return id;
            }
        }
    }

    public void myClickHandler() { // check if the network has connected
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new getProfile().execute();
        } else {
            Toast.makeText(MainActivity.this, "! No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void putDataIntoParams(String user_id,String user_name){
        postDataParams.put("user_id",user_id);
        postDataParams.put("user_name",user_name);
    }

    private class FacebookServerLogIn extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            int resultCode=99;
            String resultMsg="";
            String msg;
            try{
                JSONObject result = new JSONObject(s);
                resultCode = result.getInt("resultCode");
                //resultMsg = result.getString("resultMsg");
            }catch (JSONException e){
                e.printStackTrace();
            }

            if (resultCode==00){ //result is Okay
                msg = "fb info has passed to server db successfully";
               /*AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent aa = new Intent(getContext(),LogInActivity.class);
                                startActivity(aa);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();*/
                Toast.makeText(MainActivity.this,msg, Toast.LENGTH_SHORT).show();
            }else{ // error occurred
                switch (resultCode){
                    case 11 : msg = "no info";
                        break;
                    case 12: msg = "id dup";
                        break;
                    default: msg = "unknown error";
                        break;
                }
                Toast.makeText(MainActivity.this,msg, Toast.LENGTH_SHORT).show();
            }

        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                // add post parameters
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public String readIt(InputStream stream, int len) throws IOException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }
    }




}
