package com.favesolution.kemnaker.managers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.favesolution.kemnaker.R;
import com.favesolution.kemnaker.activities.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;

public class KemnakerGcmListenerService extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;
    public static final int REQUEST_NOTIFICATION = 1;
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString("title");
        String body = data.getString("body");
        createNotification(title,body);
    }
    private void createNotification(String title, String body) {
        Context context = getBaseContext();
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,REQUEST_NOTIFICATION,resultIntent,0);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());

    }
}
