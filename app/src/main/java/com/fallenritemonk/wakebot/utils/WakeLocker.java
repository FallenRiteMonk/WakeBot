package com.fallenritemonk.wakebot.utils;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by FallenRiteMonk on 19/03/16.
 */
public class WakeLocker {
    private static final String WAKELOCK_TAG = "WAKELOCK_TAG";

    private static PowerManager.WakeLock wakeLock = null;

    public static void acquire(Context ctx) {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.	PARTIAL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP, WAKELOCK_TAG);
        }
        wakeLock.acquire();
    }

    public static void release() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
}
