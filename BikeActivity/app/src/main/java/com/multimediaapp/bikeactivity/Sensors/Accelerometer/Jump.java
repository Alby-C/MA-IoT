package com.multimediaapp.bikeactivity.Sensors.Accelerometer;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IJumpListener;

public class Jump extends BaseSensor<IJumpListener> implements IAccelListener {

    public Jump(){
    }

    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {

    }

    @Override
    public void Start() {

    }

    @Override
    public void Stop() {

    }
}
