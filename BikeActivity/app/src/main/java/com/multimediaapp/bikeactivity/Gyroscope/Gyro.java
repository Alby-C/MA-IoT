package com.multimediaapp.bikeactivity.Gyroscope;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.IGyroListener;

public class Gyro extends BaseSensor<IGyroListener> implements SensorEventListener {
    private final String TAG = Gyro.class.getSimpleName();

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
            gyroManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_GAME);
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
