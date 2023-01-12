package com.kristianjones.snorlabs_a1;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * This service starts, pauses and ends the countdown timer required
 * for the dynamic sleep alarm.
 * The countdown will only start once the user sleep confidence level is sufficient.
 * Only one countdown instance can be created at a time.
 *
 * IntentService to be used as opposed to Service as it uses a worker thread to handle all
 * requests, one at a time. This is typically the best option if you don't require that your
 * service handle multiple requests simultaneously
 *
 * A bound service is the server in a client-server interface. This allows components such as
 * activities to bind to the service, send requests, receives responses etc.
 */

public class CountdownService extends Service {

    // Generic tag as Log identifier
    static final String TAG = CountdownService.class.getName();

    public static final String countdownService = "com.kristianjones.snorlabs_a1.CountdownService";

    // Initialise parameters
    CountDownTimer countDownTimer;

    Intent countdownIntent;

    Long milliTimeLeft;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"OnCreate");
    }

    /* 12 Jan 2023
    Countdown timer is reading in, but multiple countdown timers are being started.
    This is because there is no boolean monitoring if an alarm has already started. Whenever a
    broadcast is being received, a new timer is starting.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");

        intent.getExtras();
        milliTimeLeft = intent.getLongExtra("totalMilli",0);

        countdownIntent = new Intent(countdownService);

        countDownTimer = new CountDownTimer(milliTimeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG,"Countdown Time Remaining: " + millisUntilFinished);
                //countdownIntent.putExtra("COUNTDOWN_TIMER",millisUntilFinished);
                //sendBroadcast(countdownIntent);

            }

            @Override
            public void onFinish() {
                Log.i(TAG,"Timer Finished");
                //countdownIntent.putExtra("COUNTDOWN_TIMER",0);
                //sendBroadcast(countdownIntent);

            }
        }.start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
        Log.d(TAG,"onDestroy");

    }
}
