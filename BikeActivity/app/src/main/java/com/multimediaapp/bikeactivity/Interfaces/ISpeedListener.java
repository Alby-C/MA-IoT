package com.multimediaapp.bikeactivity.Interfaces;

public interface ISpeedListener {
    /**
     * Method to handle the variation of speed.
     *
     * @param newSpeed Current speed detected
     * @param avgSpeed Average speed
     */
    void onChangeSpeed(long timestamp, float newSpeed, float avgSpeed);
}
