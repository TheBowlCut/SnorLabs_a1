package com.kristianjones.snorlabs_a1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.SleepClassifyEvent;

import java.util.List;

import static com.google.android.gms.location.SleepClassifyEvent.extractEvents;

public class SleepReceiver extends BroadcastReceiver {
    // Generic tag as Log identifier
    static final String TAG = SleepActivity.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        List<SleepClassifyEvent> sleepClassifyEvents;

        sleepClassifyEvents = extractEvents(intent);

        Log.d(TAG, "sleepClassifyEvents = "  + sleepClassifyEvents);
    }
}
