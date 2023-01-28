package com.kristianjones.snorlabs_a1;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import static android.app.PendingIntent.FLAG_MUTABLE;

public class AlertReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        //This section is a Broadcast receiver listening out for the regular alarmManager.
        final String TRANSITIONS_RECEIVER_ACTION =
                BuildConfig.APPLICATION_ID + "TRANSITIONS_RECEIVER_ACTION";

        // Generic tag as Log identifier
        final String TAG = SleepActivity.class.getName();

        Log.d(TAG, "onReceive");
        startService(context,intent);

    }

    public void startService(Context context, Intent intent) {

        Intent intentService = new Intent(context, AlarmService.class);
        context.startService(intentService);

    }

}

