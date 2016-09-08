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
    private Context context=null;

    public FolderListAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return folderArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return folderArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addItem(Folder folder){
        folderArrayList.add(folder);
    }

    public void addItem(int position,Folder folder){
        folderArrayList.add(position,folder);
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
            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag(); // we call the view created before to not create a view in each time
        }
        if (folderArrayList.size()>0){
            holder.tvName.setText(folderArrayList.get(position).getName());
            holder.tvDesc.setText(folderArrayList.get(position).getDesc());
        }else{
          // 아직 생성한 여행폴더가 없습니다 문구
        }
        return v;
    }

    private static class ViewHolder{
        TextView tvName = null;
        TextView tvDesc = null;
    }


}
