package com.multimediaapp.bikeactivity.DataBase;

import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IGyroListener;
import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;

public class SaveData implements IAccelListener, IMeasurementHandler {
    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {

    }

    @Override
    public void onChangeSpeed(float newSpeed, float avgSpeed) {
        
    }

    @Override
    public void onChangeRoll(float currentRoll) {

    }

    @Override
    public void onJumpHappened(long flightTime) {

    }
}
