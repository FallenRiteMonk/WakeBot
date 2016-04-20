package com.fallenritemonk.wakebot.dismisshandler.fragments;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fallenritemonk.wakebot.R;
import com.fallenritemonk.wakebot.WakeBotApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class AmbientLightHandler extends Fragment implements SensorEventListener {
    private static final String LOG_TAG = "AmbientLightHandler";

    private SensorManager sensorManager;
    private Sensor lightSensor;

    public AmbientLightHandler() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ambient_light_handler, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) WakeBotApplication.getContext().getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lumen = event.values[0];
        Log.d(LOG_TAG, "lumen: " + lumen);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(LOG_TAG, "registered listener");
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
