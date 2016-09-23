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
 * Created by parkhanee on 2016. 9. 22..
 */
public class FolderShareAdapter extends BaseAdapter {
    private Context context=null;
    private ArrayList<User> users= new ArrayList<>();

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
    }

    public void addItem(User user){
        users.add(user);
    }

    public void addItem(int position, User user){
        users.add(position,user);
    }

    @Override
    public View getView(int position, View v, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.listview_foldershare, null);
            holder.tvUserName = (TextView) v.findViewById(R.id.textView36);
            holder.icon = (ImageView) v.findViewById(R.id.imageView5);
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
                // TODO: 2016. 9. 22. set default app icon ??
                holder.icon.setImageResource(R.drawable.com_facebook_button_icon_white);
            }
        }
        return null;
    }

    private static class ViewHolder{
        TextView tvUserName;
        ImageView icon;
    }

}
