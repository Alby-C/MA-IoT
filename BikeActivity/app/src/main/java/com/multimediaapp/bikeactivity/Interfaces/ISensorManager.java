package com.multimediaapp.bikeactivity.Interfaces;

/**
 * Interface to establish the structure of a generic sensor manager.
 * @param <T> The type of listener that can listen to the new values of the sensor.
 */
public interface ISensorManager<T> {
    /**
     * Used to start the measuring phase.
     */
    void Start();

    /**
     * Used to stop the measuring phase.
     */
    void Stop();

    /**
     * Used to subscribe a listener to the new values of the sensor.
     * @param listener Listener class to subscribe.
     */
    void SubscribeListener(T listener);

    /**
     * Used to unsubscribe a listener to the sensor.
     * @param listener Listener to unsubscribe.
     */
    void UnsubscribeListener(T listener);
}
