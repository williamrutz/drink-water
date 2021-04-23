package com.william.beberagua;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.william.beberagua.NotificationPublisher;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private EditText et_interval;
    private Button btn_notify;

    private boolean isActivated = false;

    private SharedPreferences preferences;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timePicker = findViewById(R.id.timePicker);
        et_interval = findViewById(R.id.et_interval);
        btn_notify = findViewById(R.id.btn_notify);

        timePicker.setIs24HourView(true);
        preferences = getSharedPreferences("db_water", Context.MODE_PRIVATE);

        isActivated = preferences.getBoolean("isActivated", false);

        setupUI(isActivated, preferences);

    }

    private void setupUI(boolean activated, SharedPreferences preferences) {

        if (activated) {
            btn_notify.setText(R.string.label_pause);
            btn_notify.setBackgroundResource(R.drawable.bg_button_background);

            int interval = preferences.getInt("interval", 0);
            int hour = preferences.getInt("hour", timePicker.getCurrentHour());
            int minute = preferences.getInt("minute", timePicker.getCurrentMinute());

            et_interval.setText(String.valueOf(interval));
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        } else {
            btn_notify.setText(R.string.label_notify);
            btn_notify.setBackgroundResource(R.drawable.bg_button_background_accent);
        }

    }

    private void alert(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    private boolean intervalIsValid() {
        String sInterval = et_interval.getText().toString();

        if (sInterval.isEmpty()) {
            alert(R.string.error_msg_interval);
            return false;
        }

        if (sInterval.equals("0")) {
            alert(R.string.error_msg_zero_value);
            return false;
        }

        return true;
    }

    private void updateStorage(boolean added, int interval, int hour, int minute) {
        SharedPreferences.Editor editor = preferences.edit();

        if (added) {
            editor.putBoolean("isActivated", added);
            editor.putInt("hour", hour);
            editor.putInt("minute", minute);
            editor.putInt("interval", interval);
        } else {
            editor.putBoolean("isActivated", isActivated);
            editor.remove("hour");
            editor.remove("minute");
            editor.remove("interval");
        }
        editor.apply();

    }

    private void setupNotification(boolean added, int interval, int hour, int minute) {
        Intent notificationIntent = new Intent(MainActivity.this, NotificationPublisher.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (added) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION_ID, 1);
            notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION, "Hora de tomar água");

            PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), (interval * 60 * 1000), broadcast);
        } else {

            PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, notificationIntent, 0);

            alarmManager.cancel(broadcast);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void notifyClick(View view) {

        if (!intervalIsValid()) return;

        String sInterval = et_interval.getText().toString();

        int interval = Integer.parseInt(sInterval);
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        if (!isActivated) {
            isActivated = true;

            setupUI(isActivated, preferences);

            updateStorage(isActivated, interval, hour, minute);

            setupNotification(isActivated, interval, hour, minute);

            alert(R.string.alert_actived);


        } else {
            isActivated = false;

            setupUI(isActivated, preferences);

            updateStorage(isActivated, 0, 0, 0);

            setupNotification(isActivated, 0, 0, 0);

            alert(R.string.alert_disabled);


        }


    }
}