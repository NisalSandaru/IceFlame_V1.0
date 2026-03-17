package com.nisal.iceflame.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.nisal.iceflame.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "iceflame_notifications";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        android.util.Log.d("FCM_TOKEN", token);
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {

        super.onMessageReceived(message);

        android.util.Log.d("FCM_DEBUG", "Message received!");
        String title = null;
        String body = null;

        android.util.Log.d("FCM_DEBUG", "Title: " + title);
        android.util.Log.d("FCM_DEBUG", "Body: " + body);

        if (message.getNotification() != null) {
            title = message.getNotification().getTitle();
            body = message.getNotification().getBody();
        }

        // Support data payload (pro way)
        if (title == null) title = message.getData().get("title");
        if (body == null) body = message.getData().get("body");

        if (title != null && body != null) {
            showNotification(title, body);
        }
    }

    private void showNotification(String title, String message) {

        createNotificationChannel();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_no_bg_ice_flame)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat manager =
                NotificationManagerCompat.from(this);

        if (androidx.core.app.ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
        ) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return;
        }

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "IceFlame Notifications",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }
}