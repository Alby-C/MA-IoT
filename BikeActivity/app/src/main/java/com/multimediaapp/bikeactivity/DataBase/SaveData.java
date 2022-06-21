package com.multimediaapp.bikeactivity.DataBase;

import static android.os.SystemClock.elapsedRealtimeNanos;
import static com.multimediaapp.bikeactivity.Sensors.Gyroscope.Roll.X;
import static com.multimediaapp.bikeactivity.Sensors.Gyroscope.Roll.Y;
import static com.multimediaapp.bikeactivity.Sensors.Gyroscope.Roll.Z;
import static java.lang.Math.sqrt;
import static Miscellaneous.MiscellaneousOperations.Truncate;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;

import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.ILinearAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IRollListener;
import com.multimediaapp.bikeactivity.Interfaces.ISpeedListener;

public class SaveData implements IAccelListener, ILinearAccelListener, IRollListener, ISpeedListener {
    private final Context context;

    private final long startingTime;

    int accelAxis;

    public SaveData(Context context, int orientation) {
        this.context = context;
        this.startingTime = elapsedRealtimeNanos();

        if(orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            accelAxis = X;
        else
            accelAxis = Y;
    }

    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {
        timestamp = (timestamp - startingTime);
        ContentValues accValues = new ContentValues();

        accValues.put(MyContentProvider.InstantAcc_Col,
                (float) sqrt(newValues[X] * newValues[X] + newValues[Y] * newValues[Y] + newValues[Z] * newValues[Z]));
        accValues.put(MyContentProvider.TimeStamp_Col, timestamp);
        context.getContentResolver().insert(MyContentProvider.ACC_URI, accValues);
    }

    @Override
    public void onChangeLinearAccel(long timestamp, float[] newValues) {
        timestamp = (timestamp - startingTime);
        ContentValues linAccValues = new ContentValues();

        Log.i("SaveData", "axis: " + accelAxis + " value: " +  newValues[accelAxis]+ "\nts: "+ timestamp);

        linAccValues.put(MyContentProvider.InstantLinAcc_Col, Truncate(newValues[accelAxis], 3));
        linAccValues.put(MyContentProvider.TimeStamp_Col, timestamp);
        context.getContentResolver().insert(MyContentProvider.LIN_ACC_URI, linAccValues);
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
}
