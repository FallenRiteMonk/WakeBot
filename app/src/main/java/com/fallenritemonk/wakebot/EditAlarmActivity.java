package com.fallenritemonk.wakebot;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fallenritemonk.wakebot.dismisshandler.DismissTypeEnum;
import com.fallenritemonk.wakebot.utils.AlarmAdapter;
import com.fallenritemonk.wakebot.utils.AlarmReceiver;
import com.fallenritemonk.wakebot.utils.Day;

import java.util.Arrays;
import java.util.Calendar;

public class EditAlarmActivity extends AppCompatActivity {
    private final String LOG_TAG = "EditAlarmActivtiy";

    public static final String PASSED_ALARM_ID = "PASSED_ALARM_ID";

    private TextView timeView;
    private TextView repeatView;
    private TextView dismissTypeView;

    private boolean newAlarm;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        timeView = (TextView) findViewById(R.id.edit_time_view);
        repeatView = (TextView) findViewById(R.id.edit_repeat_view);
        dismissTypeView = (TextView) findViewById(R.id.edit_dismiss_type_view);

        long id = getIntent().getLongExtra(PASSED_ALARM_ID, -1);
        alarm = AlarmAdapter.getInstance(this).getAlarm(id);
        newAlarm = false;

        if (alarm == null) {
            Calendar mCurrentTime = Calendar.getInstance();
            mCurrentTime.add(Calendar.MINUTE, 1);

            alarm = new Alarm(mCurrentTime.get(Calendar.HOUR_OF_DAY), mCurrentTime.get(Calendar.MINUTE), DismissTypeEnum.DEFAULT);
            newAlarm = true;
        }

        timeView.setText(alarm.getReadableTime());
        repeatView.setText(alarm.getRepeatDayString(this));
        dismissTypeView.setText(alarm.getDismissType().toString());
    }

    public void click(View view) {
        if (view.getId() == R.id.edit_time_layout) {
            editTime();
        } else if (view.getId() == R.id.edit_repeat_layout) {
            editRepeat();
        } else if (view.getId() == R.id.edit_dismiss_type_layout) {
            editDismissType();
        } else if (view.getId() == R.id.edit_delete_button) {
            deleteAlarm();
        } else if (view.getId() == R.id.edit_save_button) {
            saveAlarm();
        }
    }

    private void editTime() {
        TimePickerDialog mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                alarm.setHour(selectedHour);
                alarm.setMinute(selectedMinute);

                timeView.setText(alarm.getReadableTime());
            }
        }, alarm.getHour(), alarm.getMinute(), true);
        mTimePicker.show();
    }

    private void editRepeat() {
        CharSequence[] dayList = new CharSequence[7];
        dayList[0] = getString(R.string.sunday);
        dayList[1] = getString(R.string.monday);
        dayList[2] = getString(R.string.tuesday);
        dayList[3] = getString(R.string.wednesday);
        dayList[4] = getString(R.string.thursday);
        dayList[5] = getString(R.string.friday);
        dayList[6] = getString(R.string.saturday);

        final boolean[] selectedItems = new boolean[7];
        Arrays.fill(selectedItems, false);
        if (alarm.getRepeatDay(Day.SUNDAY)) {
            selectedItems[0] = true;
        }
        if (alarm.getRepeatDay(Day.MONDAY)) {
            selectedItems[1] = true;
        }
        if (alarm.getRepeatDay(Day.TUESDAY)) {
            selectedItems[2] = true;
        }
        if (alarm.getRepeatDay(Day.WEDNESDAY)) {
            selectedItems[3] = true;
        }
        if (alarm.getRepeatDay(Day.THURSDAY)) {
            selectedItems[4] = true;
        }
        if (alarm.getRepeatDay(Day.FRIDAY)) {
            selectedItems[5] = true;
        }
        if (alarm.getRepeatDay(Day.SATURDAY)) {
            selectedItems[6] = true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMultiChoiceItems(dayList, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selectedItems[which] = isChecked;
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alarm.setRepeatDay(Day.SUNDAY, selectedItems[0]);
                alarm.setRepeatDay(Day.MONDAY, selectedItems[1]);
                alarm.setRepeatDay(Day.TUESDAY, selectedItems[2]);
                alarm.setRepeatDay(Day.WEDNESDAY, selectedItems[3]);
                alarm.setRepeatDay(Day.THURSDAY, selectedItems[4]);
                alarm.setRepeatDay(Day.FRIDAY, selectedItems[5]);
                alarm.setRepeatDay(Day.SATURDAY, selectedItems[6]);

                repeatView.setText(alarm.getRepeatDayString(getApplicationContext()));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void editDismissType() {
        CharSequence[] typeList = {DismissTypeEnum.DEFAULT.toString(), DismissTypeEnum.QR_CODE.toString()};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.dismiss_type))
                .setItems(typeList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            alarm.setDismissType(DismissTypeEnum.DEFAULT);
                        } else if (which == 1) {
                            alarm.setDismissType(DismissTypeEnum.QR_CODE);
                        }

                        dismissTypeView.setText(alarm.getDismissType().toString());
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteAlarm() {
        if (!newAlarm) {
            Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) alarm.get_id(), alarmIntent, 0);

            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
            pendingIntent.cancel();

            AlarmAdapter.getInstance(this).removeAlarm(alarm.get_id());
        }

        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }

    private void saveAlarm() {
        if (newAlarm) {
            AlarmAdapter.getInstance(this).addAlarm(alarm);
        } else {
            AlarmAdapter.getInstance(this).updateAlarm(alarm);
        }

        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        alarmIntent.putExtra(AlarmReceiver.ALARM_ID, alarm.get_id());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) alarm.get_id(), alarmIntent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.getNextAlarmTime(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, alarm.getNextAlarmTime(), pendingIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, alarm.getNextAlarmTime(), pendingIntent);
        }

        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
