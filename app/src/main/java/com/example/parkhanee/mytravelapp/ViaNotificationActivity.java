package com.example.parkhanee.mytravelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ViaNotificationActivity extends AppCompatActivity {

    DBHelper dbHelper;
    String TAG = "ViaNotificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_via_notification);

        Intent i = getIntent();
        String share_id = i.getStringExtra("share_id");
        dbHelper = new DBHelper(this);
        Share share = dbHelper.getShare(Integer.valueOf(share_id));
        dbHelper.close();

        //폴더이름, 보낸 사용자 이름 보여주고 수락/거부 버튼


        Log.d(TAG, "onCreate: "+share.toString());

    }
}
