package com.multimediaapp.bikeactivity.DataBase;

import android.content.ContentValues;
import android.content.Context;

import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;
import com.multimediaapp.bikeactivity.Interfaces.IRollListener;

public class SaveData implements IAccelListener, IMeasurementHandler, IRollListener {

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
    private static final float NS2S = 1.0f / 1000000000.0f; ///Constant to convert from nanoseconds to seconds
    private Context context;
    public SaveData(Context context)
    {
        this.context = context;
    }
    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {

    }

    @Override
    public void onChangeSpeed(float newSpeed, float avgSpeed, long timestamp) {
        timestamp = (long) (timestamp * NS2S);
        ContentValues speedValues = new ContentValues();

        speedValues.put(MyContentProvider.IstantSpeed_Col, newSpeed);
        speedValues.put(MyContentProvider.TimeStamp_Col, timestamp);
    }

    @Override
    public void onChangeRoll(float currentRoll, long timestamp) {
        timestamp = (long) (timestamp * NS2S);
        ContentValues rollValues = new ContentValues();

        rollValues.put(MyContentProvider.IstantRoll_Col, currentRoll);
        rollValues.put(MyContentProvider.TimeStamp_Col, timestamp);
        context.getContentResolver().insert(MyContentProvider.ROLL_URI, rollValues);

    }

    @Override
    public void onJumpHappened(long flightTime) {

    }
}
