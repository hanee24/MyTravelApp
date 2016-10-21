package com.example.parkhanee.mytravelapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


import static android.support.v4.widget.SwipeRefreshLayout.*;


public class FolderActivity extends AppCompatActivity implements OnRefreshListener {

    int folder_id;
    Folder folder;
    TextView tv_name,tv_desc, tv_date;
    DBHelper db;
    String TAG = "FolderActivity";
    RecyclerView recyclerView;
    FolderContentsAdapter mAdapter;
    LinearLayoutManager lm;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int pageNum = 1 ; // 현재 페이지 번호
    private int pages=0; // 총 불러와야하는 페이지 갯수
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        Intent a = getIntent();
        final Bundle bundle = a.getBundleExtra("args");
        folder_id = bundle.getInt("folder_id");

        // get folder from local DB
        db = new DBHelper(FolderActivity.this);
        //db.getAllFolders(MainActivity.getUserId());

        // initiate views
        tv_name = (TextView)findViewById(R.id.folderName);
        tv_desc = (TextView) findViewById(R.id.description);
        tv_date = (TextView) findViewById(R.id.textView25);

        mAdapter = new FolderContentsAdapter(FolderActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.listView4);
        lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm); // may need to use different kind of LayoutManager
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator()); // may need to use different kind
        
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = lm.getItemCount();
                int firstVisibleItem = lm.findFirstVisibleItemPosition();
                int count = totalItemCount - visibleItemCount;

                if(!isLoading && firstVisibleItem >= count && totalItemCount != 0
                        &&recyclerView.getChildAt(visibleItemCount - 1).getBottom() <= recyclerView.getHeight()) //  && mLockListView == false)
                {
                    Log.d(TAG, "onScrolled: it's the end!");
                    if (pages > pageNum){ // 현재 페이지가 총 페이지수 보다 적을때만 새로운 페이지를 로딩
                        // footer
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }

            }
        });

        mOnLoadMoreListener = new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore: ");
                mAdapter.addItem(null);
               // mAdapter.notifyDataSetChanged();
                pageNum ++;
                myClickHandler(true);

            }
        };

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    public void mOnClick(View view){
        switch (view.getId()){
            case R.id.imageButton : // write imageButton
                Intent i = new Intent(FolderActivity.this,FolderUpdateActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("folder_id",folder_id);
                i.putExtra("args",bundle1);
                startActivity(i);
                break;
            case R.id.imageButton4 : // delete imageButton
                // show a confirm dialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FolderActivity.this);
                    alertDialog.setTitle("폴더 삭제");
                    alertDialog.setMessage("정말 삭제 하시겠습니까?");
                    alertDialog.setIcon(R.drawable.garbage);

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        //  delete folder from local db
                        db.deleteFolder(folder_id);

                        // delete folder from server db
                        myClickHandler(false); // isList==false, since it's folder deleting process
                        finish();
                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                // Showing Alert Message
                alertDialog.show();
                break;
            case R.id.writeButton : // write imageButton
                // go to WriteActivity
                Intent p = new Intent(FolderActivity.this,WriteActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putInt("folder_id",folder_id);
                p.putExtra("args",bundle2);
                startActivity(p);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // set folder info
        folder = db.getFolder(folder_id);
        tv_name.setText(folder.getName());
        tv_desc.setText(folder.getDesc());
        String date = folder.getDate_start().substring(0,10)+" ~ "+folder.getDate_end().substring(0,10);
        tv_date.setText(date);

        // TODO: 2016. 10. 13. 서버에서 폴더정보 받아와서 리사이클러뷰 어뎁터 통해서 뿌려주기
        myClickHandler(true /* isList */);

//        // get Posting info from local db and set it into listView
//        ArrayList<Posting> postings = db.getMyPostings(folder_id);
//        mAdapter.addItem(postings);
//        mAdapter.notifyDataSetChanged();
    }

    // check if the network has connected before executing AsyncTask network connection to server
    public void myClickHandler(Boolean isList) {

        //save update to server if there is network connection
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (isList){
                new mURLConnection().execute(pageNum);
            }else{
                new mURLConnection().execute(0);
            }


        } else {
            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(FolderActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    // swipeRefresh !! pull to refresh
    @Override
    public void onRefresh() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        pageNum=1;
        myClickHandler(true);
    }

    private class mURLConnection extends AsyncTask<Integer, Void, HashMap<String,String> > {

        String TAG_SUB, stringUrl;
        Boolean isList; // true == fetch latest postings, false == delete folder
        int pageNum; // 현재 불러오는 페이지 번호
        int totalCount; // 총 포스팅 갯수
        int numOfRows = 5; // 한페이지의 최대 포스팅 갯수
        int currentCount; // 현재 페이지에 불러오는 포스팅 갯수

        @Override
        protected HashMap<String,String> doInBackground(Integer... integers) {
            HashMap<String, String> map = new HashMap<>();
            pageNum = integers[0];
            isList = integers[0]!=0; // pageNum==0 이면 deleteFolderProcess.
            if (isList){
                TAG_SUB = "[GetPostings]";
                stringUrl = "http://hanea8199.vps.phps.kr/get_postings.php?pageNum="+pageNum+"&numOfRows="+numOfRows;
            }else{
                TAG_SUB = "[DeleteFolderProcess]";
                stringUrl = "http://hanea8199.vps.phps.kr/deletefolder_process.php";
            }

            String postData = "folder_id="+String.valueOf(folder.getId());  // send folder id to server with POST method

            try {
                String data = downloadUrl(stringUrl,postData);

                if (isList){ // dealing with json data

                    String resultMsg="";
                    JSONObject bodyObject=null;

                    JSONObject result = new JSONObject(data);
                    JSONObject header = result.getJSONObject("header");
                    resultMsg = header.getString("resultMsg");
                    Log.d(TAG,  TAG_SUB + " onPostExecute: resultMsg "+resultMsg);

                    // "body"로받은 오브젝트가 String 이면 totalCount = 0, JsonObject 이면 totalCount 받아서 설정해주기.
                    Object body = result.get("body");
                    if (body instanceof String) {// It's a string
                        Log.d(TAG, "onPostExecute: bodyString "+ body);
                        totalCount = 0;
                        pages = 0;
                    } else if (body instanceof JSONObject) {// It's an object
                        bodyObject = (JSONObject) body;
                        totalCount = bodyObject.getInt("totalCount");
                        pages = bodyObject.getInt("pages");
                        Log.d(TAG, "doInBackground: pages "+pages);
                    }

                    if(totalCount > 0){
                        JSONArray postingsArray = bodyObject.getJSONArray("postings");
                        currentCount = postingsArray.length();

                        // get each posting object from postings, and set it into recyclerView.
                        for (int i=0; i<currentCount; i++){
                            JSONObject posting = postingsArray.getJSONObject(i);

                            map.put("posting_id"+i,posting.getString("posting_id"));
                            map.put("user_id"+i,posting.getString("user_id"));
                            map.put("modified"+i,posting.getString("modified"));
                            map.put("type"+i,posting.getString("type"));
                            map.put("title"+i,posting.getString("title"));
                            map.put("note"+i,posting.getString("note"));
                            map.put("image_path"+i,posting.getString("image_path"));
                            map.put("original_path"+i,posting.getString("original_path"));

                        }
                    }

                }else {
                    map.put("result",data);
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


            return map;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> s) {

            int resultCode=98;
            String str_result="";
            String resultMsg="";

            try{
                if (isList){ // if it's [GetPostings] process

                    // clear items which mAdapter has before adding new postings list.
                    if (pageNum==1){
                        mAdapter.clearItem();
                    }
                    //footer
                    if (isLoading){
                        // remove progressBar
                        mAdapter.removeFooter();
                    }

                    // get each posting object from HashMap, and set it into recyclerView.
                    for (int i=0; i<currentCount; i++){
                        // add posting to recycler view adapter
                        Posting posting1 = new Posting();
                        posting1.setPosting_id(s.get("posting_id"+i));
                        posting1.setFolder_id(String.valueOf(folder_id));
                        posting1.setUser_id(s.get("user_id"+i));
                        posting1.setModified(s.get("modified"+i));
                        posting1.setCreated(s.get("modified"+i));
                        posting1.setType(s.get("type"+i));
                        posting1.setPosting_title(s.get("title"+i));
                        posting1.setNote(s.get("note"+i));
                        if ( ! s.get("image_path"+i).equals("")){ // image_path 요소가 비어있지 않으면 posting1에 설정해줌.
                            posting1.setImage_path(s.get("image_path"+i));
                            posting1.setOriginal_path(s.get("original_path"+i));
                        }
                        mAdapter.addItem(posting1);
                    }
                    mAdapter.notifyDataSetChanged();
                    Log.d(TAG, TAG_SUB +" onPostExecute: postings "+s.toString());

                    // stopping swipe refresh
                    swipeRefreshLayout.setRefreshing(false);


                    //footer
                    isLoading=false;

                }else { // if it's [DeleteFolderProcess]

                    //check the whole result
                    JSONObject result = new JSONObject(s.get("result"));
                    Log.d(TAG,  TAG_SUB +"onPostExecute: result "+result.toString());

                    // print the number of folders at local DB after deleting
                    Log.d(TAG, TAG_SUB + " onPostExecute: folders "+String.valueOf(db.getMyFolders(folder.getOwner_id()).size()));
                }


            }catch (JSONException e){
                e.printStackTrace();
            }

        }

        private String downloadUrl(String myurl,String postData) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.

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
                Log.d(TAG, TAG_SUB + " The server response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public String readIt(InputStream stream) throws IOException {
            return IOUtils.toString(stream, "UTF-8");
        }


    }


}
