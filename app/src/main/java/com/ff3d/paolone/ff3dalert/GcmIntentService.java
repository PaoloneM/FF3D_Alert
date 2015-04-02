package com.ff3d.paolone.ff3dalert;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by paolomorgano on 01/04/15.
 */
public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    // Notification ID to allow for future updates
    private static final int MY_NOTIFICATION_ID = 1;

    // Notification Text Elements
    private final CharSequence tickerText = "FF3D Alert Ticker";
    private final CharSequence contentTitle = "New FF3D ALert";

    // Notification Action Elements
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;

    // Notification Sound and Vibration on Arrival
    private long[] mVibratePattern = { 0, 200, 200, 300 };


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());

                //showToast(extras.getString("message"));
                showNotification(extras.getString("message"));

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

   protected void showNotification(final String message){


        mNotificationIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://mp2quality.manutencoop.it/zone/"));
        mContentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                mNotificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                getApplicationContext());
        notificationBuilder.setTicker(tickerText);
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_warning);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentTitle(contentTitle);
        notificationBuilder.setContentText("Content Text");
        notificationBuilder.setNumber(1);
        notificationBuilder.setContentIntent(mContentIntent);
        notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notificationBuilder.setVibrate(mVibratePattern);
        notificationBuilder.setStyle(new NotificationCompat.InboxStyle()
                        .addLine(message + " 1")
                        .addLine(message + " 2")
                        .setBigContentTitle("FF3D Alert details:")
                        .setSummaryText("+1 more")
        );


        // Pass the Notification to the NotificationManager:
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MY_NOTIFICATION_ID,
                notificationBuilder.build());


    }
}