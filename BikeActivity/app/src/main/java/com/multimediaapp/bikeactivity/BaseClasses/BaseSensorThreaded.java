package com.multimediaapp.bikeactivity.BaseClasses;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Base class for a sensor manager class that uses a thread to communicate 
 * new values to listeners.
 * @param <T> The type of listener that can subscribe to this sensor.
 * @param <Q> The type of values that can be handled from the thread.
 */
public abstract class BaseSensorThreaded<T,Q> extends BaseSensor<T> {
    private static final String TAG = BaseSensorThreaded.class.getSimpleName();

    /// Blocking queue (thread-safe) with fixed capacity set to 500 elements
    protected final ArrayBlockingQueue<Q> data = new ArrayBlockingQueue<>(500, true);
    protected Thread listenersManager;      /// Thread used to communicate new values to listeners
    protected final int TIMEOUT = 3;        /// Used in Stop() to determine whether to kill listenersManager

    protected BaseSensorThreaded() { }

    /**
     * Used to start or resume the measuring phase.
     */
    public void Start(){
        /// A thread that has been stopped needs to be re-instantiated, so listenerManager
        /// will be initialized directly here
        listenersManager = new Thread(new Runnable() {
            @Override
            public void run() {
                updateListeners();
            }
        }, "listenersManager");

        listenersManager.start();
    }

    /**
     * Used to suspend the measuring phase, without killing the thread.
     */
    public abstract void Pause();

    /**
     * Used to stop the measuring phase, killing the thread if necessary.
     */
    public void Stop(){
        int dataPrevSize;
        int dataSize = data.size();
        int count = TIMEOUT;

        /// The code that follows will make sure that all the data from the sensor has been
        /// communicated to all listeners by checking the queue size and the status of
        /// listenersManager thread. In case of problems with the thread, such as it is
        /// permanently locked the method will interrupt it.
        try {
            do {
                if (dataSize == 0 && !listenersManager.isAlive()) { /// If the queue is empty and the thread is already dead exits the loop
                    break;
                }
                else {  /// If the queue is not empty and/or the thread is still alive
                    listenersManager.join(500);  /// Waits 0.5 seconds for listenersManager to finish

                    dataPrevSize = dataSize;
                    dataSize = data.size();
                    /// If the previous size and the current size are the same, maybe the thread
                    /// is in a deadlock so if after a number of loop's cycles defined by TIMEOUT
                    /// it's still in this situation the thread will be killed.
                    if (dataSize == dataPrevSize)
                        count--;
                    else    /// Means that the thread has made progress
                        count = TIMEOUT;
                }
            }while(count > 0);
        } catch (InterruptedException e) {
            Log.e(TAG +"thr:" + listenersManager.getName(), e.toString());
        } finally {
            if(listenersManager.isAlive())
                listenersManager.interrupt();
        }
    }

    /**
     * Method used by the listenerManager thread to manage the poll of new
     * data from the queue and the communication with the listeners.
     */
    protected abstract void updateListeners();
}
