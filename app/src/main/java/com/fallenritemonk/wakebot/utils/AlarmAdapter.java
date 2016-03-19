package com.fallenritemonk.wakebot.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fallenritemonk.wakebot.Alarm;
import com.fallenritemonk.wakebot.R;
import com.fallenritemonk.wakebot.database.DatabaseHelper;

import java.util.ArrayList;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by FallenRiteMonk on 18/03/16.
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private static AlarmAdapter instance;

    private ArrayList<Alarm> alarmList;
    private SQLiteDatabase db;

    private AlarmAdapter(Context context) {
        alarmList = new ArrayList<>();

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public static AlarmAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new AlarmAdapter(context);
        }
        return instance;
    }

    public void addAlarm(Alarm alarm) {
        alarmList.add(alarm);
        notifyDataSetChanged();

        long id = cupboard().withDatabase(db).put(alarm);
        alarm.set_id(id);
    }

    public void updateAlarm(Alarm alarm) {
        cupboard().withDatabase(db).put(alarm);
        notifyDataSetChanged();
    }

    public void removeAlarm(int i) {
        cupboard().withDatabase(db).delete(alarmList.get(i));
        alarmList.remove(i);
        notifyDataSetChanged();
    }

    public void loadAlarms() {
        Cursor alarms = cupboard().withDatabase(db).query(Alarm.class).getCursor();
        try {
            QueryResultIterable<Alarm> itr = cupboard().withCursor(alarms).iterate(Alarm.class);
            for (Alarm alarm : itr) {
                alarmList.add(alarm);
            }
        } finally {
            alarms.close();
        }
    }

    public Alarm getAlarm(long l) {
        return cupboard().withDatabase(db).get(Alarm.class, l);
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder contactViewHolder, int i) {
        Alarm alarm = alarmList.get(i);
        contactViewHolder.alarmText.setText(alarm.getReadableTime());
        if (!alarm.isActive()) {
            contactViewHolder.itemView.setBackgroundColor(Color.GRAY);
        } else {
            contactViewHolder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.alarm_card_view, viewGroup, false);

        return new AlarmViewHolder(itemView);
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        protected View itemView;
        protected TextView alarmText;

        public AlarmViewHolder(View v) {
            super(v);
            itemView = v;
            alarmText =  (TextView) v.findViewById(R.id.alarm_time_view);
        }
    }
}