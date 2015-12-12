package com.miranda.luis.activity_chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by Luis Fm on 10/12/2015.
 */
public class Notificaciones {
    private Context mContexto;
    private String mMessage;


    public Notificaciones(String message, Context context){
        this.mContexto=context;
        sendNotification(message);
    }


    private void sendNotification(String message) {

        Intent mIntent = new Intent(mContexto, Activity_chat.class);

        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContexto, 0 /* Request code */, mIntent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContexto)
                .setSmallIcon(R.drawable.sta1)
                .setContentTitle("Chat")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) mContexto.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
