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
    private  Sensor acc = null;
    private SensorManager accManager = null;
    private IMeasurementHandler onAccChange = null;
    private SensorEventListener accListener = null;
    private int orientation = 0;
    private float accelAngle = 0;
    private float axis = 0;
    private float accelZ = 0;
    private int coord = 0;

    public Accelerometer(Sensor acc, SensorManager accManager, IMeasurementHandler onAccChange,int orientation)
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
            coord = 1;
        else
            coord = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        // il sensor event restituisce i 3 valori in ordine x y e z, quindi li metto nelle mie
        //variabili locali
        // _x = event.values[0];
        axis = event.values[coord];
        accelZ = event.values[2];
        /// complementary filter to have very accuracy data
        accelAngle = (float)(Math.atan2(-1* accelAngle, Math.sqrt(accelAngle*accelAngle + accelZ*accelZ)) * 180/Math.PI);
        onAccChange.onChangeAcc(accelAngle);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
