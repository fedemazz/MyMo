package com.prova.promemorialong;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    public static String ID = "ID";
    public static String NOTIFICATION = "notification";
    private String TAG = "Notification Receiver";
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive: ricevuta notifica");
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(ID, 0);
        nm.notify(id, notification);
    }
}
