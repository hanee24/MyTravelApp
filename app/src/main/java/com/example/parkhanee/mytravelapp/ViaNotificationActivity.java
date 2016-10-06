package com.example.parkhanee.mytravelapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViaNotificationActivity extends AppCompatActivity {

    DBHelper dbHelper;
    String TAG = "ViaNotificationActivity";
    static Share share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_via_notification);

        Intent i = getIntent();
        String share_id = i.getStringExtra("share_id");
        dbHelper = new DBHelper(this);
        share = dbHelper.getShare(Integer.valueOf(share_id));

        //폴더이름, 보낸 사용자 이름 보여주고 수락/거부 버튼
        TextView tv = (TextView) findViewById(R.id.textView39);
        tv.setText(share.toString());

    }

    public void mOnClick(View view){

        switch (view.getId()){
            case R.id.accept : // 공유 수락 하고 로컬디비에 저장
                share.setState("Accepted");
                dbHelper.updateShare(share);

                Toast.makeText(ViaNotificationActivity.this, "폴더 공유를 수락하였습니다", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(ViaNotificationActivity.this,FolderActivity.class);
                Bundle args = new Bundle();
                args.putInt("folder_id",Integer.parseInt(share.getFolder_id()));
                i.putExtra("args",args);
                startActivity(i);
                finish();
                dbHelper.close();
                break;
            case R.id.reject : // 공유 거부 하고 로컬디비에 저장
                share.setState("Denied");
                dbHelper.updateShare(share);
//                dbHelper.deleteFolder(Integer.parseInt(share.getFolder_id()));
                // TODO: 2016. 10. 6. 바로 지우지말고 일단 DINiED 상태로 남겨서 '거부한 목록' 보이기. 거기서 지워야 로컬디비에서 정보 완전히 지우기.

                Toast.makeText(ViaNotificationActivity.this, "폴더 공유를 거부하였습니다", Toast.LENGTH_SHORT).show();

                // TODO: 2016. 10. 6. 거부한 정보 서버에 업뎃.

                finish();
                dbHelper.close();
                break;
        }
    }
}
