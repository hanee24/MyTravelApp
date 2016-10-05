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
            // TODO: 2016. 10. 5. accept 하고 폴더액티비티로 갔다가 폴더목록으로 돌아오면 state가 바뀐게 반영이 안되어있는 문제
            case R.id.reject : // 공유 거부 하고 로컬디비에 저장
                share.setState("Denied");
                dbHelper.updateShare(share);
//                Intent o = new Intent(ViaNotificationActivity.this,FolderListFragment.class);
//                startActivity(o);
                finish();
                dbHelper.close();
                break;
        }
    }
}
