package com.kristianjones.snorlabs_a1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        // Declare all variables
        settingSpinner = findViewById(R.id.optionsSpinner4);

        // Set settings array adaptor, linked to the 'settings' string in strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.settings, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        //Set spinner to arrayAdaptor
        settingSpinner.setAdapter(adapter);

        alarmIntent = getIntent();
        bundle = alarmIntent.getExtras();

        timerHour = bundle.getInt("timerH");
        timerMinute = bundle.getInt("timerM");
        alarmHour = bundle.getInt("alarmH");
        alarmMinute = bundle.getInt("alarmM");

    }
}
