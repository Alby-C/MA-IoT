package com.multimediaapp.bikeactivity.DataBase;

import static android.os.SystemClock.elapsedRealtimeNanos;

import android.content.ContentValues;
import android.content.Context;

import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;

public class SaveData implements IMeasurementHandler {

    String[] speedCol = {
            MyContentProvider._ID_Col,
            MyContentProvider.IstantSpeed_Col ,
            MyContentProvider.TimeStamp_Col
    };

    String[] rollCol = { MyContentProvider._ID_Col,
            MyContentProvider.IstantRoll_Col,
            MyContentProvider.TimeStamp_Col
    };

    String[] accCol = { MyContentProvider._ID_Col,
            MyContentProvider.IstantAcc_Col,
            MyContentProvider.TimeStamp_Col
    };
    private static final long NS2S = 1000000000; ///Constant to convert from nanoseconds to seconds
    private Context context;
    private long startingTime;


    public SaveData(Context context) {
        this.context = context;
        this.startingTime = elapsedRealtimeNanos();
    }

    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {

    }


    @Override
    public void onChangeSpeed(long timestamp,float newSpeed, float avgSpeed) {
        timestamp = (timestamp - startingTime);
        ContentValues speedValues = new ContentValues();

        speedValues.put(MyContentProvider.IstantSpeed_Col, newSpeed);
        speedValues.put(MyContentProvider.TimeStamp_Col, timestamp);
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
