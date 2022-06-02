package com.multimediaapp.bikeactivity.Interfaces;

public interface IRollListener
{
    /**
     * Method to handle the variation of roll.
     * @param currRoll Current roll detected
     */
    void onChangeRoll(long timestamp, float currRoll);

}
