package com.example.parkhanee.mytravelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FolderActivity extends AppCompatActivity {

    int position;
    Folder folder;
    TextView tv_name,tv_desc, tv_date;
    ImageButton btn_edit;
    DBHelper db;
    String TAG = "FolderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        Intent a = getIntent();
        final Bundle bundle = a.getBundleExtra("args");
        position = bundle.getInt("position");

        // get folder from local DB
        db = new DBHelper(FolderActivity.this);
        db.getAllFolders();
//        Log.d(TAG, "position: "+position);
       // folder = db.getFolder(position);

        // initiate views
        tv_name = (TextView)findViewById(R.id.folderName);
        tv_desc = (TextView) findViewById(R.id.description);
        tv_date = (TextView) findViewById(R.id.textView25);
        btn_edit = (ImageButton) findViewById(R.id.imageButton);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FolderActivity.this,FolderUpdateActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position",position);
                i.putExtra("args",bundle1);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        folder = db.getFolder(position);
        tv_name.setText(folder.getName());
        tv_desc.setText(folder.getDesc());
        String date = folder.getDate_start()+" ~ "+folder.getDate_end();
        tv_date.setText(date);
    }

}
