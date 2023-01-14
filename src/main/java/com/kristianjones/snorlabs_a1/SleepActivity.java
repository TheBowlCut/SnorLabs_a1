package com.kristianjones.snorlabs_a1;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

    Bundle bundle;

    Integer timerHour;
    Integer timerMinute;
    Integer alarmHour;
    Integer alarmMinute;

    Intent alarmIntent;
    Intent timerIntent;

    Long timerHourMilli;
    Long timerMinuteMilli;
    Long totalMilli;

    PendingIntent timerPendingIntent;

    // Broadcast receiver to register whether user is asleep - SleepReceiver
    SleepReceiver sleepReceiver;

    Spinner settingSpinner;

    Task<Void> task;

    TextView descTextView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        // We are here - all alarms are set and permission has been granted. Set alarmActive = True
        alarmActive = true;

        // Declare all variables
        settingSpinner = findViewById(R.id.optionsSpinner4);
        descTextView = findViewById(R.id.descTextView3);
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

    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        registerReceiver(sleepReceiver, new IntentFilter(TRANSITIONS_RECEIVER_ACTION));
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

        timerIntent.putExtra("totalMilli",totalMilli);

        timerPendingIntent = PendingIntent.getBroadcast(this,
                0, timerIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);

        SleepSegmentRequest sleepSegmentRequest = null;

        task = ActivityRecognition.getClient(this).requestSleepSegmentUpdates(timerPendingIntent,
                SleepSegmentRequest.getDefaultSleepSegmentRequest());

    }

}
