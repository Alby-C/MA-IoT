package com.multimediaapp.bikeactivity.Gyroscope;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;


import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;

public class Roll implements SensorEventListener
{
    private Sensor gyro = null;
    private SensorManager gyroManager = null;
    private SensorEventListener gyroListener = null;
    private IMeasurementHandler onRollChange = null;
    private Context context = null;

    /// Constant to convert nanosec to sec
    private static final float NS2S = 1.0f / 1000000000.0f;
    /// Constant for threeshold
    private static final float EPSILON = 0.06f;
    /// Variables
    private float angle = 0;
    private float axis = 0;
    private int coord = 0;
    private int orientation = 0;
    /// Time to calculate the integral
    private double ts;

    public Roll(Sensor gyro, SensorManager gyroManager, IMeasurementHandler onRollChange, int orientation )
    {
        this.gyro = gyro;
        this.gyroManager = gyroManager;
        this.gyroListener = this;
        this.onRollChange = onRollChange;
        this.orientation = orientation;
        Start(orientation);
    }

    public void Start(int orientation)
    {
        if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            /// y axis on portrait mode
            coord = 1;
        else
            /// x axis on landascape mode
            coord = 0;

        gyroManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_GAME);
        // timestamp initialization
        ts = SystemClock.elapsedRealtimeNanos();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double delta = (event.timestamp - ts) * NS2S;
        /// Get accellaration angular
        axis = event.values[coord];

        /// Discrete integral to calculate the angle
        angle = (float) delta * axis;

        /// Send new angle to activity management
        onRollChange.onChangeRoll(angle);

        /// Set new timestamp
        ts = event.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
