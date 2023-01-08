package com.kristianjones.snorlabs_a1;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmActivity extends AppCompatActivity {

    Bundle bundle;
    Bundle finalBundle;

    Calendar calendar;

    Integer hourNow;
    Integer minutesNow;
    Integer alarmHour;
    Integer alarmMinute;
    Integer timerHour;
    Integer timerMinute;

    Intent dynIntent;
    Intent alarmIntent;

    TimePicker timePicker;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        timePicker = findViewById(R.id.timePicker);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
            hourNow = calendar.get(Calendar.HOUR_OF_DAY);
            minutesNow = calendar.get(Calendar.MINUTE);
        }

        timePicker.setHour(hourNow);
        timePicker.setMinute(minutesNow);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setAlarm (View view) {

        dynIntent = getIntent();
        bundle = dynIntent.getExtras();

        timerHour = bundle.getInt("Hours");
        timerMinute = bundle.getInt("Mins");

        alarmHour = timePicker.getHour();
        alarmMinute = timePicker.getMinute();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            calendar.set(Calendar.HOUR_OF_DAY,alarmHour);
            calendar.set(Calendar.MINUTE,alarmMinute);
            calendar.set(Calendar.SECOND,0);
        }

        alarmIntent = new Intent(getApplicationContext(),MainActivity.class);

        finalBundle = new Bundle();

        finalBundle.putInt("timerH",timerHour);
        finalBundle.putInt("timerM",timerMinute);
        finalBundle.putInt("alarmH",alarmHour);
        finalBundle.putInt("alarmM",alarmMinute);

        alarmIntent.putExtras(finalBundle);
        startActivity(alarmIntent);

    }
}

