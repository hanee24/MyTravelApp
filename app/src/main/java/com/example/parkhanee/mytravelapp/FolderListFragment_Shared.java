package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by parkhanee on 2016. 10. 15..
 */
public class FolderListFragment_Shared  extends Fragment {

    TextView refresh;
    private FolderListAdapter myAdapter;

    String TAG = "FolderListFragment_Shared";
    HashMap<String, String> PostDataParams;

    SharedPreferences sharedPreferences;
    List<FolderListAdapter.shareState> shareStates;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_folder_list_shared,container,false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        myAdapter = new FolderListAdapter(getActivity());
        ListView listView = (ListView) view.findViewById(R.id.listView5);
        listView.setAdapter(myAdapter);
        final DBHelper dbHelper = new DBHelper(getActivity());

        refresh = (TextView) view.findViewById(R.id.textView43);
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                myClickHandler();
                Toast.makeText(getActivity(), "refresh", Toast.LENGTH_SHORT).show();
            }
        });


        // set item onClickListener on the listView
        // direct to FolderActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                int folder_id = myAdapter.getItem(position).getId();
                FolderListAdapter.shareState state = myAdapter.getIsShared(position);

                if (state == FolderListAdapter.shareState.REQUESTED){
                    // 공유신청받은 폴더 아이템을 폴더리스트에서 클릭하면, 공유신청 gcm pendingIntent 와 같은 액티비티로 넘어감.
                    Intent i = new Intent(getActivity(),ViaNotificationActivity.class);
                    i.putExtra("share_id",dbHelper.getShareWithFolderId(folder_id).get(0).getShare_id()); // 공유받은 폴더에 해당하는 share row 는 하나밖에 없으니까 get(0) !
                    startActivity(i);

                }else if ( state == FolderListAdapter.shareState.ACCEPTED){
                    Intent a = new Intent(getActivity(),FolderActivity.class);
                    Bundle args = new Bundle();
                    args.putInt("folder_id",folder_id); // 리스트뷰 포지션말고 폴더 포지션(아이디)을 넘겨야지 !

                    a.putExtra("args",args);
                    startActivity(a);
                }
            }
        });

        dbHelper.close();
    }


    @Override
    public void onResume() {
        super.onResume();
        //fetch data to make folder list on RESUME because the new data should be fetched when a folder data has been updated
        myClickHandler();
    }


    // must clear ListViewItem before calling this method !!
    public void setFolderListView(List<Folder> folders, FolderListAdapter.shareState s){
        for (int i=0; i< folders.size();i++){
            Folder folder = folders.get(i);
            myAdapter.addItem(folder,s);
        }
        myAdapter.notifyDataSetChanged();
    }

    // must clear ListViewItem before calling this method !!
    public void setFolderListView(List<Folder> folders, List<FolderListAdapter.shareState> s){
        for (int i=0; i< folders.size();i++){
            Folder folder = folders.get(i);
            myAdapter.addItem(folder,s.get(i));
        }
        myAdapter.notifyDataSetChanged();
    }

    public void myClickHandler() {

        /* 폴더목록 액티비티에서 데이터 동기화 처리
        * 1. 로컬디비에서 폴더목록 가져와서 출력
        * 2. (온라인) 로컬디비의 정보를 서버로 보내서 동기화 -- 새폴더 만들어서 목록 다시 불러오면 새폴더 서버로 동기화도 바로 처리됨.
        */
        DBHelper db = new DBHelper(getActivity());
        db.getReadableDatabase();
        // 1. get folder list from local DB no matter there is network or not.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
        String str = sharedPreferences.getString(getString(R.string.userIdKey),null);
        List<Folder> folders;
        // clear Adapter before fetch folder list
        myAdapter.clearItem();

//        setFolderListView(db.getSharedFolders(str),db.getSharedFoldersState(str));

        // 2. synchronize server
        // check if the network has connected
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data from localDB and put them into postData hashMap.
            PostDataParams = new HashMap<>();
            PostDataParams.put("user_id",str); // 로그인한 사용자 아이디

            // 공유받은 폴더의 아이디를 뽑아서 POST 로 보내줌 ! syncFolderProcess 랑 다 같이 보내서 php 파일에서 각자 필요한 것만 갖다쓰면 되니까
            folders = db.getSharedFolders(str);
            shareStates= db.getSharedFoldersState(str);
            PostDataParams.put("shared_size",String.valueOf(folders.size()));
            for (int j=0; j<folders.size();j++){
                Folder folder = folders.get(j);
                PostDataParams.put("shared_folder_id"+j,String.valueOf(folder.getId()));
                PostDataParams.put("shared_state"+j,shareStates.get(j).toString()); // 서버로 보낼때는 state 상관없지만 리스트뷰에 뿌려줄때 보여줄려고 여기 넣어놓음! AsyncTask의 PostExecute 에서 씀
            }


            String url = "http://hanea8199.vps.phps.kr/sharedfolderlist.php";
            new SyncServer().execute(url); // get shared folder info including folderName, desc, duration, ..

            db.close();
        } else {
            Toast.makeText(getActivity(), "Cannot proceed, No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }


    private class SyncServer extends AsyncTask<String, Void, String> {

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


            // clear Adapter before fetch folder list
            myAdapter.clearItem();

            JSONObject result = null;
            try {
                result = new JSONObject(s);
                //check the whole result
                String resultStr = result.toString();
                Log.d(TAG, "onPostExecute: "+ resultStr);

                JSONObject body = result.getJSONObject("body");
                totalCount = body.getInt("totalCount");

                DBHelper db = new DBHelper(getActivity());
                db.getWritableDatabase();

                if (totalCount>0){
                    List<Folder> folderList = new LinkedList<>();
                    JSONArray folders = body.getJSONArray("folders");
                    for (int j=0;j<totalCount;j++){
                        Folder folder = getFolderInfo((JSONObject) folders.get(j));
                        folderList.add(j,folder);
                        db.updateFolder(folder); // update folder info on localDB
                    }

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
                    String str = sharedPreferences.getString(getString(R.string.userIdKey),null);
                    // 1-1. shared folders
                    setFolderListView(folderList,db.getSharedFoldersState(str));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            myAdapter.notifyDataSetChanged();

        }

        public Folder getFolderInfo(JSONObject folder) throws JSONException {
            // sort folder information from a jsonObject sent from the server
            String name = folder.getString("folder_name");
            String desc  =  folder.getString("description");
            String start = folder.getString("date_start").substring(0,10);
            String end = folder.getString("date_end").substring(0,10);

            // String start == 2016-09-20 , String str_start == 2016 - 09 - 20.
            // trimming date format
//            String str_start = start.substring(0,4)+" - "+start.substring(5,7)+" - "+start.substring(8,10);
//            String str_end = end.substring(0,4)+" - "+end.substring(5,7)+" - "+end.substring(8,10);

            int id = folder.getInt("folder_id");
            String user_id = folder.getString("user_id");
            String created = folder.getString("created").substring(0,10);

            Folder folderItem = new Folder(id,name,user_id,desc,start,end,created);
            return folderItem;
        }
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
