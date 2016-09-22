package com.example.parkhanee.mytravelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FolderShareActivity extends AppCompatActivity {

    static int folder_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_share);

        Intent i = getIntent();
        Bundle args = i.getExtras();
        folder_id = args.getInt("folder_id");


    }


}
