package com.vibes.androidsdkexampleapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vibes.androidsdkexampleapp.R;
import com.vibes.androidsdkexampleapp.views.DeepLinkActivity;
import com.vibes.vibes.PushPayloadParser;
import com.vibes.vibes.Vibes;

public class FMS extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Integer uniqueId = ((Long)System.currentTimeMillis()).intValue();
        Log.d("MSG", "--> Message Received: " + message.getData().toString());
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PushPayloadParser pushPayloadParser =
                Vibes.getInstance().createPushPayloadParser(message.getData());

        Intent intent = new Intent(this, DeepLinkActivity.class);
        intent.setAction(uniqueId.toString());

        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "VIBES_CHANNEL", importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }



        builder.setContentTitle(pushPayloadParser.getTitle())
                .setContentText(pushPayloadParser.getBody())
                .setSmallIcon(R.drawable.firebase_icon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        Log.d("Notification", "Notification -->" + builder.build().toString());
        mNotificationManager.notify(uniqueId, builder.build());
    }
}

