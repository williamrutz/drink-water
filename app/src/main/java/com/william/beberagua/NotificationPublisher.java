package com.william.beberagua;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.william.beberagua.MainActivity;
import com.william.beberagua.R;

public class NotificationPublisher extends BroadcastReceiver {
    public static final String KEY_NOTIFICATION = "key_notification";
    public static final String KEY_NOTIFICATION_ID = "key_notification_id";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intentNotification = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentNotification, 0);

        String message = intent.getStringExtra(KEY_NOTIFICATION);
        int id = intent.getIntExtra(KEY_NOTIFICATION_ID, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = getNotification(context, message, notificationManager, pendingIntent);
        notificationManager.notify(id, notification);
    }

    private Notification getNotification(Context context, String content, NotificationManager manager, PendingIntent intent) {
        Notification.Builder builder = new Notification.Builder(context.getApplicationContext())
                .setContentText(content)
                .setContentIntent(intent)
                .setTicker("Alerta")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel_drink_water =
                    new NotificationChannel(channelId, "Channel Drink Water", NotificationManager.IMPORTANCE_DEFAULT);

            manager.createNotificationChannel(channel_drink_water);
            builder.setChannelId(channelId);

        }

        return builder.build();


    }
}
