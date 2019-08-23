package com.piworks.chitchat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Firebase_Message extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sendNotification(remoteMessage.getNotification().getBody());
    }

    private void sendNotification(String body) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        int notificationId = 100;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Intent ix = new Intent(this,MainActivity.class);
        ix.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pd = PendingIntent.getActivity(this , 100,ix,PendingIntent.FLAG_UPDATE_CURRENT);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            makeNotificationChannel(channelId, channelName, importance,this);


            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder(this, channelId);
            // the second parameter is the channel id.
            // it should be the same as passed to the makeNotificationChannel() method

            notification
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // can use any other icon
                    .setContentTitle("New Message")
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(alarmsound)
                    .setVibrate(new long[]{1000, 1000})

                    .setNumber(3)// this shows a number in the notification dots
                    .setContentIntent(pd);


            assert notificationManager != null;
            notificationManager.notify(100, notification.build());
            // it is better to not use 0 as notification id, so used 1.

        }

        else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("New Message")
                    .setContentText(body)
                    .setSound(alarmsound)
                    .setAutoCancel(true)

                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))   // for color of icon
                    .setVibrate(new long[]{1000, 1000})
                    .setContentIntent(pd);

            //  TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            //  stackBuilder.addNextIntent(intent);
            // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
            //0,
            //PendingIntent.FLAG_UPDATE_CURRENT
            //  );
            // mBuilder.setContentIntent(resultPendingIntent);

            notificationManager.notify(notificationId, mBuilder.build());

        }}


    @RequiresApi(api = Build.VERSION_CODES.O)

    void makeNotificationChannel(String id, String name, int importance,Context context)
    {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

}

