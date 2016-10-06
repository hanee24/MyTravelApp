package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by parkhanee on 2016. 10. 6..
 */
public class ShareListAdapter  extends BaseAdapter {
    Context context = null;
    ArrayList<Share> shareArrayList = new ArrayList<>();
    ArrayList<User> userArrayList = new ArrayList<>();
    final static String TAG = "ShareListAdapter";

    public ShareListAdapter (Context context){
        this.context = context;
    }

    public void setItem(ArrayList<Share> shareArrayList, ArrayList<User> userArrayList ){
        this.shareArrayList = shareArrayList;
        this.userArrayList = userArrayList;
    }

    public void addItem(Share share, User user){
        shareArrayList.add(share);
        userArrayList.add(user);
    }

    public void clearItem(){
        shareArrayList.clear();
        userArrayList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View v, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.listview_sharelist, null);
            holder.tvUserName = (TextView) v.findViewById(R.id.textView36);
            holder.icon = (ImageView) v.findViewById(R.id.imageView5);
            holder.tvState = (TextView) v.findViewById(R.id.textView41);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag(); // we call the view created before to not create a view in each time
        }

        holder.tvUserName.setText(userArrayList.get(i).getUser_name());
        holder.tvState.setText(shareArrayList.get(i).getState());
        if (userArrayList.get(i).getFB()){
            //set fb icon on the image view
            holder.icon.setImageResource(R.drawable.com_facebook_button_icon_blue);
        }else{
            // set default app icon
            holder.icon.setImageResource(R.drawable.road);
        }

        return v;
    }

    private static class ViewHolder{
        TextView tvUserName = null;
        ImageView icon = null;
        TextView tvState = null;
    }
}
