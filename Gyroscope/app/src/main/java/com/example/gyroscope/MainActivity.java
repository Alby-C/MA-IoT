package com.example.gyroscope;

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

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final String TAG = "MainActivity";

        private Button bttStart = null, bttStop = null;

    private TextView tvX = null, tvY = null, tvZ = null, tvResult = null;

    private ArrayList<double[]> gyroValues = null;

    private Sensor gyro = null;
    private SensorManager gyroManager = null;
    private SensorEventListener gyroListener = null;

    double result;

    private static final float NS2S = 1.0f / 1000000000.0f;
    private double ts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bttStart = findViewById(R.id.bttStart);
        bttStop = findViewById(R.id.bttStop);

        tvX = findViewById(R.id.TVX);
        tvY = findViewById(R.id.TVY);
        tvZ = findViewById(R.id.TVZ);
        tvResult = findViewById(R.id.tvResults);

        gyroValues = new ArrayList<double[]>();

        gyroManager = (SensorManager)getSystemService((Context.SENSOR_SERVICE));
        gyro = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if(gyro != null) Log.i(TAG, "Giroscopio inizializzato correttamente");
        else Log.i(TAG, "Giroscopio non presente");

        gyroListener = this;

        bttStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gyroManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_NORMAL);
                // Inizializzo il timestamp
                ts = SystemClock.elapsedRealtimeNanos();
                // Cancella i valori precedenti della lista
                gyroValues.clear();
                tvResult.setText("");
            }
        });

        bttStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gyroManager.unregisterListener(gyroListener);
                // Da conventire in gradi (con un attimo di attenzione)
                result = calculateRotationAngleY();
                double resultDeg = result * 180 / Math.PI;
                tvResult.setText("Y rotation angle is: " + resultDeg + "°");
                result = 0;
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float axisX = sensorEvent.values[0];
        float axisY = sensorEvent.values[1];
        float axisZ = sensorEvent.values[2];

        // float omegaMag = (float) Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);
        // Calcolo il delta di tempo necessario per l'integrazione
        double delta = (sensorEvent.timestamp - ts) * NS2S;
        double[] values = new double[]{axisX, axisY, axisZ, delta};

        // Salvo i valori nella lista
        gyroValues.add(values);

        tvX.setText("X angular velocity is :" + axisX);
        tvY.setText("Y angular velocity is :" + axisY);
        tvZ.setText("Z angular velocity is :" + axisZ);

        // Salvo il valore corrente necessario a calcolare il delta successivo
        ts = sensorEvent.timestamp;

    }

    private double calculateRotationAngleY(){
        int i = 0;
        double angle = 0;
        Log.i(TAG, "List length is " + String.valueOf(gyroValues.size()));
        while(i < gyroValues.size()){
            double instantY = gyroValues.get(i)[1];
            double instantDelta = gyroValues.get(i)[3];
            angle = angle + instantY*instantDelta;
            i++;
        }
        return angle;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}