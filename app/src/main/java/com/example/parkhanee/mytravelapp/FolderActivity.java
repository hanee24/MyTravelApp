package com.example.parkhanee.mytravelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class FolderActivity extends AppCompatActivity {

    String str_name;
    String str_desc;
    String str_start;
    String str_end;
    String date;
    TextView tv_name,tv_desc, tv_date;
    ImageButton btn_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        Intent a = getIntent();
        final Bundle bundle = a.getBundleExtra("args");

        str_name = bundle.getString("name");
        str_desc = bundle.getString("desc");
        str_start = bundle.getString("start");
        str_end = bundle.getString("end");

        tv_name = (TextView)findViewById(R.id.folderName);
        tv_desc = (TextView) findViewById(R.id.description);
        tv_date = (TextView) findViewById(R.id.textView25);
        btn_edit = (ImageButton) findViewById(R.id.imageButton);

        tv_name.setText(str_name);
        tv_desc.setText(str_desc);
        date = str_start+" ~ "+str_end;
        tv_date.setText(date);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FolderActivity.this,FolderModifyActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("name",str_name);
                bundle1.putString("desc",str_desc);
                bundle1.putString("start",str_start);
                bundle1.putString("end",str_end);
                i.putExtra("args",bundle1);
                startActivity(i);
            }
        });
    }
}
