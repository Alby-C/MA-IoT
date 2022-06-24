package com.multimediaapp.bikeactivity.Interfaces;

/**
 * Interface for a class that wants to get updates from the gyroscope sensor.
 */
public interface IGyroListener {
    /**
     * Used from the Gyroscope manager to give a new value.
     * @param timestamp The timestamp of the measurement.
     * @param newValues New angular velocity values along three axis in rad/s.
     */
    void onChangeGyro(long timestamp, float[] newValues);
}
