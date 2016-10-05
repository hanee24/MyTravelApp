package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by parkhanee on 2016. 9. 6..
 */
public class FolderListFragment extends Fragment {
    TextView refresh;
    String userId;

    private FolderListAdapter myAdapter;
    public static Boolean isHidden;// tells if the drop-down "newFolder" fragment is hidden
    public static Button btn_new;
    public static View frame;

    String TAG = "FolderListFragment";
    HashMap<String, String> PostDataParams;

    DBHelper db;

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
        ListView listView = (ListView) view.findViewById(R.id.listView2);
        listView.setAdapter(myAdapter);
//        dialog = new ProgressDialog(getContext());
        db = new DBHelper(getActivity());

        refresh = (TextView) view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                myClickHandler();
                Toast.makeText(getActivity(), "refresh", Toast.LENGTH_SHORT).show();
            }
        });


        btn_new = (Button) view.findViewById(R.id.button8);
        frame =  view.findViewById(R.id.frame1);

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
                } else { //열린거 닫기 + 저장
                    animation = new ExpandCollapseAnimation(frame, 400, 1);
                    isHidden = true;
                    btn_new.setText("새로운 폴더 만들기");

                    // get info from NewFolderFragment
                    //프래그먼트에서 뷰(에딧텍스트) 가져오기 //에딧텍스트에서 그안의 스트링 가져오기
                    String name = NewFolderFragment.et_name.getText().toString();
                    String desc = NewFolderFragment.et_desc.getText().toString();
                    //  date info is processed at NewFolderFragment and passed as a tag
                    String start_date = NewFolderFragment.et_start.getTag().toString();
                    String end_date = NewFolderFragment.et_end.getTag().toString();

                    Log.d(TAG, "onClick: "+name+desc);
                    Log.d(TAG, "onClick: start_date "+start_date);
                    Log.d(TAG, "onClick: end_date "+end_date);

                    myClickHandler();

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
                    int unixTime = (int) System.currentTimeMillis() / 1000; //set unix time as folder id
                    db.addFolder(new Folder(unixTime,name,MainActivity.getUserId(),desc,start_date,end_date,dateFormat.format(date)));
                    Log.d(TAG, "created "+dateFormat.format(date));

                    // reset editTexts since the data within them has been sent
                    NewFolderFragment.et_name.setText("");
                    NewFolderFragment.et_desc.setText("");
                    setCurrentDate(); //날짜 에딧텍스트 초기화

                    myClickHandler(); //update listView and sync data to server

                }
                frame.startAnimation(animation);

            } //onclick
        });

        // set item onClickListener on the listView
        // direct to FolderActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent a = new Intent(getActivity(),FolderActivity.class);
                Bundle args = new Bundle();
                args.putInt("folder_id",myAdapter.getItem(i).getId()); // 리스트뷰 포지션말고 폴더 포지션(아이디)을 넘겨야지 !

                a.putExtra("args",args);
                startActivity(a);
            }
        });
    }






    @Override
    public void onResume() {
        super.onResume();
        //fetch data to make folder list on RESUME because the new data should be fetched when a folder data has been updated
        myClickHandler();
    }

    public static void setCurrentDate(){
        // set current date as default
        final Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH)+1); // TODO : 2016. 9. 20. why does it need +1 ?
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));

        if (c.get(Calendar.MONTH)+1<10){
           month = "0"+month;
        }
        if (c.get(Calendar.DAY_OF_MONTH)<10){
            day = "0"+day;
        }

        String dateNow = year+"-"+month+"-"+day;
        NewFolderFragment.et_start.setText(dateNow);
        NewFolderFragment.et_start.setTag(dateNow+" 00:00:00");
        NewFolderFragment.et_end.setText(dateNow);
        NewFolderFragment.et_end.setTag(dateNow+" 00:00:00");
    }

    public void setFolderList(List<Folder> folders){
        myAdapter.clearItem(); // clear Adapter before fetch folder list
        for (int i=0; i< folders.size();i++){
            Folder folder = folders.get(i);
            myAdapter.addItem(folder);
        }
        myAdapter.notifyDataSetChanged();
    }

    public void myClickHandler() {

        /* 폴더목록 액티비티에서 데이터 동기화 처리
        * 1. 로컬디비에서 폴더목록 가져와서 출력
        * 2. (온라인) 로컬디비의 정보를 서버로 보내서 동기화 -- 새폴더 만들어서 목록 다시 불러오면 새폴더 서버로 동기화도 바로 처리됨.
        */

        // 1. get folder list from local DB no matter there is network or not.
        List<Folder> folders = db.getMyFolders(MainActivity.getUserId());
        setFolderList(folders);

        // TODO: 2016. 10. 4. sharedFolders는 어디에 출력해주지 ?

        // check if the network has connected
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // 2. synchronize server

            // fetch data from localDB and put them into postData hashMap.
            // get ONLY "MY" folders
            folders = db.getMyFolders(MainActivity.getUserId());
            PostDataParams = new HashMap<>();

            PostDataParams.put("user_id",MainActivity.getUserId()); // 로그인한 사용자 아이디
            PostDataParams.put("size",String.valueOf(folders.size()));
            for (int i=0; i< folders.size();i++){
                Folder folder = folders.get(i);
                PostDataParams.put("owner_id"+i,String.valueOf(folder.getOwner_id())); // 각 폴더 생성한 사용자 아이디
                PostDataParams.put("folder_id"+i,String.valueOf(folder.getId()));
                PostDataParams.put("folder_name"+i,folder.getName());
                PostDataParams.put("description"+i,folder.getDesc());
                PostDataParams.put("date_start"+i,folder.getDate_start());
                PostDataParams.put("date_end"+i,folder.getDate_end());
                PostDataParams.put("created"+i,folder.getCreated());
            }

            String stringUrl = "http://hanea8199.vps.phps.kr/syncfolderlist_process.php";
            new SyncServer().execute(stringUrl); // connect to server

        } else {
            Toast.makeText(getActivity(), "Cannot proceed, No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }


    private class SyncServer extends AsyncTask<String, Void, String>{

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
            int len = 5000000;

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
                writer.write(getPostDataString(PostDataParams));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                int response = conn.getResponseCode();
                Log.d(TAG, "The server response is: " + response);
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

        @Override
        protected void onPostExecute(String s) {

            int resultCode=99;
            String resultMsg="";
            String msg;
            int totalCount=0;


            try{
                JSONObject result = new JSONObject(s);
                //JSONObject header = result.getJSONObject("header");
                resultCode = result.getInt("resultCode");

                //check the whole result
                resultMsg = result.toString();
                Log.d(TAG, "onPostExecute: "+ resultMsg);

                if (resultCode!=00){
                    Toast.makeText(getActivity(), "sync failed", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onPostExecute: "+resultMsg);
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

        }

//        public Folder getFolderInfo(JSONObject folder) throws JSONException {
//            // sort folder information from a jsonObject sent from the server
//            String name = folder.getString("folder_name");
//            String desc  =  folder.getString("description");
//            String start = folder.getString("date_start").substring(0,10);
//            String end = folder.getString("date_end").substring(0,10);
//
//            // String start == 2016-09-20 , String str_start == 2016 - 09 - 20.
//            // trimming date format
////            String str_start = start.substring(0,4)+" - "+start.substring(5,7)+" - "+start.substring(8,10);
////            String str_end = end.substring(0,4)+" - "+end.substring(5,7)+" - "+end.substring(8,10);
//
//            int id = folder.getInt("folder_id");
//            String user_id = folder.getString("user_id");
//            String created = folder.getString("created").substring(0,10);
//
//            Folder folderItem = new Folder(id,name,user_id,desc,start,end,created);
//            return folderItem;
//        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        //convert data  being sent to server as POST method into correct form
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

        Log.d(TAG, "getPostDataString: "+result.toString());

        return result.toString();
    }

}
