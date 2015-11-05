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
import com.ivansolutions.gcmandroidnights.AppContext;
import com.ivansolutions.gcmandroidnights.DetailActivity;
import com.ivansolutions.gcmandroidnights.MainActivity;
import com.ivansolutions.gcmandroidnights.R;
import com.ivansolutions.gcmandroidnights.javabeans.NotifItem;

public class GcmIntenService extends IntentService {

    public static MainActivity activityToRefresh;
    public static final int NOTIFICATION_ID = 1;
    Context context;

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

                NotifItem notification = new NotifItem(msg);
                AppContext.addItem(notification);

                sendNotification(notification);

                //Actualizar la UI
                if (activityToRefresh != null) {
                    activityToRefresh.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activityToRefresh.refreshUI();
                        }
                    });
                }
            }
        }
        GcmReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(NotifItem item) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(context, DetailActivity.class);
        resultIntent.putExtra("text", item.getContenido());

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
