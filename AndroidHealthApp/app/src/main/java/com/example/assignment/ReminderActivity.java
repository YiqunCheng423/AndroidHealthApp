package com.example.assignment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ReminderActivity extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("remind")){
            String notificationMessage = "Please enter your daily record";
            Toast.makeText(context, notificationMessage, Toast.LENGTH_LONG).show();
        }
    }
}
