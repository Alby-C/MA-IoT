package com.example.spedometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {
    public TextView txt = null;
    public TextView jumpTv = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // faccio partire il programma
        doStuff();
    }

    private void doStuff() {
        txt = findViewById(R.id.textView1);
        jumpTv = findViewById(R.id.jumpTv);
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // verifico se se son riuscito ad instanziare la location service
        if (lm != null) {
            // check necessario per poter avere la requestLocationUpdate poichè ha bisogno dei permessi
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                // codice per la gestione dei permessi non ricevuti
                return;
            }
            // se ho ricevuto i permessi allora inizio a ricevere gli update della mia locazione
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        }
        Toast.makeText(this,"Waiting for GPS connection!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location == null)
        {
            txt.setText("-.- km/h");
        }
        else
        {
            // ricavo la velocità in metri al secondo e la converto in km/h moltiplicando per 3.6
            float nCurrentSpeed = location.getSpeed() * 3.6f;
            txt.setText(String.format("%.2f", nCurrentSpeed) + " km/h");
            if(location.hasAltitude())
            {
                double altitude = location.getAltitude();
                jumpTv.setText(String.format("%.2f", altitude) + "m");
            }
            else
            {
                jumpTv.setText("0 m");
            }
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
