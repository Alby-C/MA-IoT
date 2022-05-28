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
    // indice per calcolarmi la media in modo dinamico
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
        // verifico se se son riuscito ad instanziare la location service
        if (lm != null)
        {
            // check necessario per poter avere la requestLocationUpdate poichè ha bisogno dei permessi
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
             // codice per la gestione dei permessi non ricevuto
            }
            // se ho ricevuto i permessi allora inizio a ricevere gli update della mia locazione
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location)
    {
            // ricavo la velocità in metri al secondo e la converto in km/h moltiplicando per 3.6
            float nCurrentSpeed = location.getSpeed() * 3.6f;
            /// calcolo la media della velocità tramite una serie matematica
            avgSpeed = (1/n)*(nCurrentSpeed+(n-1)*avgSpeed);
            n++;
            /// invio i risultati
            onSpeedChange.onChangeSpeed(nCurrentSpeed, avgSpeed);
    }
}

