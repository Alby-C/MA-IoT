package com.multimediaapp.bikeactivity.Sensors.Accelerometer;

import android.content.Context;
import android.util.Log;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IJumpListener;

import java.util.ArrayList;

import Space.Vector;

public class Jump extends BaseSensor<IJumpListener> implements IAccelListener {

    private static final String TAG = Jump.class.getSimpleName();

    private int flagindex;
    private boolean isJumping = false;
    private boolean isJumpEndReached = false;

    private final ArrayList<Long> timestamps = new ArrayList<>(1000);
    private final ArrayList<Vector> measurements = new ArrayList<>(1000);
    private final ArrayList<Integer> jumpIndexes = new ArrayList<>(5);

    private final Thread jumpEvaluator;
    private boolean isThreadWaiting = false;
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
            float module;
            Vector measurement = new Vector(newValues[0], newValues[1], newValues[2]);

            measurements.add(measurement);
            timestamps.add(timestamp);
            ///Flag that determines whether it is performing a jump, in particular if this is the highest point of the trajectory
            if (!isJumping && (int)measurement.getModule() == 0) {
                Log.i(TAG, "jump happened");
                isJumping = true;
                flagindex = timestamps.size() - 1;

                this.notify();
            } else if (isJumping && measurement.getModule() >= 9.6f) {
                isJumpEndReached = true;
                if (isThreadWaiting) {
                    Log.i(TAG, "Jump end reached");
                    this.notify();
                    isJumping = false;
                }
            }
        }
    }

    public void JumpEvaluation(){
        Log.i(TAG, "starting thread");

        float prevModule;
        float module = 0;

        long jumpStartTimestamp = 0;
        synchronized (this) {
            while (isRunning) {
                module = 0;
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Log.e(TAG, e.toString());
                }

                Log.i(TAG, "notify got");

                for (int i = flagindex; i >= 0; i--) {
                    prevModule = module;
                    module = measurements.get(i).getModule();

                    if (module < prevModule) { ///peak reached
                        jumpStartTimestamp = timestamps.get(i + 1);
                        jumpIndexes.add(i + 1);
                        break;
                    }
                }
                Log.i(TAG, "Jump started at: " + jumpStartTimestamp);

                if (!isJumpEndReached) {
                    isThreadWaiting = true;
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Log.e(this.toString(), e.toString());
                    }
                    Log.i(TAG, "Notify got");
                } else {
                    Log.i(TAG, "jumpEndReached");
                }

                isJumpEndReached = false;

                for (int i = flagindex; i < measurements.size(); i++) {
                    if (measurements.get(i).getModule() >= 9.5f) {
                        jumpIndexes.add(i);

                        Log.i(TAG, "Jump length: " + (timestamps.get(i) - jumpStartTimestamp));
                        break;
                    }
                }
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
