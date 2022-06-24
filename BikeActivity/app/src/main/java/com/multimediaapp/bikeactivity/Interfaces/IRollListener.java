package com.multimediaapp.bikeactivity.Interfaces;

/**
 * Interface for a class that wants to get updates from the roll sensor.
 */
public interface IRollListener
{
    /**
     * Method to handle the variation of roll.
     * @param currRoll Current roll detected
     */
    void onChangeRoll(long timestamp, float currRoll);

}
