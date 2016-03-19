package com.fallenritemonk.wakebot.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fallenritemonk.wakebot.dismisshandler.DismissHandler;

/**
 * Created by FallenRiteMonk on 18/03/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private final String LOG_TAG = "AlarmReceiver";

    public static final String ALARM_ID = "ALARM_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        WakeLocker.acquire(context);

        Intent dismissHandlerIntent = new Intent(context, DismissHandler.class);
        dismissHandlerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dismissHandlerIntent.putExtras(intent.getExtras());
        context.startActivity(dismissHandlerIntent);
    }
}
