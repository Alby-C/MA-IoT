package com.multimediaapp.bikeactivity.Sensors.Accelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensorThreaded;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;

import java.util.concurrent.TimeUnit;

/**
 * Hardware sensor class, gets the acceleration [m/s^2] of the device along 3 axis
 * considering the gravitational contribute.
 */
public class Accelerometer extends BaseSensorThreaded<IAccelListener, SensorEvent> implements SensorEventListener {
    private final String TAG = Accelerometer.class.getSimpleName();

    private final Sensor acc;
    private final SensorManager accManager;
    private final SensorEventListener accListener;

    public Accelerometer(Sensor acc, SensorManager accManager){
        super();

        this.acc = acc;
        this.accManager = accManager;
        this.accListener = this;
    }

    @Override
    public void Start() {
        if(listeners.size() == 0)
            requestToStart = true;
        else {
            accManager.registerListener(accListener, acc, SensorManager.SENSOR_DELAY_GAME);
            isRunning = true;

            super.Start();
        }
    }

    @Override
    public void Pause() {
        if(isRunning)
            accManager.unregisterListener(accListener);

        isRunning = false;
        requestToStart = false;
    }

    @Override
    public void Stop() {
        if(isRunning)
            accManager.unregisterListener(accListener);

        isRunning = false;
        requestToStart = false;

        super.Stop();
    }

    @Override
    public void updateListeners() {
        SensorEvent data;
        while(isRunning){
            ///Before evaluating isRunning will take all elements from the queue until it is emptied
            while(this.data.size() > 0) {
                try {
                    /// If data is not available waits 20 milliseconds for it, if still is not
                    /// available (null) because the queue is empty go on and check if the sensor
                    /// is running
                    if ((data = this.data.poll(20, TimeUnit.MILLISECONDS)) != null) {
                        for (IAccelListener listener :
                                listeners) {
                            listener.onChangeAccel(data.timestamp, data.values);
                        }
                    }
                } catch (InterruptedException e) {
                }    //If interrupted keep polling
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try{
            data.add(event);
        }catch(IllegalStateException e){ }  //If the queue is full keep measuring
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
