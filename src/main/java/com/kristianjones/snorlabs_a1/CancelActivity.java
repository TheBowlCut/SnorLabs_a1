package com.kristianjones.snorlabs_a1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static android.app.PendingIntent.FLAG_MUTABLE;

public class CancelActivity extends AppCompatActivity {

    // Generic tag as Log identifier
    final String TAG = SleepActivity.class.getName();

    Button snoozeButton;
    Button finishButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel);

        finishButton = findViewById(R.id.finishButton);
        snoozeButton = findViewById(R.id.snoozeButton);

    }

    public void finishAlarm (View view) {

        // Cancels the service. OnDestroy, the service will shut down.
        Intent intentService = new Intent(getApplicationContext(),AlarmService.class);
        getApplicationContext().stopService(intentService);

        // To make sure everything is cancelled, set up alarmManager, intent and pendingIntent identical to StartAlarm, in order to cancel it.
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent alertIntent = new Intent(getApplicationContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, alertIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | FLAG_MUTABLE);
        alarmManager.cancel(pendingIntent);

        // Once service is shut down, the app will return to main activity.
        Intent cancelIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(cancelIntent);
    }
}
