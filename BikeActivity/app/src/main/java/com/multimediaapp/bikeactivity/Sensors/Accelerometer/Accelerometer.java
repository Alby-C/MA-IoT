package com.multimediaapp.bikeactivity.Sensors.Accelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;

public class Accelerometer extends BaseSensor<IAccelListener> implements SensorEventListener {
    private final String TAG = Accelerometer.class.getSimpleName();

    private final Sensor acc;
    private final SensorManager accManager;
    private final SensorEventListener accListener;

    public Accelerometer(Sensor acc, SensorManager accManager){
        this.acc = acc;
        this.accManager = accManager;
        this.accListener = this;
    }

    @Override
    public void Start() {
        if(internalListeners.size() == 0)
            requestToStart = true;
        else {
            accManager.registerListener(accListener, acc, SensorManager.SENSOR_DELAY_GAME);
            isRunning = true;
        }
    }

    @Override
    public void Stop() {
        if(isRunning)
            accManager.unregisterListener(accListener);

        requestToStart = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        for (IAccelListener listener :
                internalListeners) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    listener.onChangeAccel(event.timestamp, event.values);
                }
            }).start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

}
