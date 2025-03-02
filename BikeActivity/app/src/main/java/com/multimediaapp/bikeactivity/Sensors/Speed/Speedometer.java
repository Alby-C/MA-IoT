package com.multimediaapp.bikeactivity.Sensors.Speed;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensorThreaded;
import com.multimediaapp.bikeactivity.Interfaces.ISpeedListener;

import java.util.concurrent.TimeUnit;

import Miscellaneous.MiscellaneousOperations;

/**
 * Sensor class, uses a GPS service to get the speed [km/h] and returns it as measurement.
 * It also takes care of calculating the average speed.
 */
public class Speedometer extends BaseSensorThreaded<ISpeedListener, long[]> implements LocationListener {
    private final String TAG = Speedometer.class.getSimpleName();

    public LocationManager lm;
    private Context context;
    private float avgSpeed = 0;
    // index to calculate speed average dynamically
    float n = 1;

    private boolean isRunning = false;

    public Speedometer(LocationManager lm, Context context) {
        this.lm = lm;
        this.context = context;
    }

    @Override
    public void Start() {
        if(listeners.size() == 0)
            requestToStart = true;
        else {
            // check if I have been able to instantiate the location service
            if (lm != null) {
                // check necessary to be able to have the requestLocationUpdate as it needs permissions
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // code for the management of permissions not received
                }
                // if received the permits then start to receive the updates of my location
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                isRunning = true;

                super.Start();
            }
        }
    }

    @Override
    public void Pause() {
        if(isRunning)
            lm.removeUpdates(this);

        isRunning = false;
        requestToStart = false;
    }

    @Override
    public void Stop() {
        if(isRunning)
            lm.removeUpdates(this);

        isRunning = false;
        requestToStart = false;

        super.Stop();
    }

    @Override
    protected void updateListeners() {
        long[] data;
        while(isRunning) {
            ///Before evaluating isRunning will take all elements from the queue until it is emptied
            while(this.data.size() > 0) {
                try {
                    /// If data is not available waits 20 milliseconds for it, if still is not
                    /// available (null) because the queue is empty go on and check if the sensor
                    /// is running
                    if ((data = this.data.poll(20, TimeUnit.MILLISECONDS)) != null) {
                        for (ISpeedListener listener :
                                listeners) {
                            listener.onChangeSpeed(data[0], data[1] / MiscellaneousOperations.FLOAT2LONG, data[2] / MiscellaneousOperations.FLOAT2LONG);
                        }
                    }
                } catch (InterruptedException e) {
                }    //If interrupted keep polling
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        long timestamp = SystemClock.elapsedRealtimeNanos();
        // Get the speed in meters per second and convert it to km/h multiplying by 3.6
        float nCurrentSpeed = location.getSpeed() * 3.6f;
        /// Calculation of  speed average through a portrait mathematical series
        avgSpeed = (1/n)*(nCurrentSpeed+(n-1)*avgSpeed);
        n++;

        try {
            data.add(new long[]{timestamp, (long) (nCurrentSpeed * MiscellaneousOperations.FLOAT2LONG) , (long) ( avgSpeed * MiscellaneousOperations.FLOAT2LONG)});
        } catch(IllegalStateException e){ }  //If the queue is full keep measuring

        for (ISpeedListener listener:
                listeners) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /// Send result to activity management
                    listener.onChangeSpeed(timestamp, nCurrentSpeed, avgSpeed);
                }
            }).start();
        }
    }
}