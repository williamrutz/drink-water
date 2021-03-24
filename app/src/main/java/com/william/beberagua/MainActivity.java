package com.william.drinkWater;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private EditText et_interval;
    private Button btn_notify;

    private int hour;
    private int minute;
    private int interval;

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
        if (isActivated) {
            btn_notify.setText(R.string.label_pause);
            btn_notify.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.black));

            int interval = preferences.getInt("interval", 0);
            int hour = preferences.getInt("hour", timePicker.getHour());
            int minute = preferences.getInt("minute", timePicker.getMinute());

            et_interval.setText(String.valueOf(interval));
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void notifyClick(View view) {
        String sInterval = et_interval.getText().toString();

        if (sInterval.isEmpty()) {
            Toast.makeText(this, R.string.error_msg_interval, Toast.LENGTH_LONG).show();
            return;
        }

        interval = Integer.parseInt(sInterval);
        hour = timePicker.getHour();
        minute = timePicker.getMinute();

        if (!isActivated) {
            isActivated = true;
            btn_notify.setText(R.string.label_pause);
            btn_notify.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.black));

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isActivated", isActivated);
            editor.putInt("hour", hour);
            editor.putInt("minute", minute);
            editor.putInt("interval", interval);
            editor.apply();

        } else {
            isActivated = false;
            btn_notify.setText(R.string.label_notify);
            btn_notify.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.teal_700));

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isActivated", isActivated);
            editor.remove("hour");
            editor.remove("minute");
            editor.remove("interval");
            editor.apply();
        }


    }
}