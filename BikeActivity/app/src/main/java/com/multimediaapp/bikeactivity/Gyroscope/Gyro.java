package com.multimediaapp.bikeactivity.Gyroscope;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


import com.multimediaapp.bikeactivity.Interfaces.IGyroListener;
import com.multimediaapp.bikeactivity.Interfaces.ISensorManager;

import java.util.ArrayList;

public class Gyro implements SensorEventListener, ISensorManager<IGyroListener>{
    /// Array of listeners to the new values of the gyroscope
    private final ArrayList<IGyroListener> listeners = new ArrayList<>();
    /// True if the sensor is getting
    private boolean isRunning = false;
    /// this flag if no listeners are subscribed, is set to true in the start method
    private boolean requestToStart = false;

    private final Sensor gyro;
    private final SensorManager gyroManager;
    private final SensorEventListener gyroListener;

    public Gyro(Sensor gyro, SensorManager gyroManager ){
        this.gyro = gyro;
        this.gyroManager = gyroManager;
        this.gyroListener = this;
    }

    @Override
    public void Start() {
        if(listeners.size() == 0)
            requestToStart = true;
        else {
            gyroManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_NORMAL);
            isRunning = true;
        }
    }

    @Override
    public void Stop() {
        if(!isRunning)
            gyroManager.unregisterListener(gyroListener);

        requestToStart = false;
    }

    @Override
    public void SubscribeListener(IGyroListener listener) {
        if(!this.listeners.contains(listener))
            this.listeners.add(listener);

        if(requestToStart){
            Start();
            requestToStart = false;
        }
    }

    @Override
    public void UnsubscribeListener(IGyroListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        for (IGyroListener listener :
                listeners) {
            listener.onChangeGyro(event.timestamp, event.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}
