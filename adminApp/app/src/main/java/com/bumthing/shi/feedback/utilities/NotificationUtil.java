package com.bumthing.shi.feedback.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.bumthing.shi.feedback.MainActivity;
import com.bumthing.shi.feedback.R;

import data.Constants;

public class NotificationUtil {
    private static final String NOTIFICATION_CHANNEL_ID="Mast channel hai";
    private static final int NOTIFICATION_ID=890;

    public static void makeNotification(Context context, int notifCount, String notifText) {

        int requestID = (int) System.currentTimeMillis();
        Intent notificationIntent = new Intent(context.getApplicationContext(), MainActivity.class);
        notificationIntent.putExtra(MainActivity.UPDATE_AVAILABLE_KEY,1);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    Constants.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        if(notifText.length()>20)
            notifText = notifText.substring(0,20) + "...";
        if(notifCount>1){
            notifText = notifText + "...and " + (notifCount-1)+ " more";
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(notifText)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_dialog_info);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
