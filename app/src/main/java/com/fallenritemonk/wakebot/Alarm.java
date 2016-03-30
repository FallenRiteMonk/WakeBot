package com.fallenritemonk.wakebot;

import android.content.Context;
import android.media.RingtoneManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.fallenritemonk.wakebot.dismisshandler.DismissTypeEnum;
import com.fallenritemonk.wakebot.utils.Day;

import java.util.Calendar;

import nl.qbusict.cupboard.annotation.Column;

/**
 * Created by FallenRiteMonk on 18/03/16.
 */
public class Alarm implements Parcelable {
    private final String LOG_TAG = "AlarmManagerActivity";

    private Long _id; // for cupboard
    private int hour;
    private int minute;
    @Column("dismiss_type")
    private DismissTypeEnum dismissType;
    private boolean active;
    private String alarmTonePath;
    private boolean sunday;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;

    public Alarm() {}

    public Alarm(int hour, int minute, DismissTypeEnum type) {
        this.hour = hour;
        this.minute = minute;
        this.dismissType = type;
        active = true;
        alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
        sunday = false;
        monday = false;
        tuesday = false;
        wednesday = false;
        thursday = false;
        friday = false;
        saturday = false;
    }

    protected Alarm(Parcel in) {
        hour = in.readInt();
        minute = in.readInt();
        active = in.readByte() != 0;
        alarmTonePath = in.readString();
        sunday = in.readByte() != 0;
        monday = in.readByte() != 0;
        tuesday = in.readByte() != 0;
        wednesday = in.readByte() != 0;
        thursday = in.readByte() != 0;
        friday = in.readByte() != 0;
        saturday = in.readByte() != 0;
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

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
        if (!active) {
            return -1;
        }
        Calendar alarm = Calendar.getInstance();
        alarm.set(Calendar.HOUR_OF_DAY, hour);
        alarm.set(Calendar.MINUTE, minute);
        alarm.set(Calendar.SECOND, 0);

        if (alarm.before(Calendar.getInstance())) {
            alarm.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (!sunday && !monday && !tuesday && !wednesday && !thursday && !friday && !saturday) {
            return alarm.getTimeInMillis();
        }

        while (true) {
            int dayOfWeek = alarm.get(Calendar.DAY_OF_WEEK);
            Log.d(LOG_TAG, "Day of Week: " + dayOfWeek);
            if (dayOfWeek == 1 && sunday) {
                return alarm.getTimeInMillis();
            } else if (dayOfWeek == 2 && monday) {
                return alarm.getTimeInMillis();
            } else if (dayOfWeek == 3 && tuesday) {
                return alarm.getTimeInMillis();
            } else if (dayOfWeek == 4 && wednesday) {
                return alarm.getTimeInMillis();
            } else if (dayOfWeek == 5 && thursday) {
                return alarm.getTimeInMillis();
            } else if (dayOfWeek == 6 && friday) {
                return alarm.getTimeInMillis();
            } else if (dayOfWeek == 7 && saturday) {
                return alarm.getTimeInMillis();
            } else {
                alarm.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
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

    public void setRepeatDay(Day day, boolean value) {
        switch (day) {
            case SUNDAY: sunday = value;
                return;
            case MONDAY: monday = value;
                return;
            case TUESDAY: tuesday = value;
                return;
            case WEDNESDAY: wednesday = value;
                return;
            case THURSDAY: thursday = value;
                return;
            case FRIDAY: friday = value;
                return;
            case SATURDAY: saturday = value;
                return;
        }
    }

    public boolean getRepeatDay(Day day) {
        switch (day) {
            case SUNDAY: return sunday;
            case MONDAY: return monday;
            case TUESDAY: return tuesday;
            case WEDNESDAY: return wednesday;
            case THURSDAY: return thursday;
            case FRIDAY: return friday;
            case SATURDAY: return saturday;
        }
        return false;
    }

    public String getRepeatDayString(Context context) {
        String temp = "";
        if (sunday) {
            temp += context.getString(R.string.short_sunday) + " ";
        }
        if (monday) {
            temp += context.getString(R.string.short_monday) + " ";
        }
        if (tuesday) {
            temp += context.getString(R.string.short_tuesday) + " ";
        }
        if (wednesday) {
            temp += context.getString(R.string.short_wednesday) + " ";
        }
        if (thursday) {
            temp += context.getString(R.string.short_thursday) + " ";
        }
        if (friday) {
            temp += context.getString(R.string.short_friday) + " ";
        }
        if (saturday) {
            temp += context.getString(R.string.short_saturday) + " ";
        }
        if (temp.length() > 0) {
            temp = temp.substring(0, temp.length() - 1);
        } else {
            temp = context.getString(R.string.no_repeat);
        }
        return temp;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(LOG_TAG);
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(alarmTonePath);
        dest.writeByte((byte) (sunday ? 1 : 0));
        dest.writeByte((byte) (monday ? 1 : 0));
        dest.writeByte((byte) (tuesday ? 1 : 0));
        dest.writeByte((byte) (wednesday ? 1 : 0));
        dest.writeByte((byte) (thursday ? 1 : 0));
        dest.writeByte((byte) (friday ? 1 : 0));
        dest.writeByte((byte) (saturday ? 1 : 0));
    }
}
