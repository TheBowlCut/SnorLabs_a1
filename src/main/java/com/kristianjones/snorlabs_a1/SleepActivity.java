package com.kristianjones.snorlabs_a1;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.SleepClassifyEvent;
import com.google.android.gms.location.SleepSegmentRequest;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.google.android.gms.location.SleepClassifyEvent.extractEvents;

public class SleepActivity extends AppCompatActivity {

    // Generic tag as Log identifier
    static final String TAG = SleepActivity.class.getName();

    // Review check for devices with Android 10 (29+). Already covered for 28 and below.
    // Need to be covered for 29 and beyond.
    // Action fired when transitions are triggered.
    private final String TRANSITIONS_RECEIVER_ACTION =
            BuildConfig.APPLICATION_ID + "TRANSITIONS_RECEIVER_ACTION";

    Boolean alarmActive;
    Boolean debugMode;

    // timerActive is static so it can be accessed in SleepReceiver,
    static Boolean timerActive;

    Bundle bundle;

    Integer confLimit;
    Integer timerHour;
    Integer timerMinute;
    Integer alarmHour;
    Integer alarmMinute;

    Intent alarmIntent;
    Intent timerIntent;
    Intent countdownIntent;

    Long timerHourMilli;
    Long timerMinuteMilli;
    Long totalMilli;
    Long timerTimeLeft;

    PendingIntent timerPendingIntent;

    // Broadcast receiver to register whether user is asleep - SleepReceiver
    SleepReceiver sleepReceiver;

    Spinner settingSpinner;

    Task<Void> task;

    TextView descTextView;
    TextView titleTextView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        // We are here - all alarms are set and permission has been granted. Set alarmActive = True
        alarmActive = true;

        // Timer will not have started unless sleep confidence is recorded, initialise as false.
        timerActive = false;

        // Declare all variables
        settingSpinner = findViewById(R.id.optionsSpinner4);
        descTextView = findViewById(R.id.descTextView3);
        titleTextView = findViewById(R.id.titleTextView3);
        sleepReceiver = new SleepReceiver();

        // Set settings array adaptor, linked to the 'settings' string in strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.settings, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        //Set spinner to arrayAdaptor
        settingSpinner.setAdapter(adapter);

        // Read in intent from Alarm activity and pull bundle
        alarmIntent = getIntent();
        bundle = alarmIntent.getExtras();

        // Initialise all values within the bundle for use within activity
        timerHour = bundle.getInt("timerH");
        timerMinute = bundle.getInt("timerM");
        alarmHour = bundle.getInt("alarmH");
        alarmMinute = bundle.getInt("alarmM");

        //Set regular alarm
        //First, update TextView with latest wake up time
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, alarmHour);
        c.set(Calendar.MINUTE, alarmMinute);
        c.set(Calendar.SECOND, 0);
        updateRegText(c);

        //Initialise alarm service
        StartAlarm alarm = new StartAlarm(getApplicationContext(), alarmHour, alarmMinute, 0);

        //Initialise countdown - first need to convert data to milliseconds.
        if (Build.VERSION.SDK_INT >= 31) {
            convertToMilli(timerHour, timerMinute);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void cancelAll (View view) {

        // Cancels all services
        // Cancels the alarm service. OnDestroy, the service will shut down.
        Intent alarmIntentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(alarmIntentService);

        // Cancels the countdown service. OnDestroy, the service will shut down.
        if (timerActive) {
            timerActive = false;
            timerPendingIntent.cancel();
            unregisterReceiver(sleepReceiver);
            Intent countdownIntentService = new Intent(getApplicationContext(), CountdownService.class);
            getApplicationContext().stopService(countdownIntentService);
        } else {
            timerPendingIntent.cancel();
            unregisterReceiver(sleepReceiver);
        }

        // Once service is shut down, the app will return to main activity.
        Intent cancelIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(cancelIntent);
    }

    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        registerReceiver(sleepReceiver, new IntentFilter(TRANSITIONS_RECEIVER_ACTION));
        registerReceiver(dynamicReceiver, new IntentFilter(CountdownService.countdownService));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        //registerReceiver(dynamicReceiver, new IntentFilter(CountdownService.countdownService));
    }

    public void updateRegText(Calendar c) {
        // Setting text view to time selected by user
        String timeText = "Latest wake up: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        descTextView.setText(timeText);
    }

    @RequiresApi(api = 31)
    public void convertToMilli(Integer hours, Integer minutes) {

        // Convert the timer values into milliseconds for the countdown service
        timerHourMilli = (long) (hours*3.6e6);
        timerMinuteMilli = (long) (minutes*3.6e6);

        // Combine milliseconds of hours and minutes
        totalMilli = timerHourMilli + timerMinuteMilli;

        startTracking();
    }

    @RequiresApi(api = 31)
    public void startTracking() {

        // Activate sleep segment requests using pending intent to listen to activityRecognition API
        // Broadcast receiver with Intent filter TRANSITIONS_RECEIVER_ACTION - this is linked to the
        // sleepReceiver. When this intent is activated through the pendingIntent, it activates the
        // sleep receiver.

        timerIntent = new Intent(TRANSITIONS_RECEIVER_ACTION);
        Log.d(TAG,"timerActive = " + timerActive);

        timerIntent.putExtra("totalMilli",totalMilli);
        timerIntent.putExtra("timerActive = ",timerActive);

        timerPendingIntent = PendingIntent.getBroadcast(this,
                0, timerIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);

        SleepSegmentRequest sleepSegmentRequest = null;

        task = ActivityRecognition.getClient(this).requestSleepSegmentUpdates(timerPendingIntent,
                SleepSegmentRequest.getDefaultSleepSegmentRequest());

    }

    public class SleepReceiver extends BroadcastReceiver {
        //Broadcast receiver looking for activity recognition broadcasts

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "totalMilli = " + totalMilli);
            Log.d(TAG, "timerActive = " + timerActive);

            //DEBUG MODE - When just wanting to check whether code works, this will set the sleep
            // confidence level to 1. When not in DEBUG MODE, this will set the receiver to
            // X (95 as of 15/01/2023)
            debugMode = true;

            if (debugMode) {
                confLimit = 1;
            } else {
                confLimit = 95;
            }

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

                    //if there is no timer started (!timerActive), activate timer.
                    if (confTimerInt > confLimit && !timerActive) {

                        // Set timerActive as true, this should stop countdown timers being
                        // set in the future
                        timerActive = true;
                        countdownIntent = new Intent(context, CountdownService.class);
                        countdownIntent.putExtra("totalMilli", totalMilli);
                        context.startService(countdownIntent);

                        // if there is a timer started, go here
                    }

                }

            }

        }

    }

    public BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {

            //Listens to broadcast from CountdownService.
            //Receives the amount of time left and displays it in a text view

            timerTimeLeft = intent.getLongExtra("countdownTimer",0);

            if (timerTimeLeft > 0) {
                titleTextView.setText("Sleep timer left: " + String.valueOf(timerTimeLeft/1000));
            } else {
                titleTextView.setText("Timer complete");
            }

        }
    };
}
