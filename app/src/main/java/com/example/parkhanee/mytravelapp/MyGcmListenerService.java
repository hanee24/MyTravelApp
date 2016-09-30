package com.example.parkhanee.mytravelapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by parkhanee on 2016. 9. 28..
 */
public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";


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
        String title = data.getString("title");
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Message: " + message);


        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        // GCM으로 받은 메세지를 디바이스에 알려주는 sendNotification()을 호출한다.
        sendNotification(title, message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * 실제 디바에스에 GCM 으로부터 받은 메세지를 알려주는 함수이다. 디바이스 Notification Center에 나타난다.
     * Create and show a simple notification containing the received GCM message.
     * @param title GCM message title
     * @param message GCM message received.
     */
    private void sendNotification(String title, String message) {
        Log.d(TAG, "sendNotification: ");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.road)
//                .setContentTitle(title)
//                .setTicker(title)
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.road))
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(11 /* ID of notification */, notificationBuilder.build());



//        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Notification.Builder mBuilder = new Notification.Builder(this);
//        mBuilder.setSmallIcon(R.drawable.road);
//        mBuilder.setTicker("Notification.Builder");
//        mBuilder.setWhen(System.currentTimeMillis());
//        mBuilder.setNumber(10);
//        mBuilder.setContentTitle(title);
//        mBuilder.setContentText(message);
//        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
//        mBuilder.setContentIntent(pendingIntent);
//        mBuilder.setAutoCancel(true);
//        mBuilder.setPriority(Notification.PRIORITY_MAX);
//        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.road));
//        nm.notify(111, mBuilder.build());

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mCompatBuilder = new NotificationCompat.Builder(this);
        mCompatBuilder.setSmallIcon(R.drawable.road);
        mCompatBuilder.setTicker("NotificationCompat.Builder");
        mCompatBuilder.setWhen(System.currentTimeMillis());
        mCompatBuilder.setNumber(10);
        mCompatBuilder.setContentTitle("NotificationCompat.Builder Title");
        mCompatBuilder.setContentText("NotificationCompat.Builder Massage");
        mCompatBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mCompatBuilder.setContentIntent(pendingIntent);
        mCompatBuilder.setAutoCancel(true);

        nm.notify(222, mCompatBuilder.build());


        // TODO: 2016. 9. 29. check the notification process
    }
}
