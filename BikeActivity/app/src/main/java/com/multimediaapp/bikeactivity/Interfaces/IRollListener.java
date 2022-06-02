package com.multimediaapp.bikeactivity.Interfaces;

public interface IRollListener
{
    /**
     * Method to handle the variation of roll.
     * @param currentRoll Current roll detected
     */
    void onChangeRoll(float currentRoll, long timestamp);

}
