package com.multimediaapp.bikeactivity.BaseClasses;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;

public abstract class BaseSensorThreaded<T,Q> extends BaseSensor<T> {

    private static final String TAG = BaseSensorThreaded.class.getSimpleName();
    ///blocking queue (thread-safe) with fixed capacity set to 500 elements
    protected final ArrayBlockingQueue<Q> datas = new ArrayBlockingQueue<>(500, true);;
    protected Thread listenersManager;
    protected final int TIMEOUT = 3;      ///used in stop to determine whether to kill listenersManager

    protected BaseSensorThreaded() {
        listenersManager = new Thread(new Runnable() {
            @Override
            public void run() {
                updateListeners();
            }
        }, "listenersManager");
    }

    public void Start(){
        listenersManager = new Thread(new Runnable() {
            @Override
            public void run() {
                updateListeners();
            }
        });
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
        int datasPrevSize;
        int datasSize = datas.size();
        int count = TIMEOUT;

        try {
            do {
                if (datasSize == 0 && !listenersManager.isAlive()) { /// if the queue is empty and the thread is already dead exit the loop
                    break;
                }
                else {  ///if the queue is not empty and/or the thread is still alive
                    listenersManager.join(500);  ///waits 0.5 seconds for listenersManager to finish
                    datasPrevSize = datasSize;
                    datasSize = datas.size();
                    ///if the previous size and the current size are the same, maybe the thread is in a deadlock
                    ///so if after TIMEOUT loop's cycles it's still in this situation the thread will be killed
                    if (datasSize == datasPrevSize)
                        count--;
                    else    ///means the thread as done progresses
                        count = TIMEOUT;
                }
            }while(count > 0);
            if(listenersManager.isAlive())
                listenersManager.interrupt();
        } catch (InterruptedException e) {
            Log.e(TAG +"thr:" + listenersManager.getName(), e.toString());
        }
    }

    protected abstract void updateListeners();
}
