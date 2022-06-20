package com.multimediaapp.bikeactivity.Interfaces;

public interface IJumpListener {
    /**
     * Method to handle the jump event.
     *
     * @param flightTimeNanos The flight time of the jump in nanoseconds.
     */
    void onJumpHappened(long flightTimeNanos);
}
