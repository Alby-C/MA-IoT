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

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;
import com.multimediaapp.bikeactivity.Interfaces.ISpeedListener;

public class Speedometer extends BaseSensor<ISpeedListener> implements LocationListener
{
    private final String TAG = Speedometer.class.getSimpleName();;

    public LocationManager lm = null;
    public IMeasurementHandler onSpeedChange = null;
    private Context context = null;
    private float avgSpeed = 0;
    // index to calculate speed average dynamically
    float n = 1;
    private static final float NS2S = 1.0f / 1000000000.0f; ///Constant to convert from nanoseconds to seconds

    private boolean isRunning = false;

    public Speedometer(LocationManager lm, IMeasurementHandler onSpeedChange, Context context)
    {
        this.lm = lm;
        this.onSpeedChange = onSpeedChange;
        this.context = context;
    }

    @Override
    public void Start()
    {
        // check if I have been able to instantiate the location service
        if (lm != null)
        {
            // check necessary to be able to have the requestLocationUpdate as it needs permissions
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
             // code for the management of permissions not received
            }
            // if received the permits then start to receive the updates of my location
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            isRunning = true;
        }
    }

    @Override
    public void Stop(){
        if(isRunning){
            lm.removeUpdates(this);
            isRunning = true;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location)
    {
            long timestamp = SystemClock.elapsedRealtimeNanos();
            // Get the speed in meters per second and convert it to km/h multiplying by 3.6
            float nCurrentSpeed = location.getSpeed() * 3.6f;
            /// Calculation of  speed average through a portrait mathematical series
            avgSpeed = (1/n)*(nCurrentSpeed+(n-1)*avgSpeed);
            n++;

        for (ISpeedListener listener:
                internalListeners) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /// Send result to activity management
                    onSpeedChange.onChangeSpeed(timestamp, nCurrentSpeed, avgSpeed);
                }
            }).start();
        }
    }
}

