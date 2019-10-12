package com.prova.promemorialong;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class NotificationHelper {

    private int id;
    private String title;
    private Long dateTime;

    public NotificationHelper(String title, int id, Long dateTime) {
        this.id = id;
        this.title = title;
        this.dateTime = dateTime;
    }

    private String TAG = "Notification Helper";

    private int getId() {
        return id;
    }

    private String getTitle() {
        return title;
    }

    private Long getDateTime() {
        return dateTime;
    }

    public void createNotification(Context context, NotificationHelper notificationHelper,int priority) {
        //Metodo per la creazione della notifica
        //Inizialmente definisco 3 pending intent:

        //Intent per l'evento di click sulla notifica
        //Al click visualizzerò la main activity
        Intent intentActivity = new Intent(context, MainActivity.class);
        //getActivity significa che il pending intent raggiungerà un'activity
        PendingIntent contentIntent = PendingIntent.getActivity(context, notificationHelper.getId(), intentActivity, 0);

        //Intent per l'evento di click sul bottone "in corso"
        //Passo anche un extra per avere l'id del promemoria
        //Questo pendingIntent porterà ad un Broadcast receiver
        Intent onGoingIntent = new Intent(context, onGoingReceiver.class);
        onGoingIntent.putExtra("ID", notificationHelper.getId());
        //getBroadcast significa che raggiungerà un Broadcast Receiver
        PendingIntent onGoingPendingIntent = PendingIntent.getBroadcast(context, notificationHelper.getId(), onGoingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //aggiungere l'intent per posporre il tutto
        //far aprire l'activity dove modifica il promemoria
        //in quell'activity deve partire sendOnchannel1 cosi da creare una nuova notifica
        Intent postponeIntent = new Intent(context, ModifyTaskActivity.class);
        postponeIntent.putExtra("id", notificationHelper.getId());
        PendingIntent postponePendingIntent = PendingIntent.getActivity(context, notificationHelper.getId(), postponeIntent, 0);

        String channel = String.valueOf(priority);

        //quando creo una notifica essa è schedulata nel canale consono alla sua priorità.
        //Quattro canali per quattro priorità, cosi da poter gestire le notifiche in base anche alle priorità
        Notification notification = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle(notificationHelper.getTitle())
                .setContentText(context.getResources().getString(R.string.happens_now))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setWhen(notificationHelper.getDateTime())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[] { 1000, 1000})
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_directions_run_black_24dp, context.getString(R.string.ongoing), onGoingPendingIntent)
                .addAction(R.drawable.ic_postpone_24dp, context.getString(R.string.postpone), postponePendingIntent)
                .setAutoCancel(true)
                .build();


        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("ID", notificationHelper.getId());
        notificationIntent.putExtra("notification", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationHelper.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, getDateTime(), pendingIntent);
        Log.d(TAG, "createNotification: "+ getDateTime());
    }


    public void deleteNotification(Context context, NotificationHelper notificationHelper, int priority) {

        String channel = String.valueOf(priority);
        Notification notification = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle(notificationHelper.getTitle())
                .setContentText(context.getResources().getString(R.string.happens_now))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setColor(Color.blue(110))
                .setAutoCancel(true)
                .build();

        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("ID", notificationHelper.getId());
        notificationIntent.putExtra("notification", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationHelper.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void modifyNotification (Context context, NotificationHelper notificationHelper,int priority) {
        deleteNotification(context,notificationHelper,priority);
        createNotification(context,notificationHelper,priority);
    }

}


