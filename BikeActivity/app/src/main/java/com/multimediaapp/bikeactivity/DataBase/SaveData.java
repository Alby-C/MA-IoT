package com.multimediaapp.bikeactivity.DataBase;

import static android.os.SystemClock.elapsedRealtimeNanos;

import android.content.ContentValues;
import android.content.Context;

import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;

public class SaveData implements IMeasurementHandler {

    private Context context;
    private long startingTime;

    private final int X = 0;
    private final int Y = 1;
    private final int Z = 2;

    public SaveData(Context context) {
        this.context = context;
        this.startingTime = elapsedRealtimeNanos();
    }

    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {
        timestamp = (timestamp - startingTime);
        ContentValues accValues = new ContentValues();

        accValues.put(MyContentProvider.IstantAccX_Col, newValues[X]);
        accValues.put(MyContentProvider.IstantAccY_Col, newValues[Y]);
        accValues.put(MyContentProvider.IstantAccZ_Col, newValues[Z]);
        accValues.put(MyContentProvider.TimeStamp_Col, timestamp);
        context.getContentResolver().insert(MyContentProvider.ACC_URI, accValues);
    }


    @Override
    public void onChangeSpeed(long timestamp, float newSpeed, float avgSpeed) {
        timestamp = (timestamp - startingTime);
        ContentValues speedValues = new ContentValues();

        speedValues.put(MyContentProvider.IstantSpeed_Col, newSpeed);
        speedValues.put(MyContentProvider.TimeStamp_Col, timestamp);
        context.getContentResolver().insert(MyContentProvider.SPEED_URI, speedValues);
    }

    @Override
    public void onChangeRoll(long timestamp, float currentRoll) {
        timestamp = (timestamp - startingTime);
        ContentValues rollValues = new ContentValues();

        rollValues.put(MyContentProvider.IstantRoll_Col, currentRoll);
        rollValues.put(MyContentProvider.TimeStamp_Col, timestamp);
        context.getContentResolver().insert(MyContentProvider.ROLL_URI, rollValues);

    }

    @Override
    public void onJumpHappened(long flightTime) {

    }

    @Override
    public void onChangeGyro(long timestamp, float[] newValues) {

    }
}
