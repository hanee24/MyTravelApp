package com.example.parkhanee.mytravelapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by parkhanee on 2016. 9. 22..
 */
public class FolderShareAdapter extends BaseAdapter implements Filterable {
    private Context context=null;
    private ArrayList<User> all_users;
    private ArrayList<User> users= new ArrayList<>();
    String TAG = "FolderShareAdapter";

    ProgressDialog dialog;
    HashMap<String,String> postDataParams;
    DBHelper db;

    public FolderShareAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void clearItem(){
        users.clear();
        this.notifyDataSetChanged();
    }

    public void addItem(User user){
        users.add(user);
    }

    public void addItem(int position, User user){
        Log.d(TAG, "addItem: "+String.valueOf(position)+user.getUser_name());
        users.add(position,user);
    }

    @Override
    public View getView(final int position, View v, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.listview_foldershare, null);
            holder.tvUserName = (TextView) v.findViewById(R.id.textView36);
            holder.icon = (ImageView) v.findViewById(R.id.imageView5);
            holder.share = (ImageButton) v.findViewById(R.id.imageButton5);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag(); // we call the view created before to not create a view in each time
        }

        if (users.size()>0){
            User user = users.get(position);
            holder.tvUserName.setText(user.getUser_name());
            if (user.getFB()){
                //set fb icon on the image view
                holder.icon.setImageResource(R.drawable.com_facebook_button_icon_blue);
            }else{
                // set default app icon
                holder.icon.setImageResource(R.drawable.road);
            }
        }

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String receiver = users.get(position).getUser_name();
                final String receiver_id = users.get(position).getUser_id();
                Toast.makeText(context, receiver+"  "+users.get(position).getUser_id(), Toast.LENGTH_SHORT).show();
                final int folder_id = FolderShareActivity.folder_id;

                // 2016. 9. 23. make a dialog and send GCM
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
//                adb.setView(alertDialogView);
                adb.setTitle(receiver+"님 에게 "+FolderShareActivity.folder.getName()+" 폴더 공유 신청을 보내시겠습니까?");
                adb.setIcon(android.R.drawable.ic_dialog_alert);

                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        int unixTime = (int) System.currentTimeMillis() / 1000;
                        postDataParams  = new HashMap<>();
                        postDataParams.put("share_id",String.valueOf(unixTime)); //set unix time as share id
                        postDataParams.put("folder_id",String.valueOf(folder_id));
                        postDataParams.put("owner_id",MainActivity.getUserId()); // 현재 로그인된 사용자
                        postDataParams.put("user_id",receiver_id); // 공유신청 받는 사용자 == 리스트뷰에서 클릭된 아이템에 해당하는 사용자

                        postDataParams.put("user_name",receiver);
                        postDataParams.put("isFB",String.valueOf(users.get(position).getFB()));


                        // send data to the server
                        myNetworkHandler("http://hanea8199.vps.phps.kr/sharefolder_process.php");



                    } });

                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    } });
                adb.show();
            }
        });


        return v;
    }


    private static class ViewHolder{
        TextView tvUserName=null;
        ImageView icon=null;
        ImageButton share=null;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<User> FilteredArrList = new ArrayList<>();

                if (all_users == null) {
                    all_users = new ArrayList<>(users); // saves the original data in all_users
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = all_users.size();
                    results.values = all_users;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < all_users.size(); i++) {
                        String data = all_users.get(i).getUser_name();
                        if (data.toLowerCase().contains(constraint.toString())){ //startsWith(constraint.toString())) {
                            FilteredArrList.add(new User(all_users.get(i).getUser_id(),all_users.get(i).getUser_name(),all_users.get(i).getFB()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                users = (ArrayList<User>) filterResults.values ;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    // check if the network has connected before executing AsyncTask network connection to server
    public void myNetworkHandler(String url) {

        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            // 폴더 공유 신청 하기
            new ShareFolderProcess().execute(url);

            // 폴더 공유 신청 보내면서 신청받는 user, share 정보 로컬디비에 저장하기
            db = new DBHelper(context);
            // TODO: 2016. 10. 1. do not addUser if exists
            db.addUser(new User(postDataParams.get("user_id"),postDataParams.get("user_name"),Boolean.valueOf(postDataParams.get("isFB"))));
            db.addShare(new Share(postDataParams.get("share_id"),postDataParams.get("folder_id"),postDataParams.get("user_id"),"Requested"));

        } else {
            Toast.makeText(context, "Cannot send invitation, No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public class ShareFolderProcess extends AsyncTask<String,Void,String> {
        String stringUrl;

        @Override
        protected void onPreExecute() {
            dialog = FolderShareActivity.dialog;
            dialog.setMessage("잠시만 기다려 주세요");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String string) {

            try{
                JSONObject result = new JSONObject(string);
                //check the whole result
                String str_result = result.toString();
                Log.d(TAG, "onPostExecute: "+str_result);

            }catch (JSONException e){
                e.printStackTrace();
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // 2016. 9. 30. show okay dialog
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle("폴더 공유 신청 완료");
            builder1.setMessage(postDataParams.get("user_name")+"님에게 폴더공유신청을 보냈습니다");
            builder1.setCancelable(true);
            builder1.setNeutralButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            stringUrl= strings[0];
            try {
                return downloadUrl(stringUrl);
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
                writer.write(getPostDataString(postDataParams));
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
}
