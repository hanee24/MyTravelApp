package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by parkhanee on 2016. 10. 7..
 */
public class FolderContentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Posting> postings = new ArrayList<>();
    private final String TAG = "FolderContentsAdapter";

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context context;
    private String type;

    public FolderContentsAdapter(Context context, ArrayList<Posting> postings) {
        this.postings = postings;
    }

    public FolderContentsAdapter(Context context){
        this.context = context;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_folder_contents, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_foldercontents_footer, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder){
            final ViewHolder mHolder = (ViewHolder) holder;
            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData
            if (postings.size()>0){
                Posting p = postings.get(position);

                String iii = p.getPosting_title();
                mHolder.tvTitle.setText(iii);
                mHolder.tvNote.setText(p.getNote());
                mHolder.tvCreated.setText(p.getCreated());
                mHolder.tvUserId.setText(p.getUser_id());

                final String path = p.getOriginal_path();
                type = p.getType();
                final String posting_id = p.getPosting_id();
                  Integer.parseInt(p.getFolder_id());

                if (p.getImage_path()==null){ // when there is no image
                    mHolder.imageView.setVisibility(View.GONE);
                    mHolder.tvTitle.setBackgroundColor(mHolder.view.getResources().getColor(R.color.myWhite));
                    mHolder.tvTitle.setTextColor(mHolder.view.getResources().getColor(R.color.myBlack));
                }else{
                    // set image from url (from my server) at imageView using Picasso library
                    mHolder.imageView.setVisibility(View.VISIBLE);
                    mHolder.tvTitle.setBackgroundColor(mHolder.view.getResources().getColor(R.color.transparentBlack));
                    mHolder.tvTitle.setTextColor(mHolder.view.getResources().getColor(R.color.myWhite));
                    if (p.getImage_path().equals("null")){
                        // poi인데 이미지가 없는 경우 , no Image Availble이미지 설정 해주기
                        mHolder.imageView.setImageResource(R.drawable.noimageavailable);
                    }else {
                        // get image from url and set it onto the imageView
                        Picasso.with(mHolder.view.getContext()).load(p.getImage_path()).into(mHolder.imageView); //set picture
                    }


                    mHolder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (type.equals("poi")){
                                Intent i = new Intent(view.getContext(),NearbyD3Activity.class);
                                // posting id = contentId(6~7) + unixTime(2)
                                //  e.g. unixTime 1477659150 contentId 133353  postingId  13335350
                                String contentId = posting_id.substring(0,posting_id.length()-2);
                                i.putExtra("contentId",Integer.parseInt(contentId));
                                context.startActivity(i);
                            }else {
                                Intent i = new Intent(view.getContext(),ImageActivity.class);
                                i.putExtra("original_path",path);
                                context.startActivity(i);
                            }

                        }
                    });
                }
            }


            mHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // dialog 수정 / 삭제 ?
                    final CharSequence[] items={"게시물 수정하기","게시물 삭제하기"};
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("수정 또는 삭제");
                    alertDialog.setIcon(R.drawable.garbage);
                    alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final Posting p = postings.get(position);
                            switch (i){
                                case 0 : // 수정
                                    Intent intent = new Intent(context,WriteActivity.class);
                                    Bundle args = new Bundle();
                                    args.putInt("folder_id",Integer.parseInt(p.getFolder_id()));
                                    args.putInt("posting_id",Integer.parseInt(p.getPosting_id()));
                                    if (p.getImage_path()!=null){
                                        args.putString("image_path",p.getOriginal_path());
                                    }
                                    intent.putExtra("args",args);
                                    context.startActivity(intent);
                                    break;
                                case 1 : // 삭제
                                    // 정말 삭제하시겠습니까 dialog

                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                    alertDialog.setTitle("폴더 삭제");
                                    alertDialog.setMessage("정말 삭제 하시겠습니까?");
                                    alertDialog.setIcon(R.drawable.garbage);

                                    // Setting Positive "Yes" Button
                                    alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            //  delete the posting from local db
                                            mHolder.helper.deletePosting(Integer.parseInt(p.getPosting_id()));

                                            // delete the posting from server db
                                            myClickHandler(p.getPosting_id());
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
                            }
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();


                    return true;
                }
            });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    // viewHolder for items
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle = null;
        public TextView tvNote = null;
        public TextView tvCreated = null;
        public TextView tvUserId = null;
        public ImageView imageView = null;
        public View view;
        public DBHelper helper = null;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            tvTitle = (TextView) itemLayoutView.findViewById(R.id.title);
            tvNote = (TextView) itemLayoutView.findViewById(R.id.note);
            tvCreated = (TextView) itemLayoutView.findViewById(R.id.created);
            tvUserId = (TextView) itemLayoutView.findViewById(R.id.userId);
            imageView = (ImageView) itemLayoutView.findViewById(R.id.imageView8);
            view = itemLayoutView;
            helper = new DBHelper(view.getContext());
        }
    }

    // viewHolder for loading progress bar
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

    public void addItem(Posting posting){
        postings.add(posting);
    }

    public void clearItem(){
        postings.clear();
        notifyDataSetChanged();
    }

    public void removeFooter(){
        postings.remove(postings.size()-1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return postings.get(position)==null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return postings.size();
    }

    public void myClickHandler(String posting_id) {

        //save update to server if there is network connection
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DeletePostingProcess().execute(posting_id);
        } else {
            Toast.makeText(context, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public class DeletePostingProcess extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String postData = "posting_id="+strings[0];
            String url = "http://hanea8199.vps.phps.kr/deleteposting_process.php";
            try {
                return  downloadUrl(url,postData);
            } catch (IOException e) {
                e.printStackTrace();
                return "Unable to retrieve web page. URL may be invalid.";
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
                Log.d(TAG," The server response is: " + response);
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

        @Override
        protected void onPostExecute(String s) {
            int resultCode=99;
            String resultMsg="";
            String msg;
            int totalCount=0;


            JSONObject result = null;
            try {
                result = new JSONObject(s);
                resultCode = result.getInt("resultCode");

                //check the whole result
                resultMsg = result.toString();
                Log.d(TAG, "onPostExecute: "+ resultMsg);

                if (resultCode!=00){
                    Toast.makeText(context, "sync failed", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            notifyDataSetChanged();
        }
    }


}