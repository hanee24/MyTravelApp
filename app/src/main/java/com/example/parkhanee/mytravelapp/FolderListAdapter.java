package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by parkhanee on 2016. 9. 8..
 */
public class FolderListAdapter extends BaseAdapter{
    private ArrayList<Folder> folderArrayList = new ArrayList<>();
    private ArrayList<Boolean> isShared = new ArrayList<>();
    private Context context=null;

    public FolderListAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return folderArrayList.size();
    }

    @Override
    public Folder getItem(int i) {
        return folderArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addItem(Folder folder, Boolean s){
        folderArrayList.add(folder);
        isShared.add(s);
    }

    public void addItem(int position,Folder folder,Boolean s){
        folderArrayList.add(position,folder);
        isShared.add(s);
    }

    public void clearItem(){
        folderArrayList.clear();
    }

    @Override
    public View getView(int position, View v, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.listview_folderlist, null);
            holder.tvName = (TextView) v.findViewById(R.id.folder_name);
            holder.tvDesc = (TextView) v.findViewById(R.id.folder_desc);
            holder.tvDate = (TextView) v.findViewById(R.id.textView19);
            holder.tvShared = (TextView) v.findViewById(R.id.tvShareState);
            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag(); // we call the view created before to not create a view in each time
        }
        if (folderArrayList.size()>0){
            Folder f = folderArrayList.get(position);
            holder.tvName.setText(f.getName());
            holder.tvDesc.setText(f.getDesc());
            String start = f.getDate_start().substring(0,10);
            String end = f.getDate_end().substring(0,10);
            String str = start+" ~ "+end;
            holder.tvDate.setText(str);
            holder.tvShared.setVisibility(View.INVISIBLE);
            if (isShared.get(position)){
                holder.tvShared.setVisibility(View.VISIBLE);
                holder.tvShared.setText("shared"); // TODO: 2016. 10. 5. set state which can be obtained from local db 'share' table..
            }
        }
        return v;
    }

    private static class ViewHolder{
        TextView tvName = null;
        TextView tvDesc = null;
        TextView tvDate = null;
        TextView tvShared = null;
    }


}
