package com.multimediaapp.bikeactivity.Gyroscope;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;

import static Space.CartesianSpaceOperations.AngleBetween;

import android.content.pm.ActivityInfo;
import android.os.SystemClock;

import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IGyroListener;
import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;

import Space.CartesianSpaceOperations;
import Space.ReferenceSystemCommutator;
import Space.Vector;

public class Roll implements IGyroListener, IAccelListener {
    private final String TAG = Roll.class.getSimpleName();

    ///////////////////////////
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final float NS2S = 1.0f / 1000000000.0f; ///Constant to convert from nanoseconds to seconds
    private static final double R2D = 180./ PI;             ///Constant to convert from radians to degree
    private static final float EPSILON =0.01f;              ///Constant for threshold

    private final float filterPercent = 0.95f;
    private final IMeasurementHandler iMeasurementHandler;

    private final int gyroAxis;
    private final int accelRefAxis;
    private final int inverter;

    // Current angle
    private float currAngle = 0;
    // Current component to the gyro of the angle
    private float currGyroAngle;
    private long prevTimestamp;
    // Current component to the accelerometer of the angle
    private float currAccelAngle;

    public Roll(IMeasurementHandler iMeasurementHandler, int orientation){
        if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            gyroAxis = Y;
            accelRefAxis = X;
            inverter = -1;
        }
        else{
            gyroAxis = X;
            accelRefAxis = Y;
            inverter = 1;
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
        this.currAccelAngle = inverter * (float)(atan(newValues[accelRefAxis] / newValues[Z]) * R2D);

        //this.currAccelAngle = (float) ((newValues[0]/abs(newValues[0]))*AngleBetween(new Vector(newValues[0],0,newValues[Z]), new Vector(0, 0, 1))*R2D);

        calculateRoll();
    }

    public void calculateRoll() {
        /// Complementary filter to have very accuracy data

        this.currAngle = ( filterPercent * (this.currAngle + this.currGyroAngle)) + ((1-filterPercent)* this.currAccelAngle);

        iMeasurementHandler.onChangeRoll(currAngle);
    }
}
