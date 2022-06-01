package com.multimediaapp.bikeactivity.Gyroscope;

import static java.lang.Math.PI;

import android.content.pm.ActivityInfo;
import android.os.SystemClock;

import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IGyroListener;
import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;

public class Roll implements IGyroListener, IAccelListener {
    private final String TAG = Roll.class.getSimpleName();

    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final float NS2S = 1.0f / 1000000000.0f; ///Constant to convert from nanoseconds to seconds
    private static final double R2D = 180./ PI;             ///Constant to convert from radians to degree
    private static final float EPSILON =0.01f;             ///Constant for threshold

    private final IMeasurementHandler iMeasurementHandler;

    private final int gyroAxis;
    private final int accelRefAxis;

    // Current angle
    private float currAngle = 0;
    // Current component to the gyro of the angle
    private float currGyroAngle;
    private long prevTimestamp;
    // Current component to the accelerometer of the angle
    private float currAccelAngle;

    private boolean newGyro;
    private boolean newAccel;

    public Roll(IMeasurementHandler iMeasurementHandler, int orientation){
        if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            gyroAxis = Y;
            accelRefAxis = X;
        }
        else{
            gyroAxis = X;
            accelRefAxis = Y;
        }

        this.iMeasurementHandler = iMeasurementHandler;

        prevTimestamp = SystemClock.elapsedRealtimeNanos();
    }

    @Override
    public void onChangeGyro(long timestamp, float[] newValues) {
        float delta = (timestamp - this.prevTimestamp) * NS2S;

        /// Discrete integral to calculate the angle, then convert it to degrees
        this.currGyroAngle = (float) (delta * newValues[gyroAxis] * R2D);

        calculateRoll();

        /// Set new timestamp
        prevTimestamp = timestamp;
    }

    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {
        this.currAccelAngle = (float) (Math.atan(newValues[accelRefAxis] / newValues[Z]) * R2D);

        calculateRoll();
    }

    public void calculateRoll() {
        /// Complementary filter to have very accuracy data
        this.currAngle = (0.98f * (this.currAngle + this.currGyroAngle)) + (0.02f * this.currAccelAngle);

        iMeasurementHandler.onChangeRoll(currAngle);
    }
}
