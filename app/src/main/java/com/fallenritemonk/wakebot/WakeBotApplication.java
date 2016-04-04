package com.fallenritemonk.wakebot;

import android.app.Application;
import android.content.Context;

/**
 * Created by fallenritemonk on 4/4/16.
 */
public class WakeBotApplication extends Application {
    private static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
    }

    public static Context getContext() {
        return applicationContext;
    }
}
