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
    private final String LOG_TAG = "AlarmAdapter";

    private static AlarmAdapter instance;

    private final Context context;
    private final ArrayList<Alarm> alarmList;
    private final SQLiteDatabase db;

    private static OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private AlarmAdapter(Context context) {
        alarmList = new ArrayList<>();

        this.context = context;
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
        loadAlarms();
        notifyDataSetChanged();
    }

    public void removeAlarm(long id) {
        cupboard().withDatabase(db).delete(Alarm.class, id);
        loadAlarms();
        notifyDataSetChanged();
    }

    public void loadAlarms() {
        alarmList.clear();
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

    public long getId(int i) {
        return alarmList.get(i).get_id();
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
        contactViewHolder.repeatDayText.setText(alarm.getRepeatDayString(context));

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
        final View itemView;
        final TextView alarmText;
        final TextView repeatDayText;

        public AlarmViewHolder(View v) {
            super(v);
            itemView = v;
            alarmText =  (TextView) v.findViewById(R.id.alarm_time_view);
            repeatDayText = (TextView) v.findViewById(R.id.repeat_day_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }
}