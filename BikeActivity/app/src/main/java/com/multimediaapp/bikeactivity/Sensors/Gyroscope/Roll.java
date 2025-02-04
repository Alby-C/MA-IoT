package com.multimediaapp.bikeactivity.Sensors.Gyroscope;

import static android.os.SystemClock.elapsedRealtimeNanos;
import static java.lang.Math.atan;

import android.content.pm.ActivityInfo;
import android.os.SystemClock;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IGyroListener;
import com.multimediaapp.bikeactivity.Interfaces.IRollListener;

import java.util.concurrent.locks.ReentrantLock;

import Miscellaneous.MiscellaneousOperations;

/**
 * Software sensor class, takes measurements from gyroscope and accelerometer to
 * calculate the roll angle [°] via complementary filter.
 */
public class Roll extends BaseSensor<IRollListener> implements IGyroListener, IAccelListener {
    private final String TAG = Roll.class.getSimpleName();

    private static final float FILTER_PERCENTAGE = 0.95f;

    /**
     * Determines which axis is to take in consideration from
     * the 3 given by the sensor, based on the orientation of the device.
     * Y -> portrait mode, angular velocity around Y axis;
     * X -> landscape mode, angular velocity around X axis.
     */
    private final int gyroAxis;
    /**
     * Determines which axis is to take in consideration from
     * the 3 given by the sensor, based on the orientation of the device.
     * X -> portrait mode, acceleration along X axis, the arctangent will be
     * measured among X and Z axis;
     * Y -> landscape mode, acceleration along Y axis, the arctangent will be
     * measured among Y and Z axis.
     */
    private final int accelRefAxis;
    /**
     * If in portrait mode it will be -1, and multiplied by the arctangent in
     * the acceleration because the angle will be opposite due to the orientation
     * of the X axis. In landscape will be 1.
     */
    private final int inverter;

    /// Current angle
    private float currAngle = 0;
    /// Current component to the gyro of the angle
    private float currGyroAngle;
    private long prevTimestamp;
    /// Current component to the accelerometer of the angle
    private float currAccelAngle;

    public ReentrantLock mutex = new ReentrantLock();

    public Roll( int orientation){
        if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            gyroAxis = MiscellaneousOperations.Y;
            accelRefAxis = MiscellaneousOperations.X;
            inverter = -1;
        }
        else{
            gyroAxis = MiscellaneousOperations.X;
            accelRefAxis = MiscellaneousOperations.Y;
            inverter = 1;
        }


        prevTimestamp = SystemClock.elapsedRealtimeNanos();
    }

    @Override
    public void onChangeGyro(long timestamp, float[] newValues) {
        float delta = (timestamp - this.prevTimestamp) * MiscellaneousOperations.NS2S;

        /// Discrete integral to calculate the angle, then convert it to degrees
        this.currGyroAngle = (float) (delta * newValues[gyroAxis] * MiscellaneousOperations.R2D);

        /// Set new timestamp
        prevTimestamp = timestamp;

        calculateRoll();
    }

    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {
        float angle = inverter * (float)(atan(newValues[accelRefAxis] / newValues[MiscellaneousOperations.Z]) * MiscellaneousOperations.R2D);
        if(angle != Float.NaN) {
            this.currAccelAngle = angle;
            calculateRoll();
        }
    }

    public void calculateRoll() {
        /// Complementary filter to have very accuracy data
        mutex.lock();
        this.currAngle = ( FILTER_PERCENTAGE * (this.currAngle + this.currGyroAngle)) + ((1- FILTER_PERCENTAGE)* this.currAccelAngle);
        mutex.unlock();

        if (!(currAngle > 90) && !(currAngle < -90)) {
            for (IRollListener listener
                    : listeners) {
                listener.onChangeRoll(elapsedRealtimeNanos(), currAngle);
            }
        }
    }

    @Override
    public void Start() {

    }

    @Override
    public void Stop() {

    }
}
