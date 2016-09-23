package com.example.parkhanee.mytravelapp;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * Created by parkhanee on 2016. 9. 22..
 */
public class FolderShareAdapter extends BaseAdapter implements Filterable {
    private Context context=null;
    private ArrayList<User> all_users;
    private ArrayList<User> users= new ArrayList<>();
    String TAG = "FolderShareAdapter";

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
        this.notifyDataSetChanged();
    }

    public void addItem(User user){
        users.add(user);
    }

    public void addItem(int position, User user){
        Log.d(TAG, "addItem: "+String.valueOf(position)+user.getUser_name());
        users.add(position,user);
    }

    @Override
    public View getView(final int position, View v, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.listview_foldershare, null);
            holder.tvUserName = (TextView) v.findViewById(R.id.textView36);
            holder.icon = (ImageView) v.findViewById(R.id.imageView5);
            holder.share = (ImageButton) v.findViewById(R.id.imageButton5);
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
                // TODO: 2016. 9. 22. set default app icon
                holder.icon.setImageResource(R.drawable.road);
            }
        }

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiver = users.get(position).getUser_name();
                Toast.makeText(context, receiver+"  "+users.get(position).getUser_id(), Toast.LENGTH_SHORT).show();
                // TODO: 2016. 9. 23. make a dialog and send GCM
            }
        });


        return v;
    }


    private static class ViewHolder{
        TextView tvUserName=null;
        ImageView icon=null;
        ImageButton share=null;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<User> FilteredArrList = new ArrayList<>();

                if (all_users == null) {
                    all_users = new ArrayList<>(users); // saves the original data in all_users
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = all_users.size();
                    results.values = all_users;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < all_users.size(); i++) {
                        String data = all_users.get(i).getUser_name();
                        if (data.toLowerCase().contains(constraint.toString())){ //startsWith(constraint.toString())) {
                            FilteredArrList.add(new User(all_users.get(i).getUser_id(),all_users.get(i).getUser_name(),all_users.get(i).getFB()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                users = (ArrayList<User>) filterResults.values ;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
