package com.vibes.androidsdkexampleapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vibes.androidsdkexampleapp.R;
import com.vibes.androidsdkexampleapp.activities.DeepLinkActivity;
import com.vibes.vibes.PushPayloadParser;
import com.vibes.vibes.Vibes;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
public class FMS extends FirebaseMessagingService {
    private final String kCustomSound = "custom_sound";
    private final String kDefaultSound = "default";
    private PushPayloadParser pushModel;
    private String collapse_key;

    /**
     * Method called to download the image used in the rich push notification.
     * @param imageUrl: String
     * @return Bitmap
     */
    private Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream in = connection.getInputStream();
            return BitmapFactory.decodeStream(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the custom sound. For this demo app there is only one custom sound. If this method is called
     * that means the user defined a custom sound in the push payload. Here we basically don't use
     * the value (resourceName) -> If the key exists we return R.raw.notif_sound.
     */
    private Integer getSoundFrom(String resource) {
        switch (resource) {
            // Add different cases if you have different push notification custom sounds.
            case kCustomSound: return R.raw.notif_sound;
            case kDefaultSound: return 0;
            default: return -1;
        }
    }

    /**
     * Method called to check whether some custom data exists. For this demo app, we broadcasts its
     * value. They're later one displayed in a Toast message.
     */
    private void handleCustomData() {
        if (pushModel.getCustomClientData() != null) {
            // Do something with the custom keys values.
        }
    }

    /**
     * Check if the sound specified in the push payload is a custom sound
     */
    private Boolean isValidCustomSound() {
        String sound = pushModel.getSound();
        if (sound != null) {
            Integer resource = getSoundFrom(sound);
            return resource != -1 && resource != 0;
        }
        return false;
    }

    /**
     * Check if the sound specified in the push payload is a default sound
     */
    private Boolean isDefaultSound() {
        String sound = pushModel.getSound();
        return sound != null && getSoundFrom(sound) == 0;
    }

    /**
     * Method called to create the Notification builder. Depending on the version of Android used,
     * it will use notification channel (>= Oreo) or not. Defining a custom sound depends also on
     * the version of Android. On >= Oreo, the sound must be defined in the notification channel.
     *
     * @param notificationManager: NotificationManager
     * @return NotificationCompat.Builder.
     */
    private NotificationCompat.Builder createBuilder(NotificationManager notificationManager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            if (pushModel.getPriority() != null) {
                importance = pushModel.getPriority();
            }
            String channelID = pushModel.getChannel();
            channelID = (channelID != null) ? channelID : "VIBES_CHANNEL";
            NotificationChannel notificationChannel = new NotificationChannel(channelID, channelID, importance);

            if (isValidCustomSound()) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                notificationChannel.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + getSoundFrom(pushModel.getSound())), att);
            }

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            return new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            if (pushModel.getPriority() != null) {
                builder.setPriority(pushModel.getPriority());
            }
            if (isValidCustomSound()) {
                builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + getSoundFrom(pushModel.getSound())));
            }
            return builder;
        }
    }

    /**
     * Method called to setup the application badging. On Android the badging is always incremental
     * (ex: 2 push notification with badge value = 5, results to show a badge with a value of 10).
     * On iOS, it's always the absolute value
     * (ex: 2 push notification with badge value = 5, results to show a badge with a value of 5).
     *
     * @param builder: NotificationCompat.Builder
     */
    private void handleBadging(NotificationCompat.Builder builder) {
        if (pushModel.getBadgeNumber() != null) {
            builder.setNumber(pushModel.getBadgeNumber());
        }
    }

    /**
     * Method called in case a rich content exists in the pushpaylod. If yes, it will download
     * the image and set it up in the builder.
     */
    private void handleRichPush(NotificationCompat.Builder builder) {
        if (pushModel.getRichPushImageURL() != null) {
            Bitmap imageDownloaded = getBitmapFromUrl(pushModel.getRichPushImageURL());
            builder.setLargeIcon(imageDownloaded)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(imageDownloaded));
        }
    }

    /**
     * Method used to retrieve the collapseKey from the push payload. if the collapseKey does not
     * exist then a random integer will be returned (collapsible is then disabled).
     */
    private String getCollapseKey() {
        return (collapse_key != null) ? collapse_key :
                String.valueOf((int)(System.currentTimeMillis() % Integer.MAX_VALUE));
    }


    /**
     * @see FirebaseMessagingService#onMessageReceived(RemoteMessage)
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        pushModel = Vibes.getInstance().createPushPayloadParser(message.getData());
        collapse_key = message.getCollapseKey();
        handleCustomData();
        if (!pushModel.isSilentPush()) {
            Intent intent = new Intent(this, DeepLinkActivity.class);
            intent.setAction(getCollapseKey());
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = createBuilder(mNotificationManager);
            builder.setContentTitle(pushModel.getTitle())
                    .setContentText(pushModel.getBody())
                    .setSmallIcon(R.drawable.firebase_icon)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            if (isDefaultSound()) {
                builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }
            handleBadging(builder);
            handleRichPush(builder);
            int val = (collapse_key != null) ? collapse_key.hashCode() :
                    (int)(System.currentTimeMillis() % Integer.MAX_VALUE);
            if (mNotificationManager != null) {
                mNotificationManager.notify(val, builder.build());
            }
        }
    }
}

