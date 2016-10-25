package com.example.parkhanee.mytravelapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;

public class AreaBasedD1Activity extends AppCompatActivity {

    private ExpandableListView listView;
    private AreaBasedListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_based_d1);

        listView = (ExpandableListView) findViewById(R.id.expandableListView);
        adapter = new AreaBasedListViewAdapter(AreaBasedD1Activity.this);
        listView.setAdapter(adapter);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                return false;
            }
        });
    }


}
