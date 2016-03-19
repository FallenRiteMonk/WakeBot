package com.fallenritemonk.wakebot;

import android.media.RingtoneManager;

import com.fallenritemonk.wakebot.dismisshandler.DismissTypeEnum;

import java.util.Calendar;

import nl.qbusict.cupboard.annotation.Column;

/**
 * Created by FallenRiteMonk on 18/03/16.
 */
public class Alarm {
    private Long _id; // for cupboard
    private int hour;
    private int minute;
    @Column("dismiss_type")
    private DismissTypeEnum dismissType;
    private boolean active;
    private String alarmTonePath;

    public Alarm() {}

    public Alarm(int hour, int minute, DismissTypeEnum type) {
        this.hour = hour;
        this.minute = minute;
        this.dismissType = type;
        active = true;
        alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
    }

    public String getReadableTime() {
        String text = "";
        if (hour < 10) {
            text += "0";
        }
        text += hour + ":";
        if (minute < 10) {
            text += 0;
        }
        text += minute;
        return text;
    }

    public long getNextAlarmTime() {
        Calendar alarm = Calendar.getInstance();
        alarm.set(Calendar.HOUR_OF_DAY, hour);
        alarm.set(Calendar.MINUTE, minute);
        alarm.set(Calendar.SECOND, 0);

        if (alarm.before(Calendar.getInstance())) {
            alarm.add(Calendar.DAY_OF_YEAR, 1);
        }

        return alarm.getTimeInMillis();
    }

    public boolean isActive() {
        return active;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public long get_id() {
        return _id;
    }

    public DismissTypeEnum getDismissType() {
        return dismissType;
    }

    public String getAlarmPath() {
        return alarmTonePath;
    }

    public void deactivate() {
        active = false;
    }
}
