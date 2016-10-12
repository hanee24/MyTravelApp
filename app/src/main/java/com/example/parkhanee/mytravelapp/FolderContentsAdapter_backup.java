package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by parkhanee on 2016. 10. 7..
 */
public class FolderContentsAdapter_backup extends BaseAdapter {
    private ArrayList<Posting> postings = new ArrayList<>();
    private Context context;

    public FolderContentsAdapter_backup(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return postings.size();
    }

    @Override
    public Object getItem(int i) {
        return postings.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
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

    @Override
    public View getView(int i, View v, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.listview_folder_contents, null);
            holder.tvTitle = (TextView) v.findViewById(R.id.title);
            holder.tvNote = (TextView) v.findViewById(R.id.note);
            holder.tvCreated = (TextView) v.findViewById(R.id.created);
            holder.tvUserId = (TextView) v.findViewById(R.id.userId);
            holder.db = new DBHelper(context);
            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag(); // we call the view created before to not create a view in each time
        }

        if (postings.size()>0){
            Posting p = postings.get(i);
            String iii = p.getPosting_title();
            holder.tvTitle.setText(iii);
            holder.tvNote.setText(p.getNote());
            holder.tvCreated.setText(p.getCreated());
            holder.tvUserId.setText(p.getUser_id());
        }

        return v;
    }

    private static class ViewHolder{

        TextView tvTitle = null;
        TextView tvNote = null;
        TextView tvCreated = null;
        TextView tvUserId = null;
        DBHelper db = null;
    }
}
