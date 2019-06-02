package com.techdoom.yuppu;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;



public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_message = remoteMessage.getNotification().getBody();

        String click_action = remoteMessage.getNotification().getClickAction();


        String from_user_id = remoteMessage.getData().get("from_user_id");

        String GROUP_KEY_METROCHAT = "techdoom.yuppu_TARGET_NOTIFICATION";

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this,getString(R.string.default_notification_channel_id))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notification_title)
                        .setVibrate(new long[]{1000,1000,1000,1000,1000})
                        .setContentText(notification_message)


                        .setGroup(GROUP_KEY_METROCHAT);

        int numMessages= 0;

        mBuilder.setContentText(notification_message).setNumber(++numMessages);




        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("from_user_id", from_user_id);


        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);




        int mNotificationId = 1;

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }
}