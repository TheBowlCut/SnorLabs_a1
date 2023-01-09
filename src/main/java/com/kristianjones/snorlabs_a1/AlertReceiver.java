package com.kristianjones.snorlabs_a1;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import static android.app.PendingIntent.FLAG_MUTABLE;

public class AlertReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        //This section checking if cancelling pending intents work in this section
        final String TRANSITIONS_RECEIVER_ACTION =
                BuildConfig.APPLICATION_ID + "TRANSITIONS_RECEIVER_ACTION";

        Intent sleepIntent = new Intent(TRANSITIONS_RECEIVER_ACTION);
        PendingIntent dynPendingIntent = PendingIntent.getBroadcast(context,
                0, sleepIntent, PendingIntent.FLAG_CANCEL_CURRENT | FLAG_MUTABLE);
        dynPendingIntent.cancel();

        // end of pending intent try1

        //Intent regIntent = new Intent(context, com.kristianjones.snorlabs_a1.AlertReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, regIntent,
        //        PendingIntent.FLAG_CANCEL_CURRENT | FLAG_MUTABLE);
        //pendingIntent.cancel();

        startService(context,intent);

    }

    public void startService(Context context, Intent intent) {

        Intent intentService = new Intent(context, AlarmService.class);
        context.startService(intentService);

    }

}

