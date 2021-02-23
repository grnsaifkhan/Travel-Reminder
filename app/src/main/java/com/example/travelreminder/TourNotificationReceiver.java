package com.example.travelreminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.android.datatransport.runtime.Destination;

public class TourNotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "CHANNEL_ONE";
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationid",0);
        String travelName = intent.getStringExtra("travelName");
        String destination = intent.getStringExtra("destination");
        String travelDate = intent.getStringExtra("travelDate");
        String travelTime = intent.getStringExtra("travelTime");

        //call listview Fragment while notification is tabbed
        Intent mainIntent = new Intent(context,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,0,mainIntent,0
        );

        //Notification manager

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //for API 26 and above
            CharSequence channel_name = "My Notification";
            int important = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,channel_name,important);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.smallicon)
                .setContentTitle("Tour name: "+travelName)
                .setContentText("Destination: "+destination+"\n Date: "+travelDate+"\n Time: "+travelTime)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        //notify
        notificationManager.notify(notificationId,builder.build());

    }
}
