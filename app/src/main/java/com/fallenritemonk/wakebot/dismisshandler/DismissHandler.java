package com.fallenritemonk.wakebot.dismisshandler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.fallenritemonk.wakebot.Alarm;
import com.fallenritemonk.wakebot.R;
import com.fallenritemonk.wakebot.dismisshandler.fragments.StandardHandler;
import com.fallenritemonk.wakebot.utils.AlarmAdapter;
import com.fallenritemonk.wakebot.utils.AlarmReceiver;
import com.fallenritemonk.wakebot.utils.WakeLocker;

import java.io.IOException;
import java.util.Calendar;

public class DismissHandler extends AppCompatActivity {
    private final String LOG_TAG = "DismissHandler";

    private final long SNOOZE_TIME = 5 * 60 * 1000; // 5min

    private AlarmAdapter alarmAdapter;
    private Alarm alarm;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dismiss_handler);

        initAlarm();
        if (alarm != null) {
            initScreen();
            initMediaPlayer();
        } else {
            finish();
        }
    }

    private  void initAlarm() {
        alarmAdapter = AlarmAdapter.getInstance(this);
        long id = getIntent().getLongExtra(AlarmReceiver.ALARM_ID, -1);
        alarm = alarmAdapter.getAlarm(id);
    }

    private void initScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (alarm.getDismissType().equals(DismissTypeEnum.DEFAULT)) {
            StandardHandler firstFragment = new StandardHandler();
            getSupportFragmentManager().beginTransaction().add(R.id.dismiss_fragment_container, firstFragment).commit();
        }
    }

    private void initMediaPlayer() {
        setVolumeControlStream(AudioManager.STREAM_ALARM);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this,
                    Uri.parse(alarm.getAlarmPath()));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer.release();
        }
    }

    private void stopMeadiaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void click(View view) {
        if (view.getId() == R.id.snooze_button) {
            setAlarm(Calendar.getInstance().getTimeInMillis() + SNOOZE_TIME);

            stopMeadiaPlayer();
            finish();
        }
    }

    public void dismiss() {
        stopMeadiaPlayer();
        alarm.deactivate();
        alarmAdapter.updateAlarm(alarm);

        long nextAlarmTime = alarm.getNextAlarmTime();
        if (nextAlarmTime > 0) {
            setAlarm(nextAlarmTime);
        }

        finish();
    }

    private void setAlarm(long time) {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra(AlarmReceiver.ALARM_ID, alarm.get_id());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) alarm.get_id(), alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        WakeLocker.release();
    }
}
