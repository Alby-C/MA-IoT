package com.multimediaapp.bikeactivity.Sensors.Accelerometer;

import android.util.Log;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IJumpListener;

import java.util.ArrayList;
import java.util.List;

import Space.Vector;

/**
 * Software sensor class, takes measurements from accelerometer to detect
 * if the device has performed a jump, then it calculates the flight time [ns].
 */
public class Jump extends BaseSensor<IJumpListener> implements IAccelListener {
    private static final String TAG = Jump.class.getSimpleName();

    /**
     * 10000 measurements ar taken in around 200 seconds, and its the right time span to:
     * - have enough values to detect the jumps;
     * - have not so many values that would overfill the memory;
     * - have to refresh the lists not too often.
     * Lists are emptied when reach the maximum size or when a jump has been detected.
     */
    private static final int MAX_LIST_SIZE = 10000;
    /**
     * When the lists reach the max size without detecting any jump flag,
     * the lists are emptied, copying the last 1000 values of the previous
     * list, because there could be the peak of a jump that haven't already
     * been reached. 1000 measurements are taken in around 20 seconds.
     */
    private static final int MIN_VALUES_TO_COPY = MAX_LIST_SIZE - 1000;
    /**
     * All jumps whose length is above to 10 seconds are excluded, because could
     * be the result of some measurement error.
     */
    private static final float MAX_JUMP_ALLOWED = 1.0E10f;  ///< [ns]

    private ArrayList<Long> timestamps = new ArrayList<>(MAX_LIST_SIZE);        ///< keeps the timestamps of each measurement
    private ArrayList<Float> measurements = new ArrayList<>(MAX_LIST_SIZE);     ///< keeps the modules of each measurement
    private int size = 0;       ///< Size of the lists, kept to speed up the execution.

    private volatile int flagIndex;                         ///< Index of the lists corresponding to the rising of the jump flag
    private boolean isJumping = false;                      ///< Flags to indicate that the device is currently on a jump
    private volatile boolean isJumpEndReached = false;      ///< Flags to indicate that the device has reached the ground
    private volatile boolean isThreadWaiting = false;       ///< Flags to indicate that the thread is waiting for notification
    private volatile boolean isJumpDetectionEnded = false;  ///< Flags to indicate that the thread has finished his calculation

    private final Thread jumpEvaluator;

    public Jump(){
        jumpEvaluator = new Thread(new Runnable() {
            @Override
            public void run() {
                JumpEvaluation();
            }
        }, "jumpEvaluator");
    }

    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {
        synchronized (this) {
            float module = (new Vector(newValues[0], newValues[1], newValues[2])).getModule();

            measurements.add(module);
            timestamps.add(timestamp);
            size++;
            /// Flag that determines whether it is performing a jump, in particular if this is
            /// the highest point of the trajectory, it's raised when the module is less than
            /// 1.5 and not when equal to zero because the accelerometer it's really noisy and
            /// the air resistance prevent the module to go to zero
            if (!isJumping && module < 1.5f) {
                Log.i(TAG, "jump happened");
                isJumping = true;
                flagIndex = timestamps.size() - 1;

                this.notify();
            } else if (isJumping && module > 9.5f) {    /// When the module returns above 9.5 the jump is ended
                isJumpEndReached = true;
                if (isThreadWaiting) {
                    Log.i(TAG, "Jump end reached");
                    this.notify();
                    isJumping = false;
                }
            } else if(!isJumping && size >= MAX_LIST_SIZE) {    /// Emptying the list because has reached the max size
                /// Instead of clearing all the list, it'll copy the last 1000+ elements
                List<Float> tempF = measurements.subList(MIN_VALUES_TO_COPY, size);
                List<Long> tempL = timestamps.subList(MAX_LIST_SIZE - 1000, size);

                measurements = new ArrayList<>(MAX_LIST_SIZE);
                measurements.addAll(tempF);
                timestamps = new ArrayList<>(MAX_LIST_SIZE);
                timestamps.addAll(tempL);

                size = measurements.size();
            } else if (isJumpDetectionEnded){   /// Emptying the list because a jump has been detected
                isJumpDetectionEnded = false;
                measurements = new ArrayList<>(MAX_LIST_SIZE);
                timestamps = new ArrayList<>(MAX_LIST_SIZE);
                size = 0;
            }
        }
    }

    /**
     * Method executed by the thread, in this routine the thread waits for the onChangeAccel
     * to throw the flag due to the detection of a jump, then the thread will go through
     * the values collected to find the starting and the ending of the jump, evaluating the
     * total length of the jump.
     */
    private void JumpEvaluation(){
        Log.i(TAG, "starting thread");

        boolean goingToPeak = false;
        float prevModule;   ///< determines if the peak can be evaluated
        float module;
        long jumpLength = 0;

        synchronized (this) {
            while (isRunning) {
                module = 0;
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Log.e(TAG, e.toString());
                }

                Log.i(TAG, "notify got");

                ///finding the starting peak of the jump
                for (int i = flagIndex; i >= 0; i--) {
                    prevModule = module;
                    module = measurements.get(i);

                    /// Check made to exclude the natural bouncing of the values due to the
                    /// noisiness of the accelerometer, the peak is never evaluated until the
                    /// module is at least 9.5
                    if(goingToPeak || (goingToPeak = module > 9.5f)) {
                        if (module < prevModule) { ///peak reached
                            jumpLength = timestamps.get(i + 1);
                            break;
                        }
                    }
                }
                goingToPeak = false;

                Log.i(TAG, "Jump started at: " + jumpLength);

                /// If the jump is not yet over the thread waits
                if (!isJumpEndReached) {
                    isThreadWaiting = true;
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Log.e(this.toString(), e.toString());
                    }
                    Log.i(TAG, "Notify got");
                }
                isJumpEndReached = false;

                /// Finding the ending peak of the jump
                for (int i = flagIndex; i < measurements.size(); i++) {
                    if (measurements.get(i) >= 9.5f) {
                        jumpLength = timestamps.get(i) - jumpLength;
                        Log.i(TAG, "Jump length: " + (timestamps.get(i) - jumpLength));
                        break;
                    }
                }

                /// Excludes all values above MAX_JUMP_ALLOWED nanoseconds, possibly got by error in calculations
                if(jumpLength < MAX_JUMP_ALLOWED) {
                    for (IJumpListener listener :
                            listeners) {
                        listener.onJumpHappened(jumpLength);
                    }
                }

                isJumpDetectionEnded = true;
            }
        }
    }

    @Override
    public void Start() {
        jumpEvaluator.start();

        isRunning = true;
    }

    @Override
    public void Stop() {
        isRunning = false;

        try {
            jumpEvaluator.join(1000);   /// wait for the thread to interrupt himself
        } catch (InterruptedException e) {}

        if(jumpEvaluator.isAlive())
            jumpEvaluator.interrupt();
    }
}
