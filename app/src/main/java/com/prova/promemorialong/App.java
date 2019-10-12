package com.prova.promemorialong;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

public class App extends Application {
    public static final String CHANNEL_0 = "0";
    public static final String CHANNEL_1 = "1";
    public static final String CHANNEL_2 = "2";
    public static final String CHANNEL_3 = "3";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("App","Create Notification Channel");
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //Channel 0
            NotificationChannel channel0 = new NotificationChannel(
                    CHANNEL_0,
                    getString(R.string.no_priority),
                    NotificationManager.IMPORTANCE_LOW
            );
            channel0.setDescription(getString(R.string.channel_0_description));

            //Channel 1
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1,
                    getString(R.string.priority_1),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription(getString(R.string.channel_1_description));

            //Channel 2
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2,
                    getString(R.string.priority_2),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription(getString(R.string.channel_2_description));

            //Channel 3
            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3,
                    getString(R.string.priority_3),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel3.setDescription(getString(R.string.channel_3_description));

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel0);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);

        }
    }
}
