package com.multimediaapp.bikeactivity.Accelerometer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;

public class Accelerometer implements SensorEventListener
{
    private Sensor acc = null;
    private SensorManager accManager = null;
    private SensorEventListener accListener = null;
    private IMeasurementHandler onAccChange = null;
    private int orientation = 0;
    /// Accelleration of axis Y or axis X
    private float accelerationAxis = 0;
    /// Accelleration axis Z
    private float accelZ = 0;
    private int coord = 0;

    public Accelerometer(Sensor acc, SensorManager accManager, IMeasurementHandler onAccChange, int orientation)
    {
        this.acc = acc;
        this.accManager = accManager;
        this.onAccChange = onAccChange;
        this.accListener = this;
        this.orientation = orientation;
        Start();
    }

    private void Start()
    {
        accManager.registerListener(accListener, acc, SensorManager.SENSOR_DELAY_GAME);
        if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            /// set coord x for the portrait mode
            coord = 0;
        else
            /// set coord y for the portrait mode
            coord = 1;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        /// get acceleration
        accelerationAxis = event.values[coord];
        accelZ = event.values[2];
        /// send values to activity management
        onAccChange.onChangeAcc(accelerationAxis, accelZ);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
