package com.example.parkhanee.mytravelapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * Created by parkhanee on 2016. 9. 6..
 */
public class AreaFragment extends Fragment {

    private ExpandableListView listView;
    private AreaBasedListViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_area,container,false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        adapter = new AreaBasedListViewAdapter(getContext());
        listView.setAdapter(adapter);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                return false;
            }
        });
    }
}
