package com.example.parkhanee.mytravelapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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

/**
 * Created by parkhanee on 2016. 9. 6..
 */
public class FolderListFragment extends Fragment {
    TextView refresh;
    String userId;
    String postData;
    String DEBUG_TAG="folderFragment";

    private ListView listView;
    private FolderListAdapter myAdapter;
    ProgressDialog dialog;
    public static Boolean isHidden;// tells if the drop-down "newFolder" fragment is hidden
    Button btn_new;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_folder_list,container,false);
        isHidden = true;
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        myAdapter = new FolderListAdapter(getActivity());
        listView = (ListView) view.findViewById(R.id.listView2);
        listView.setAdapter(myAdapter);
        dialog = new ProgressDialog(getContext());

        //fetch data to make folder list at first
        userId = MainActivity.getLoginId();
        postData = "user_id="+userId;
        myClickHandler();

        refresh = (TextView) view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // get user id from shared preference
                userId = MainActivity.getLoginId();
                postData = "user_id="+userId;
                myClickHandler();
            }
        });


        btn_new = (Button) view.findViewById(R.id.button8);
        System.out.println(btn_new);
        final View frame =  view.findViewById(R.id.frame1);

        //set fragment
        Class fragmentClass = NewFolderFragment.class;
        Fragment fragment=null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame1, fragment).commit();

        btn_new.setOnClickListener(new View.OnClickListener() { //새로운 폴더 만들기 button onclick
            @Override
            public void onClick(View view) {
                ExpandCollapseAnimation animation = null;
                if(isHidden) { //닫혀있는거 열기
                    animation = new ExpandCollapseAnimation(frame, 700, 0);
                    isHidden = false;
                    btn_new.setText("저장");
                } else { //열린거 닫기
                    animation = new ExpandCollapseAnimation(frame, 400, 1);
                    isHidden = true;
                    btn_new.setText("새로운 폴더 만들기");

                    // TODO: 2016. 9. 9. Do refresh only when new information is being sent to server
                    //fetch data to REFRESH folder list
                    userId = MainActivity.getLoginId();
                    postData = "user_id="+userId;
                    myClickHandler();
                }
                frame.startAnimation(animation);

            } //onclick
        });
    }

    public void myClickHandler() { // check if the network has connected
        String stringUrl = "http://hanea8199.vps.phps.kr/folderlist_process.php";
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // get folders list
            new FetchData().execute(stringUrl);
        } else {
            Toast.makeText(getActivity(), "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }


    private class FetchData extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            dialog.setMessage("데이터를 가져오는 중입니다");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return downloadUrl(strings[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 50000;

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
                writer.write(postData);
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

        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected void onPostExecute(String s) {

            int resultCode=99;
            String resultMsg="";
            String msg;
            int totalCount=0;

            try{
                JSONObject result = new JSONObject(s);
                JSONObject header = result.getJSONObject("header");
                resultCode = header.getInt("resultCode");
                resultMsg = header.getString("resultMsg");
                if (resultCode==00) {
                    JSONObject body = result.getJSONObject("body");
                    totalCount = body.getInt("totalCount");
                    if (totalCount == 1) {
                        JSONObject folder = body.getJSONObject("folders");
                        Folder folderItem = getFolderInfo(folder);
                        myAdapter.clearItem();// to avoid duplicated data shown when refresh
                        myAdapter.addItem(0, folderItem);
                    } else if (totalCount > 1) {
                        JSONArray folders = body.getJSONArray("folders");
                        myAdapter.clearItem(); // to avoid duplicated data shown when refresh
                        for (int i = 0; i < totalCount; i++) {
                            JSONObject folder = folders.getJSONObject(i);
                            Folder folderItem = getFolderInfo(folder);
                            myAdapter.addItem(i, folderItem);
                        }
                    }
                }// result is ok
                resultMsg = result.toString();
            }catch (JSONException e){
                e.printStackTrace();
            }
            System.out.println(resultMsg);

            myAdapter.notifyDataSetChanged();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        public Folder getFolderInfo(JSONObject folder) throws JSONException {
            String name = folder.getString("folder_name");
            String desc  =  folder.getString("description");
            String start = folder.getString("date_start").substring(0,10);
            String end = folder.getString("date_end").substring(0,10);
            Folder folderItem = new Folder(name,desc,start,end);
            return folderItem;
        }
    }


}
