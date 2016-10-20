package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
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

                if (p.getImage_path()==null){ // when there is no image
                    mHolder.imageView.setVisibility(View.GONE);
                    mHolder.tvTitle.setBackgroundColor(mHolder.view.getResources().getColor(R.color.myWhite));
                    mHolder.tvTitle.setTextColor(mHolder.view.getResources().getColor(R.color.myBlack));
                }else{
                    // set image from url (from my server) at imageView using Picasso library
                    mHolder.imageView.setVisibility(View.VISIBLE);
                    mHolder.tvTitle.setBackgroundColor(mHolder.view.getResources().getColor(R.color.transparentBlack));
                    mHolder.tvTitle.setTextColor(mHolder.view.getResources().getColor(R.color.myWhite));
                    Picasso.with(mHolder.view.getContext()).load(p.getImage_path()).into(mHolder.imageView); //set picture

                    mHolder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(view.getContext(),ImageActivity.class);
                            i.putExtra("original_path",path);
                            context.startActivity(i);
                        }
                    });
                }
            }

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

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            tvTitle = (TextView) itemLayoutView.findViewById(R.id.title);
            tvNote = (TextView) itemLayoutView.findViewById(R.id.note);
            tvCreated = (TextView) itemLayoutView.findViewById(R.id.created);
            tvUserId = (TextView) itemLayoutView.findViewById(R.id.userId);
            imageView = (ImageView) itemLayoutView.findViewById(R.id.imageView8);
            view = itemLayoutView;
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

}