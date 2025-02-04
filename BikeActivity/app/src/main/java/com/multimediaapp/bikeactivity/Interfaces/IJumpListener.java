package com.multimediaapp.bikeactivity.Interfaces;

/**
 * Interface for a class that wants to get updates from the jump sensor.
 */
public interface IJumpListener {
    /**
     * Method to handle the jump event.
     * @param flightTimeNanos The flight time of the jump in nanoseconds.
     */
    void onJumpHappened(long flightTimeNanos);
}
