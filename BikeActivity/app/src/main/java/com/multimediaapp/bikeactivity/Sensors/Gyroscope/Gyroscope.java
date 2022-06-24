package com.multimediaapp.bikeactivity.Sensors.Gyroscope;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensorThreaded;
import com.multimediaapp.bikeactivity.Interfaces.IGyroListener;

import java.util.concurrent.TimeUnit;

/**
 * Hardware sensor class, gets the angular velocity [rad/s] of the device around 3 axis.
 */
public class Gyroscope extends BaseSensorThreaded<IGyroListener,SensorEvent> implements SensorEventListener {
    private final String TAG = Gyroscope.class.getSimpleName();

    private final Sensor gyro;
    private final SensorManager gyroManager;
    private final SensorEventListener gyroListener;


    public Gyroscope(Sensor gyro, SensorManager gyroManager ){
        super();

        this.gyro = gyro;
        this.gyroManager = gyroManager;
        this.gyroListener = this;
    }

    @Override
    public void Start() {
        if(listeners.size() == 0)
            requestToStart = true;
        else {
            gyroManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_GAME);
            isRunning = true;

            super.Start();
        }
    }

    @Override
    public void Pause() {
        if(isRunning)
            gyroManager.unregisterListener(gyroListener);

        isRunning = false;
        requestToStart = false;
    }

    @Override
    public void Stop() {
        if(isRunning)
            gyroManager.unregisterListener(gyroListener);

        isRunning = false;
        requestToStart = false;

        super.Stop();
    }

    @Override
    public void updateListeners(){
        SensorEvent data;
        while(isRunning){
            ///Before evaluating isRunning will take all elements from the queue until it is emptied
            while(this.data.size() > 0) {
                try {
                    /// If data is not available waits 20 milliseconds for it, if still is not
                    /// available (null) because the queue is empty go on and check if the sensor
                    /// is running
                    if ((data = this.data.poll(20, TimeUnit.MILLISECONDS)) != null) {
                        for (IGyroListener listener :
                                listeners) {
                            listener.onChangeGyro(data.timestamp, data.values);
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
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}
