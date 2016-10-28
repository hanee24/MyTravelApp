package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by parkhanee on 2016. 8. 11..
 */
public class myArrayListAdapter extends BaseAdapter {

    private ArrayList<Item> itemArrayList = new ArrayList<>();
    private Context context = null;

    public myArrayListAdapter(Context context){
        this.context= context;
    }

    @Override
    public int getCount() {
        return itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.listview_nearby, null);
            holder.tvTitle = (TextView) v.findViewById(R.id.title);
            holder.tvDesc = (TextView) v.findViewById(R.id.desc);
            holder.tvDist = (TextView) v.findViewById(R.id.dist);
            holder.tvCat = (TextView) v.findViewById(R.id.cat);
            holder.imageView = (ImageView) v.findViewById(R.id.imageView);
            holder.tvNumber = (TextView) v.findViewById(R.id.number);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag(); // we call the view created before to not create a view in each time
        }

        holder.tvTitle.setText(itemArrayList.get(position).getTitle());
        holder.tvDesc.setText(itemArrayList.get(position).getDesc());
        holder.tvDist.setText(String.valueOf(itemArrayList.get(position).getDist()));
        holder.tvNumber.setText(String.valueOf(position+1));
        //set category textView
        int cat = itemArrayList.get(position).getCat();
        String strCat="기타";
        switch (cat){
            case -1 : strCat="전체";
                break;
            case 12: strCat="관광지";
                break;
            case 39 : strCat="음식";
                break;
            case 32 : strCat="숙박";
                break;
            case 15 : strCat="행사|공연|축제";
                break;
            case 14: strCat = "문화시설";
                break;
            case 25: strCat = "여행코스";
                break;
            case 28 : strCat="레포츠";
                break;
            case 38 : strCat="쇼핑";
                break;
            default:
                break;
        }
        holder.tvCat.setText(strCat);
        String picture = itemArrayList.get(position).getPicture();
        if (!picture.equals("null")){
            Picasso.with(context).load(picture).into(holder.imageView); //set picture
        }else{
            holder.imageView.setImageDrawable(v.getResources().getDrawable(R.drawable.compass));
        }

        return v;
    }

    private static class ViewHolder {
        TextView tvTitle = null;
        TextView tvDesc = null;
        TextView tvDist=null;
        TextView tvCat = null;
        ImageView imageView = null;
        TextView tvNumber =null;
    }

    public void addItem(Item item){
        itemArrayList.add(item);
    }

    public void clearItem(){
        itemArrayList.clear();
        notifyDataSetChanged();
    }

}
