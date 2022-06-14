package com.multimediaapp.bikeactivity.Sensors.Gyroscope;

import static android.os.SystemClock.elapsedRealtimeNanos;
import static java.lang.Math.PI;
import static java.lang.Math.atan;

import android.content.pm.ActivityInfo;
import android.os.SystemClock;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IGyroListener;
import com.multimediaapp.bikeactivity.Interfaces.IRollListener;

import java.util.concurrent.locks.ReentrantLock;

public class Roll extends BaseSensor<IRollListener> implements IGyroListener, IAccelListener {
    private final String TAG = Roll.class.getSimpleName();

    ///////////////////////////
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final float NS2S = 1.0f / 1000000000.0f; ///<Constant to convert from nanoseconds to seconds
    private static final double R2D = 180./ PI;             ///<Constant to convert from radians to degree
    private static final float EPSILON =0.01f;              ///<Constant for threshold

    private final float filterPercent = 0.95f;

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

    public ReentrantLock mutex = new ReentrantLock();

    public Roll( int orientation){
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


        prevTimestamp = SystemClock.elapsedRealtimeNanos();
    }

    @Override
    public void onChangeGyro(long timestamp, float[] newValues) {
        float delta = (timestamp - this.prevTimestamp) * NS2S;

        /// Discrete integral to calculate the angle, then convert it to degrees
        this.currGyroAngle = (float) (delta * newValues[gyroAxis] * R2D);

        /// Set new timestamp
        prevTimestamp = timestamp;

        calculateRoll();
    }

    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {
        float angle = inverter * (float)(atan(newValues[accelRefAxis] / newValues[Z]) * R2D);
        if(angle != Float.NaN) {
            this.currAccelAngle = angle;
            calculateRoll();
        }
    }

    public void calculateRoll() {
        /// Complementary filter to have very accuracy data
        mutex.lock();
        this.currAngle = ( filterPercent * (this.currAngle + this.currGyroAngle)) + ((1-filterPercent)* this.currAccelAngle);
        mutex.unlock();
        for(IRollListener listener
                : listeners){
            listener.onChangeRoll(elapsedRealtimeNanos(), currAngle);
        }

    }

    @Override
    public void Start() {

    }

    @Override
    public void Stop() {

    }
}
