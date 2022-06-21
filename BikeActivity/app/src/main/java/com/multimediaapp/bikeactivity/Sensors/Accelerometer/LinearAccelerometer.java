package com.multimediaapp.bikeactivity.Sensors.Accelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensorThreaded;
import com.multimediaapp.bikeactivity.Interfaces.ILinearAccelListener;

import java.util.concurrent.TimeUnit;

public class LinearAccelerometer extends BaseSensorThreaded<ILinearAccelListener, SensorEvent> implements SensorEventListener {
    private final String TAG = LinearAccelerometer.class.getSimpleName();

    private final Sensor linAcc;
    private final SensorManager linAccManager;
    private final SensorEventListener linAccListener;

    public LinearAccelerometer(Sensor linAcc, SensorManager linAccManager){
        super();

        this.linAcc = linAcc;
        this.linAccManager = linAccManager;
        this.linAccListener = this;
    }

    @Override
    public void Start() {
        if(listeners.size() == 0)
            requestToStart = true;
        else {
            linAccManager.registerListener(linAccListener, linAcc, SensorManager.SENSOR_DELAY_NORMAL);
            isRunning = true;

            super.Start();
        }
    }

    @Override
    public void Pause() {
        if(isRunning)
            linAccManager.unregisterListener(linAccListener);

        isRunning = false;
        requestToStart = false;
    }

    @Override
    public void Stop() {
        if(isRunning)
            linAccManager.unregisterListener(linAccListener);

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
                        for (ILinearAccelListener listener :
                                listeners) {
                            listener.onChangeLinearAccel(data.timestamp, data.values);
                        }
                    }
                } catch (InterruptedException e) { }    //If interrupted keep polling
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
