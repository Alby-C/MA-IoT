package com.multimediaapp.bikeactivity.Interfaces;

/**
 * Interface for a class that wants to get updates from the speed sensor.
 */
public interface ISpeedListener {
    /**
     * Method to handle the variation of speed.
     *
     * @param newSpeed Current speed detected
     * @param avgSpeed Average speed
     */
    void onChangeSpeed(long timestamp, float newSpeed, float avgSpeed);
}
