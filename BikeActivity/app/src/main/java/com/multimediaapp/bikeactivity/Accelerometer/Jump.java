package com.multimediaapp.bikeactivity.Accelerometer;

import com.multimediaapp.bikeactivity.ActivityManagement;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;

import java.util.ArrayList;

public class Jump implements IAccelListener {

    private IMeasurementHandler iMeasurementHandler;

    public Jump(IMeasurementHandler iMeasurementHandler){
        this.iMeasurementHandler = iMeasurementHandler;
    }

    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {

    }
}
