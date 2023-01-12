package com.kristianjones.snorlabs_a1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.SleepClassifyEvent;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.SleepClassifyEvent.extractEvents;

public class SleepReceiver extends BroadcastReceiver {
    // Generic tag as Log identifier
    static final String TAG = SleepReceiver.class.getName();

    Intent countdownIntent;

    Long totalMilli;

    @Override
    public void onReceive(Context context, Intent intent) {

        intent.getExtras();

        totalMilli = intent.getLongExtra("totalMilli",0);

        Log.d(TAG, "totalMilli= " + totalMilli);

        List<SleepClassifyEvent> sleepClassifyEvents;

        sleepClassifyEvents = extractEvents(intent);

        Log.d(TAG, "sleepClassifyEvents = " + sleepClassifyEvents);

        if (SleepClassifyEvent.hasEvents(intent)) {

            Log.d(TAG, "hasEvents True");

            List<SleepClassifyEvent> result = extractEvents(intent);

            ArrayList<Integer> sleepConfidence = new ArrayList<>();

            for (SleepClassifyEvent event : result) {

                int confTimerInt = event.getConfidence();

                sleepConfidence.add(event.getConfidence());

                countdownIntent = new Intent(context, CountdownService.class);

                countdownIntent.putExtra("totalMilli",totalMilli);

                context.startService(countdownIntent);

            }

        }
    }
}
