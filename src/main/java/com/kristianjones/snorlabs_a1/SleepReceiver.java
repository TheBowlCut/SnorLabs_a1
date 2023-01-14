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
    //Broadcast receiver looking for activity recognition broadcasts

    // Generic tag as Log identifier
    static final String TAG = SleepReceiver.class.getName();

    Intent countdownIntent;

    Long totalMilli;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Pull user defined countdown and store in totalMilli
        intent.getExtras();

        totalMilli = intent.getLongExtra("totalMilli",0);

        Log.d(TAG, "totalMilli= " + totalMilli);

        // Initialising a list, API driven list with timestamp, sleep confidence,  device motion,
        // ambient light level.
        List<SleepClassifyEvent> sleepClassifyEvents;

        // Extract the required info from the activity recognition broadcast (intent)
        sleepClassifyEvents = extractEvents(intent);

        Log.d(TAG, "sleepClassifyEvents = " + sleepClassifyEvents);

        if (SleepClassifyEvent.hasEvents(intent)) {
            // If the intent has the required sleepActivity info, this loop reviews the data.

            Log.d(TAG, "hasEvents True");

            // Is this duplication of line 40?
            List<SleepClassifyEvent> result = extractEvents(intent);

            // Initialising an array to store sleepConfidence values
            ArrayList<Integer> sleepConfidence = new ArrayList<>();

            for (SleepClassifyEvent event : result) {

                // Pulls out the sleepConfidence value from the SleepClassifyEventList
                int confTimerInt = event.getConfidence();

                // Add the sleep confidence value to the sleepConfidence array.
                sleepConfidence.add(event.getConfidence());

                countdownIntent = new Intent(context, CountdownService.class);

                countdownIntent.putExtra("totalMilli",totalMilli);

                context.startService(countdownIntent);

            }

        }
    }
}
