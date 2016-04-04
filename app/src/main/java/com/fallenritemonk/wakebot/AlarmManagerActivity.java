package com.fallenritemonk.wakebot;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fallenritemonk.wakebot.utils.AlarmAdapter;

public class AlarmManagerActivity extends AppCompatActivity {
    private final String LOG_TAG = "AlarmManagerActivity";
    private static final int EDIT_ALARM_REQUEST = 1;

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

        alarmAdapter.setOnItemClickListener(new AlarmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent intent = new Intent(getApplicationContext(), EditAlarmActivity.class);
                intent.putExtra(EditAlarmActivity.PASSED_ALARM_ID, alarmAdapter.getId(position));
                startActivityForResult(intent, EDIT_ALARM_REQUEST);
            }
        });
    }

    private void newAlarm() {
        Intent intent = new Intent(this, EditAlarmActivity.class);
        startActivityForResult(intent, EDIT_ALARM_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_ALARM_REQUEST) {
            alarmAdapter.notifyDataSetChanged();
        }
    }
}
