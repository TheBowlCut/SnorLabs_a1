package com.kristianjones.snorlabs_a1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Calendar;

public class SleepActivity extends AppCompatActivity {

    // Generic tag as Log identifier
    static final String TAG = SleepActivity.class.getName();

    Bundle bundle;

    Integer timerHour;
    Integer timerMinute;
    Integer alarmHour;
    Integer alarmMinute;

    Intent alarmIntent;

    Spinner settingSpinner;

    TextView descTextView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        // Declare all variables
        settingSpinner = findViewById(R.id.optionsSpinner4);
        descTextView = findViewById(R.id.descTextView3);

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
    }

    private void updateRegText(Calendar c) {
        String timeText = "Latest wake up: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        descTextView.setText(timeText);
    }
}
