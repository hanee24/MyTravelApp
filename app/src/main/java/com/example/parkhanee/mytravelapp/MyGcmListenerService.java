package com.example.parkhanee.mytravelapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.HashMap;

/**
 * Created by parkhanee on 2016. 9. 28..
 */
public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    DBHelper dbHelper;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     *             Set 형태로 GCM 으로 받은 데이터 payload 이다.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "onMessageReceived: ");
        
        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         *
         *     폴더공유신청 받은정보를 로컬디비에 저장하기
         */
        String action = data.getString("action");
        if (action.equals("Request")){
            dbHelper = new DBHelper(this);
            dbHelper.addUser(new User(data.getString("owner_id"),data.getString("owner_name"),Boolean.valueOf(data.getString("isFB")),data.getString("lat"),data.getString("lng")));
            dbHelper.addFolder(new Folder(Integer.parseInt(data.getString("folder_id")),data.getString("folder_name"),data.getString("owner_id"),data.getString("desc"),data.getString("start"),data.getString("end"),data.getString("created")));
            dbHelper.addShare(new Share(data.getString("share_id"),data.getString("folder_id"),getUserId(),data.getString("state")));
            dbHelper.close();
            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */
            // GCM으로 받은 메세지를 디바이스에 알려주는 sendNotification()을 호출한다.
            sendRequestNotification(data.getString("owner_name"),data.getString("share_id"));
        }else if (action.equals("Response")){
            String share_id = data.getString("share_id");
            String state = data.getString("state");
            String folder_id = data.getString("folder_id");
            String folder_name = data.getString("folder_name");
            String user_id = data.getString("user_id");
            String user_name = data.getString("user_name");

            Log.d(TAG, "onMessageReceived: Response [folder_name : "+folder_name+", state : "+state+", user_name : "+user_name+"]");

            dbHelper = new DBHelper(this);
            dbHelper.updateShare(new Share(share_id,folder_id,user_id,state));
            dbHelper.close();

            sendResponseNotification(user_name,folder_id,folder_name,state);
            // TODO: 2016. 10. 9.  로컬디비에 updateShare 하기, notification 구현(sendResponseNotification();)

        }



        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * 실제 디바에스에 GCM 으로부터 받은 메세지를 알려주는 함수이다. 디바이스 Notification Center에 나타난다.
     * Create and show a simple notification containing the received GCM message.
     */
    private void sendRequestNotification(String owner_name,String share_id) {
        Log.d(TAG, "sendRequestNotification: ");
        String title = "폴더 공유 요청";
        String message = owner_name+"님으로 부터 폴더 공유요청이 도착하였습니다.";
        Intent intent = new Intent(this, ViaNotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("share_id",share_id);

        showNotification(title, message, intent, 11);
    }

    private void sendResponseNotification(String user_name, String folder_id,String folder_name, String state){
        Log.d(TAG, "sendRequestNotification: ");
        if (state.equals("Accepted")){
            state = "수락";
        }else{
            state = "거절";
        }
        String title = "폴더 공유 "+state ;
        String message = user_name+"님이 "+folder_name+" 폴더 공유요청을 "+state+"하였습니다.";
        Intent intent = new Intent(this, FolderUpdateActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle args= new Bundle();
        args.putInt("folder_id",Integer.valueOf(folder_id));
        intent.putExtra("args",args);

        showNotification(title, message, intent, 12);
    }

    private void showNotification(String title, String message, Intent intent, int id){
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.mytravel)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.mytravel))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id, notificationBuilder.build());
    }

    private String getUserId(){
        SharedPreferences sharedPreferences =  getSharedPreferences(getString(R.string.MyPREFERENCES), Context.MODE_PRIVATE);
        String str = sharedPreferences.getString(getString(R.string.userIdKey),null);
        return str;
    }
}
