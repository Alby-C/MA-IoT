package com.multimediaapp.bikeactivity.Interfaces;

public interface IAccelListener {
    /**
     * Used from the Accelerometer manager to give a new value.
     * @param timestamp The timestamp of the measurement.
     * @param newValues New linear acceleration values along three axis in m/s^2.
     */
    void onChangeAccel(long timestamp, float[] newValues);
}
