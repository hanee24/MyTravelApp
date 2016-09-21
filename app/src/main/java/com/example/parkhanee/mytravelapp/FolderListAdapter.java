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
    public Folder getItem(int i) {
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
            holder.tvDate = (TextView) v.findViewById(R.id.textView19);
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
        }else{
            // TODO: 2016. 9. 9. 아직 생성한 여행폴더가 없습니다 문구
            //근데 그 문구르 ㄹ여기다 넣는게 아닌거같은데. 이건 아이템 하나마다 한번씩 다 지나가니까? 근데 아이템이 없으니까 한번만 지나가기때매 상관없나?? 안지나가나?
        }
        return v;
    }

    private static class ViewHolder{
        TextView tvName = null;
        TextView tvDesc = null;
        TextView tvDate = null;
    }


}
