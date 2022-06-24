package com.multimediaapp.bikeactivity.DataBase;

import static android.os.SystemClock.elapsedRealtimeNanos;
import static java.lang.Math.sqrt;
import static Miscellaneous.MiscellaneousOperations.G;
import static Miscellaneous.MiscellaneousOperations.Truncate;
import static Miscellaneous.MiscellaneousOperations.X;
import static Miscellaneous.MiscellaneousOperations.Y;
import static Miscellaneous.MiscellaneousOperations.Z;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;

import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.ILinearAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IRollListener;
import com.multimediaapp.bikeactivity.Interfaces.ISpeedListener;

/**
 * Class that manages the saving of the data in the SQLite database.
 * This saves in real time the measurements of:
 * - The module of the acceleration [m/s^2] measured from the accelerometer (needed for the jump evaluation);
 * - The component relative to the forward direction of the linear acceleration [g], based on
 *   the orientation of the device (Y axis if portrait, X if landscape);
 * - The roll angle [°] measured from the Roll sensor;
 * - The speed [km/h] from the speedometer.
 */
public class SaveData implements IAccelListener, ILinearAccelListener, IRollListener, ISpeedListener {
    private final Context context;

    /**
     * The starting time of the activity, all the timestamps are
     * reevaluated relatively to this value.
     */
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

        /// The linear acceleration is measured in function of g
        /// (e.g. 1.5g of acceleration is equal to 1 and an half time the gravitational acceleration)
        linAccValues.put(MyContentProvider.InstantLinAcc_Col, Truncate(newValues[accelAxis] / G, 1));
        linAccValues.put(MyContentProvider.TimeStamp_Col, timestamp);
        context.getContentResolver().insert(MyContentProvider.LIN_ACC_URI, linAccValues);
    }

    @Override
    public void onChangeSpeed(long timestamp, float newSpeed, float avgSpeed) {
        timestamp = (timestamp - startingTime);
        ContentValues speedValues = new ContentValues();

        speedValues.put(MyContentProvider.InstantSpeed_Col, (int)(newSpeed));
        speedValues.put(MyContentProvider.TimeStamp_Col, timestamp);
        context.getContentResolver().insert(MyContentProvider.SPEED_URI, speedValues);
    }

    @Override
    public void onChangeRoll(long timestamp, float currentRoll) {
        timestamp = (timestamp - startingTime);
        ContentValues rollValues = new ContentValues();

        rollValues.put(MyContentProvider.InstantRoll_Col, (int)(currentRoll));
        rollValues.put(MyContentProvider.TimeStamp_Col, timestamp);
        context.getContentResolver().insert(MyContentProvider.ROLL_URI, rollValues);
    }
}
