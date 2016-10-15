package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by parkhanee on 2016. 10. 7..
 */
public class FolderContentsAdapter extends RecyclerView.Adapter<FolderContentsAdapter.ViewHolder> {
    private ArrayList<Posting> postings = new ArrayList<>();

    public FolderContentsAdapter(ArrayList<Posting> postings) {
        this.postings = postings;
    }

    public FolderContentsAdapter(){}

    // Create new views (invoked by the layout manager)
    @Override
    public FolderContentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_folder_contents, null);
        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FolderContentsAdapter.ViewHolder holder, int position) {
        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        if (postings.size()>0){
            Posting p = postings.get(position);

            String iii = p.getPosting_title();
            holder.tvTitle.setText(iii);
            holder.tvNote.setText(p.getNote());
            holder.tvCreated.setText(p.getCreated());
            holder.tvUserId.setText(p.getUser_id());

            if (p.getImage_path()==null){
                holder.imageView.setVisibility(View.GONE);
                holder.tvTitle.setBackgroundColor(holder.view.getResources().getColor(R.color.myWhite));
                holder.tvTitle.setTextColor(holder.view.getResources().getColor(R.color.myBlack));
            }else{
                // set image (which the server has) at imageView using Picasso library
                holder.imageView.setVisibility(View.VISIBLE);
                holder.tvTitle.setBackgroundColor(holder.view.getResources().getColor(R.color.transparentBlack));
                holder.tvTitle.setTextColor(holder.view.getResources().getColor(R.color.myWhite));
                Picasso.with(holder.view.getContext()).load(p.getImage_path()).into(holder.imageView); //set picture
            }
        }
    }

    @Override
    public int getItemCount() {
        return postings.size();
    }

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

    public void addItem(Posting posting){
        postings.add(posting);
    }

    public void addItem(ArrayList<Posting> postings){
        this.postings = postings;
    }

    public void clearItem(){
        postings.clear();
        notifyDataSetChanged();
    }

}