package com.example.gyroscope;

import static java.lang.Math.PI;
import static java.lang.Math.abs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final String TAG = "MainActivity";

    // Buttons: "Start" starts the angle measurement, "Stop" stops it;
    // "Reset" is useful just to clean the screen after the reset
    private Button bttStart = null, bttStop = null, bttReset;

    // Each one displays the inclination on one axis
    private TextView tvX = null, tvY = null, tvZ = null;

    // Sensor managing
    private Sensor gyro = null;
    private SensorManager gyroManager = null;
    private SensorEventListener gyroListener = null;

    // Rotation angles on the 3 axes
    float angleX;
    float angleY;
    float angleZ;

    float maxRightTilt= 0;
    float maxLeftTilt= 0;

    // Constants for simple data processing
    private static final float NS2S = 1.0f / 1000000000.0f;
    private static final float EPSILON = 0.06f;

    private List<float[]> angleValues = null;

    // Variable used to save the current timestamp, needed to calculate the time delta accurately
    private double ts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing buttons
        bttStart = findViewById(R.id.bttStart);
        bttStop = findViewById(R.id.bttStop);
        bttReset = findViewById(R.id.bttReset);

        // Initializing textviews
        tvX = findViewById(R.id.TVX);
        tvY = findViewById(R.id.TVY);
        tvZ = findViewById(R.id.TVZ);

        // Sensor set-up
        gyroManager = (SensorManager)getSystemService((Context.SENSOR_SERVICE));
        gyro = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if(gyro != null) Log.i(TAG, "Giroscopio inizializzato correttamente");
        else Log.i(TAG, "Giroscopio non presente");

        gyroListener = this;

        bttStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gyroManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_GAME);
                // Inizializzo il timestamp
                ts = SystemClock.elapsedRealtimeNanos();
                angleX = 0;
                angleY = 0;
                angleZ = 0;
            }
        });

        bttStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gyroManager.unregisterListener(gyroListener);
            }
        });

        bttReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                angleX = 0;
                angleY = 0;
                angleZ = 0;
                maxRightTilt = 0;
                maxLeftTilt = 0;
                ts = SystemClock.elapsedRealtimeNanos();
                tvX.setText("Rotation around X-axis: 0.0°");
                tvY.setText("Rotation around X-axis: 0.0°");
                tvZ.setText("Rotation around X-axis: 0.0°");
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //float axisX = sensorEvent.values[0];
        float axisY = sensorEvent.values[1];
        //float axisZ = sensorEvent.values[2];

        //float omegaMag = (float) Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);
        // Calcolo il delta di tempo necessario per l'integrazione

        double delta = (sensorEvent.timestamp - ts) * NS2S;

        //if(omegaMag > EPSILON){
        if(abs(axisY) > EPSILON){
            // Omega = dTheta/dt => Theta = I(Omega) => Theta(t + dt) = Theta(t)+dt*delTheta
<<<<<<< HEAD
            if(axisX>EPSILON){
                angleX += delta*axisX;
            }
            if(axisY>EPSILON){
                angleX += delta*axisX;
            }
            if(axisZ>EPSILON){
                angleX += delta*axisX;
            }
        }

        // We leave the variable in radians
        tvX.setText("Rotation around X-axis: " + (float)(angleX * 180 / 3.14) + "°" );
        tvY.setText("Rotation around Y-axis: " + (angleY * 180 / 3.14 + "°");
        tvZ.setText("Rotation around Z-axis: " + angleZ * 180 / 3.14 + "°");
=======
            //angleX += delta*axisX;
            angleY += delta * axisY;
            //angleZ += delta*axisZ;
        }

        // We leave the variable in radians
        //tvX.setText("Rotation around X-axis: " + (float)(angleX * 180. / PI) + "°" );
        tvY.setText("Rotation around Y-axis: " + (float)(angleY * 180. / PI) + "°");
        //tvZ.setText("Rotation around Z-axis: " + (float)(angleZ * 180. / PI) + "°");


        if(angleY > maxRightTilt){
            maxRightTilt = angleY;
        }

        if (angleY < maxLeftTilt) {
            maxLeftTilt = angleY;
        }

        tvX.setText("Max right tilt:" + (float)(maxRightTilt * 180. / PI) + "°");
        tvZ.setText("Max left tilt:" + (float)(maxLeftTilt * 180. / PI) + "°");

>>>>>>> 449c23c0896d79563658d74f6b3713ec9e5ef977

        // Salvo il valore corrente necessario a calcolare il delta successivo
        ts = sensorEvent.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}