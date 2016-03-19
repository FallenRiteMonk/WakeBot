package com.fallenritemonk.wakebot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.WindowManager;
import android.widget.TimePicker;

import com.fallenritemonk.wakebot.dismisshandler.DismissTypeEnum;
import com.fallenritemonk.wakebot.utils.AlarmAdapter;
import com.fallenritemonk.wakebot.utils.AlarmReceiver;

import java.util.Calendar;

public class AlarmManagerActivity extends AppCompatActivity {
    private final String LOG_TAG = "AlarmManagerActivity";

    private final static int PERMISSIONS_REQUEST_WAKE_LOCK = 1000;

    private AlarmAdapter alarmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initScreen();

        setVolumeControlStream(AudioManager.STREAM_ALARM);
    }

    private void initScreen() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newAlarm();
            }
        });

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.alarm_list_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        alarmAdapter = AlarmAdapter.getInstance(this);
        alarmAdapter.loadAlarms();
        mRecyclerView.setAdapter(alarmAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                alarmAdapter.removeAlarm(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void newAlarm() {
        Calendar mcurrentTime = Calendar.getInstance();
        mcurrentTime.add(Calendar.MINUTE, 1);
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, android.R.style.Theme_DeviceDefault_Dialog_Alert, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Alarm alarm = new Alarm(selectedHour , selectedMinute, DismissTypeEnum.DEFAULT);
                alarmAdapter.addAlarm(alarm);

                Intent alarmIntent = new Intent(AlarmManagerActivity.this, AlarmReceiver.class);
                alarmIntent.putExtra(AlarmReceiver.ALARM_ID, alarm.get_id());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmManagerActivity.this, (int) alarm.get_id(), alarmIntent, PendingIntent.FLAG_ONE_SHOT);

                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.getNextAlarmTime(), pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    manager.setExact(AlarmManager.RTC_WAKEUP, alarm.getNextAlarmTime(), pendingIntent);
                } else {
                    manager.set(AlarmManager.RTC_WAKEUP, alarm.getNextAlarmTime(), pendingIntent);
                }
            }
        }, hour, minute, true);
        mTimePicker.show();
    }
}
