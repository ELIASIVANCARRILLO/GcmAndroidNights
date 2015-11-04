package com.ivansolutions.gcmandroidnights.receivers;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ivansolutions.gcmandroidnights.DetailActivity;
import com.ivansolutions.gcmandroidnights.R;
import com.ivansolutions.gcmandroidnights.javabeans.NotifItem;

import java.util.Date;

public class GcmIntenService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    public static final String TAG = "GCM Demo";
    Context context;
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GcmIntenService(String name) {
        super(name);
        context = this;
    }

    public GcmIntenService() {
        this("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
// TODO Auto-generated method stub
        Bundle extras = intent.getExtras();
        String msg = intent.getStringExtra("message");
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {

            //Guardar aqui el mensaje en una lista general

            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                NotifItem notification = new NotifItem(0, msg, new Date());//reemplazar

                sendNotification(notification);
            }
        }
        GcmReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(NotifItem item) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent;

        resultIntent = new Intent(context, DetailActivity.class);
        resultIntent.putExtra("alertId", item.getId());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DetailActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Nuevo mensaje")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(item.getContenido()))
                .setContentText(item.getContenido());

        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
