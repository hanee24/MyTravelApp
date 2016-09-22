package com.example.parkhanee.mytravelapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class FolderUpdateActivity extends AppCompatActivity {

    private EditText et_name;
    static EditText et_desc, et_start, et_end;
    private InputMethodManager imm;
    ProgressDialog dialog;
    HashMap<String, String> modifyFolderPostDataParams;
    static int folder_id;
    public static Folder folder;
    DBHelper db;
    String TAG = "FolderUpdateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_update);

        // get arguments from FolderActivity
        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("args");
        folder_id = bundle.getInt("folder_id");

        // get folder from local DB
        db = new DBHelper(FolderUpdateActivity.this);
        folder = db.getFolder(folder_id);

        //hide keyboard as default
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        setLayout();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                imm.hideSoftInputFromWindow(et_name.getWindowToken(), 0);
            }
        },100);

        ImageButton btn_start = (ImageButton) findViewById(R.id.imageButton2);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        ImageButton btn_end = (ImageButton) findViewById(R.id.imageButton3);
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
    }

    public void setLayout(){
        // initiate views
        et_name = (EditText) findViewById(R.id.editText6);
        et_desc = (EditText) findViewById(R.id.editText7);
        et_start = (EditText) findViewById(R.id.editText8);
        et_end = (EditText) findViewById(R.id.editText9);
        dialog = new ProgressDialog(FolderUpdateActivity.this);
        modifyFolderPostDataParams = new HashMap<>();

        // set folder information
        et_name.setText(folder.getName());
        et_desc.setText(folder.getDesc());
        et_start.setText(folder.getDate_start().substring(0,10));
        //날짜형식 서버랑 맞추기  2016-08-20 11:04:14
        //String s = folder.getDate_start()+" 00:00:00";
        et_start.setTag(folder.getDate_start());
        et_end.setText(folder.getDate_end().substring(0,10));
       // String e = folder.getDate_end()+" 00:00:00";
        et_end.setTag(folder.getDate_end());
    }

    public void mOnClick(View view){
        switch (view.getId()){
            case R.id.save :

                // TODO: 2016. 9. 22. save update to local db
                Folder folder1 = folder;
                folder1.setName(et_name.getText().toString());
                folder1.setDesc(et_desc.getText().toString());
                folder1.setDate_start(et_start.getTag().toString());
                folder1.setDate_end(et_end.getTag().toString());
                db.updateFolder(folder1);

                modifyFolderPostDataParams.put("folder_id",String.valueOf(folder.getId()));
                // get modified data from EditTexts
                modifyFolderPostDataParams.put("name",et_name.getText().toString());
                modifyFolderPostDataParams.put("desc",et_desc.getText().toString());
                modifyFolderPostDataParams.put("start",et_start.getTag().toString()); // TODO: 2016. 9. 20. where does it set the TAG ?
                modifyFolderPostDataParams.put("end",et_end.getTag().toString());
                myClickHandler();

                break;
            case R.id.cancel :
                finish();
                break;
            case R.id.button9 :
                Intent i = new Intent(FolderUpdateActivity.this,FolderShareActivity.class);
                Bundle args = new Bundle();
                args.putInt("folder_id",folder_id);
                i.putExtras(args);
                startActivity(i);
            default:
                Log.d(TAG, "mOnClick: DEFAULT?");
                break;
        }
    }

    // check if the network has connected before executing AsyncTask network connection to server
    public void myClickHandler() {

        //save update to server if there is network connection
        String stringUrl; //server url
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            stringUrl = "http://hanea8199.vps.phps.kr/modifyfolder_process.php";
            new FolderUpdateProcess().execute(stringUrl);
            
        } else {
            Toast.makeText(FolderUpdateActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

        private class FolderUpdateProcess extends AsyncTask<String, Void, String>{

            @Override
            protected void onPreExecute() {
                dialog.setMessage("데이터를 가져오는 중입니다");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }

            @Override
            protected void onPostExecute(String s) {

                int resultCode=99;
                String resultMsg="";

                try{
                    JSONObject result = new JSONObject(s);
                   //JSONObject header = result.getJSONObject("header");
                    resultCode = result.getInt("resultCode");

                    //check the whole result
                    resultMsg = result.toString();
                    Log.d(TAG, "onPostExecute: "+resultMsg);

                    if (resultCode==00) { // result is ok
                        Log.d(TAG, "onPostExecute: Result is OK");
                    }else{
                        Log.d(TAG, "onPostExecute: resultCode is not okay ? ");
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                // finish folderModifyActivity since the modification is done.
                finish();
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
                    writer.write(getPostDataString(modifyFolderPostDataParams));
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        Boolean start;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
//            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int day = c.get(Calendar.DAY_OF_MONTH);
            start = getArguments().getBoolean("start_date");
            String date;
            if (start){
                date = folder.getDate_start();
            }else {
                date = folder.getDate_end();
            }
            Log.d("DatePicker", "onCreateDialog: date "+date);
            int year = Integer.parseInt(date.substring(0,4));
            Log.d("DatePicker", "onCreateDialog: year"+String.valueOf(year)); //2015-23-23
            int month = Integer.parseInt(date.substring(5,7))-1; // TODO: 2016. 9. 20. why does it need to be subtracted by 1 ?
            int day = Integer.parseInt(date.substring(8,10));
            Log.d("DatePicker", "onCreateDialog: y m d "+year+month+day);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        // when user choose a date and clicked ok on DatePicker,
        // Do something with the date
        public void onDateSet(DatePicker view, int year, int month, int day) {

            String str_month,str_day;
            str_month = String.valueOf(month+1);
            str_day = String.valueOf(day);
            if (month+1<10){
                str_month = "0"+str_month;
            }
            if (day<10){
                str_day = "0"+str_day;
            }
            String date = String.valueOf(year) + "-"+str_month+"-"+str_day;

            if (start){ // set selected date as string at et_start
                et_start.setText(date);
                //날짜형식 서버랑 맞추기  2016-08-20 11:04:14
                et_start.setTag(String.valueOf(year)+"-"+str_month+"-"+str_day + " 00:00:00");

            }else{ // set selected date as string at et_end
                et_end.setText(date);
                //날짜형식 서버랑 맞추기  2016-08-20 11:04:14
                et_end.setTag(String.valueOf(year)+"-"+str_month+"-"+str_day + " 00:00:00");
            }
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        if (v.getId() == R.id.imageButton2){ // btn_start clicked
            args.putBoolean("start_date",true);
        }else { // btn_end clicked
            if (v.getId() != R.id.imageButton3){
                Log.d(TAG, "showDatePickerDialog: check parameter View");
            }
            args.putBoolean("start_date",false);
        }

        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}


