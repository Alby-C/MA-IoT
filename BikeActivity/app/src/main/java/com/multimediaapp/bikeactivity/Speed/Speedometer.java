package com.multimediaapp.bikeactivity.Speed;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;

public class Speedometer implements LocationListener
{
    private final String Tag = Speedometer.class.getSimpleName();;

    public LocationManager lm = null;
    public IMeasurementHandler onSpeedChange = null;
    private Context context = null;
    private float avgSpeed = 0;
    // index to calculate the average dynamically
    float n = 1;


    public Speedometer(LocationManager lm, IMeasurementHandler onSpeedChange, Context context)
    {
        this.lm = lm;
        this.onSpeedChange = onSpeedChange;
        this.context = context;
        Start();
    }

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
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location)
    {
            // get the speed in meters per second and convert it to km/h multiplying by 3.6
            float nCurrentSpeed = location.getSpeed() * 3.6f;
            /// calculation of the average speed through a portrait mathematical series
            avgSpeed = (1/n)*(nCurrentSpeed+(n-1)*avgSpeed);
            n++;
            /// send result to activity management
            onSpeedChange.onChangeSpeed(nCurrentSpeed, avgSpeed);
    }
}

