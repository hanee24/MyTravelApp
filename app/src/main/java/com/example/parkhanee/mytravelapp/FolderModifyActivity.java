package com.example.parkhanee.mytravelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FolderModifyActivity extends AppCompatActivity {

    String str_name, str_desc,  str_start, str_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_modify);

        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("args");
        str_name = bundle.getString("name");
        str_desc = bundle.getString("desc");
        str_start = bundle.getString("start");
        str_end = bundle.getString("end");
    }
}
