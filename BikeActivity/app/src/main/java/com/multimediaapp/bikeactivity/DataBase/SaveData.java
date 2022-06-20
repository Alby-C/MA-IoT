package com.multimediaapp.bikeactivity.DataBase;

import static android.os.SystemClock.elapsedRealtimeNanos;

import static java.lang.Math.hypot;
import static Miscellaneous.MiscellaneousOperations.Truncate;

import android.content.ContentValues;
import android.content.Context;

import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;

public class SaveData implements IMeasurementHandler {

    private final Context context;
    private final long startingTime;

    private final int X = 0;
    private final int Y = 1;
    private final int Z = 2;

    float acceleration;

    public SaveData(Context context) {
        this.context = context;
        this.startingTime = elapsedRealtimeNanos();
    }

    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {
        timestamp = (timestamp - startingTime);
        ContentValues accValues = new ContentValues();

        /// Get the approximated linear acceleration as the acceleration axis X and Y
        acceleration = (float) hypot(newValues[X], newValues[Y]);

        accValues.put(MyContentProvider.InstantAccX_Col, acceleration);
        accValues.put(MyContentProvider.TimeStamp_Col, timestamp);
        context.getContentResolver().insert(MyContentProvider.ACC_URI, accValues);
    }


    @Override
    public void onChangeSpeed(long timestamp, float newSpeed, float avgSpeed) {
        timestamp = (timestamp - startingTime);
        ContentValues speedValues = new ContentValues();

        speedValues.put(MyContentProvider.InstantSpeed_Col, newSpeed);
        speedValues.put(MyContentProvider.TimeStamp_Col, timestamp);
        context.getContentResolver().insert(MyContentProvider.SPEED_URI, speedValues);
    }

    @Override
    public void onChangeRoll(long timestamp, float currentRoll) {
        timestamp = (timestamp - startingTime);
        ContentValues rollValues = new ContentValues();

        rollValues.put(MyContentProvider.InstantRoll_Col, Truncate(currentRoll,2));
        rollValues.put(MyContentProvider.TimeStamp_Col, timestamp);
        context.getContentResolver().insert(MyContentProvider.ROLL_URI, rollValues);
    }

    @Override
    public void onJumpHappened(long flightTimeNanos) {

    }

    @Override
    public void onChangeGyro(long timestamp, float[] newValues) {

    }
}
