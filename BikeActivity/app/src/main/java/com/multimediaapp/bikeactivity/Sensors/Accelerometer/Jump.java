package com.multimediaapp.bikeactivity.Sensors.Accelerometer;

import android.content.Context;
import android.util.Log;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IJumpListener;

import java.util.ArrayList;
import java.util.List;

import Space.Vector;

public class Jump extends BaseSensor<IJumpListener> implements IAccelListener {
    private static final String TAG = Jump.class.getSimpleName();

    private static final int MAX_LIST_SIZE = 10000;

    private ArrayList<Long> timestamps = new ArrayList<>(MAX_LIST_SIZE);
    private ArrayList<Float> measurements = new ArrayList<>(MAX_LIST_SIZE);
    private int size = 0;

    private volatile int flagIndex;
    private boolean isJumping = false;                      ///< Flags to indicate that the device is currently on a jump
    private volatile boolean isJumpEndReached = false;      ///< Flags to indicate that the device has reached the ground
    private volatile boolean isThreadWaiting = false;       ///< Flags to indicate that the thread is waiting for notification
    private volatile boolean isJumpDetectionEnded = false;  ///< Flags to indicate that the thread has finished his calculation

    private final Thread jumpEvaluator;
    private final Context context;

    public Jump(Context context){
        this.context = context;
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
            ///Flag that determines whether it is performing a jump, in particular if this is the highest point of the trajectory
            if (!isJumping && module < 1.5f) {
                Log.i(TAG, "jump happened");
                isJumping = true;
                flagIndex = timestamps.size() - 1;

                this.notify();
            } else if (isJumping && module > 9.5f) {
                isJumpEndReached = true;
                if (isThreadWaiting) {
                    Log.i(TAG, "Jump end reached");
                    this.notify();
                    isJumping = false;
                }
            } else if(!isJumping && size >= MAX_LIST_SIZE) {
                /// Instead of clearing all the list, it'll copy the last 1000+ elements
                List<Float> tempF = measurements.subList(9000, size);
                List<Long> tempL = timestamps.subList(9000, size);

                measurements = new ArrayList<>(10000);
                measurements.addAll(tempF);
                timestamps = new ArrayList<>(10000);
                timestamps.addAll(tempL);

                size = measurements.size();
            } else if (isJumpDetectionEnded){
                isJumpDetectionEnded = false;
                measurements = new ArrayList<>(10000);
                timestamps = new ArrayList<>(10000);
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
    public void JumpEvaluation(){
        Log.i(TAG, "starting thread");

        boolean goingToPeak = false;
        float prevModule;
        float module = 0;

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

                    if(goingToPeak || (goingToPeak = module > 9.5f)) {  ///check made to exclude bouncing of the values
                        if (module < prevModule) { ///peak reached
                            jumpLength = timestamps.get(i + 1);
                            break;
                        }
                    }
                }
                goingToPeak = false;

                Log.i(TAG, "Jump started at: " + jumpLength);

                ///if the jump is not yet over the thread waits
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

                ///finding the ending peak of the jump
                for (int i = flagIndex; i < measurements.size(); i++) {
                    if (measurements.get(i) >= 9.5f) {
                        jumpLength = timestamps.get(i) - jumpLength;
                        Log.i(TAG, "Jump length: " + (timestamps.get(i) - jumpLength));
                        break;
                    }
                }

                /// Excludes all values above 10 seconds, possibly got by error in calculations
                if(jumpLength < 1.0E10f) {
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
            jumpEvaluator.join(1000);
        } catch (InterruptedException e) {}

        if(jumpEvaluator.isAlive())
            jumpEvaluator.interrupt();
    }
}
