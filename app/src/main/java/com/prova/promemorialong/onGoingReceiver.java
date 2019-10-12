package com.prova.promemorialong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class onGoingReceiver extends BroadcastReceiver {

DatabaseHelper db;

    public void onReceive(Context context, Intent intent) {

        db = new DatabaseHelper(context);
        int id = intent.getIntExtra("ID",0);
        db.updateStateOngoing(id);
        Toast.makeText(context, context.getString(R.string.task_become_ongoing), Toast.LENGTH_LONG).show();
    }
}