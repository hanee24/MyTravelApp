package com.example.parkhanee.mytravelapp;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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
    private static final String TAG = "MainActivity";

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

    //gcm quick start guide
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    // these three values are always null when the activity is recreated !
    public static Double lat;
    public static Double lng;
    public static HashMap<String, String> weatherHashMap;
    public static String geoCode;

    public static View frame;



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
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

        setupDrawerContent(navigationView);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.string_map){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        // [START gcm quick start guide]
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.d(TAG, "onReceive: sent token");
                } else {
                    Log.d(TAG, "onReceive: token error");
                }
            }
        };

        // blog.saltfactory
        registBroadcastReceiver();
        getInstanceIdToken(); // it is originally within button onClick method in the post

        NewFolderFrame();
    }

    /**
     * src from bolg.saltfactory.net
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     * duplicated with getRegId AsyncTask
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * src from bolg.saltfactory.net
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();


                switch (action) {
                    case QuickstartPreferences.REGISTRATION_READY:
                        // 액션이 READY 일 경우
//                        mRegistrationProgressBar.setVisibility(View.GONE);
                        break;
                    case QuickstartPreferences.REGISTRATION_GENERATING:
                        // 액션이 GENERATING 일 경우
//                        mRegistrationProgressBar.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onReceive: " + getString(R.string.registering_message_generating));
                        break;
                    case QuickstartPreferences.REGISTRATION_COMPLETE:
                        // 액션이 COMPLETE 일 경우
//                        mRegistrationProgressBar.setVisibility(View.GONE);
//                        Log.d(TAG, "onReceive: " + getString(R.string.registering_message_complete));
                        String token = intent.getStringExtra("token");
//                        Log.d(TAG, "onReceive: " + token);
                        isReceiverRegistered = true; // gcm quick start guide. is it correct to be placed here?
                        break;
                }

            }
        };
    }

    @Override
    protected void onPause() {
        // gcm quick start guide
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    /**
     * gcm quick start guide
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    // [FINISH gcm quick start guide]


    // This was added in order to manage Fragment BackStack
    @Override
    public void onBackPressed() {
        // TODO: 2016. 9. 16. set nav view item selected
        if (MainContentFragment.isMain){
            //if mainContentFragment is active, quit the app
            super.onBackPressed();
        }else{
            //if not, redirect to mainFragment.
            Class fragmentClass = MainContentFragment.class;
            Fragment fragment=null;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
            navigationView.setCheckedItem(R.id.main);
//            setTitle(R.string.string_main);
        }
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

        Class fragmentClass;

        //Set action bar title
        setTitle(menuItem.getTitle());

        // location data appending to nearbyFragment
        switch(menuItem.getItemId()) {
            case R.id.main:
                openFragment(MainContentFragment.class);
                break;
            case R.id.nearby:
                openFragment(NearbyFragment.class);
                Log.d(TAG, "selectDrawerItem: lat lng hashmap "+lat+" "+lng+" "+weatherHashMap );
                break;
            case R.id.area:
                openFragment(AreaFragment.class);
                break;
            case R.id.folder:
                if (ifLogged){
                    openFragment(FolderListFragment.class);
                }else{
                    loginDialog();
                    break;
                }
                break;
            case R.id.share :
                if(ifLogged){
                    openFragment(FolderListFragment_Shared.class);
                }else {
                    loginDialog();
                    break;
                }
                break;
            default:
               openFragment(MainContentFragment.class);
                System.out.println("selectDrawerItem default ?");
              break;
        }

        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    public void openFragment(Class fragmentClass){
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            if (lat!=0.0){
                //pass location data to nearbyFragment
                Bundle bundle = new Bundle();
                bundle.putDouble("lat",lat);
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
        // TODO: 2016. 11. 11.

    }

    public void loginDialog(){
        AlertDialog.Builder adb = new AlertDialog.Builder(getApplicationContext());
        adb.setTitle("로그인이 필요한 서비스 입니다");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getApplicationContext(),LogInActivity.class);
                startActivity(i);
            } });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            } });
        adb.show();
    }



    @Override
    protected void onResume() {
        super.onResume();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken!=null){ //if 페북로그인 되어있으면



            String id = accessToken.getUserId();
            String name = "null"; // TODO: 2016. 9. 15. could it be a problem?

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
                iv_icon.setImageResource(R.drawable.mytravel);
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
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    logout(fragmentManager);
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

        // gcm quick start guide
//        registerReceiver();

        // blog.saltfactory
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

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

    public static void logout(FragmentManager fm){ //removeLoginInfoFromSharedPreference
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

        // redirect user to MainFragment
        Class fragmentClass = MainContentFragment.class;
        Fragment fragment=null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fm.beginTransaction().replace(R.id.frame, fragment).commit();
        navigationView.setCheckedItem(R.id.main);
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
                Toast.makeText(MainActivity.this,msg, Toast.LENGTH_SHORT).show();
            }
//            else{ // error occurred
//                switch (resultCode){
//                    case 11 : msg = "no info";
//                        break;
//                    case 12: msg = "id dup";
//                        break;
//                    default: msg = "unknown error";
//                        break;
//                }
//                Toast.makeText(MainActivity.this,msg, Toast.LENGTH_SHORT).show();
//            }

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

    public boolean onKeyUp(int keyCode, KeyEvent event) { // make drawer open/close on hardware menu button click
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            Log.d(TAG, "onKeyUp: KEYCODE:MENU");
            if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer(GravityCompat.START);
            }else{
                drawerLayout.openDrawer(GravityCompat.START);
            }

            //  return true when you are handling the event; return false if you want the system to handle the event too.
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public View getFrame(){
        View frame = findViewById(R.id.frame1);
        return frame;
    }

    public void NewFolderFrame(){
        // at the end of OnCreate
        frame =findViewById(R.id.frame1);

        //set fragment
        Class fragmentClass = NewFolderFragment.class;
        Fragment fragment=null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame1, fragment).commit();
    }

}
