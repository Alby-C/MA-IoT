package com.multimediaapp.bikeactivity.BaseClasses;

import com.multimediaapp.bikeactivity.Interfaces.IGyroListener;

import java.util.ArrayList;

/**
 * Base class for a sensor manager class.
 * @param <T> The type of listener that can subscribe to this sensor.
 */
public abstract class BaseSensor<T> {
    /// Array of listeners to the new values of the gyroscope
    protected final ArrayList<T> listeners = new ArrayList<>();
    /// True if the sensor is getting
    protected boolean isRunning = false;
    /// this flag if no listeners are subscribed, is set to true in the start method
    protected boolean requestToStart = false;

    /**
     * Used to start the measuring phase.
     */
    public abstract void Start();

    /**
     * Used to stop the measuring phase.
     */
    public abstract void Stop();

    /**
     * Used to subscribe a listener to the new values of the sensor.
     * @param listener Listener class to subscribe.
     */
    public void SubscribeListener(T listener) {
        if(!this.listeners.contains(listener))
            this.listeners.add(listener);

        if(requestToStart){
            Start();
            requestToStart = false;
        }
    }

    /**
     * Used to unsubscribe a listener to the sensor.
     * @param listener Listener to unsubscribe.
     */
    public void UnsubscribeListener(T listener) {
        this.listeners.remove(listener);
    }
}
