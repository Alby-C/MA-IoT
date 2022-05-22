package com.example.gyroscoperealtime;

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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private Button bttStart = null, bttStop = null;
    private TextView tvResult;

    private final String TAG = "MainActivity";

    private ArrayList<float[]> gyroValues = null;

    private Sensor gyro = null;
    private SensorManager gyroManager = null;
    private SensorEventListener gyroListener = null;

    private static final float NS2S = 1.0f / 1000000000.0f;
    private float ts;

    private static final float EPSILON = 0.06f; // costante di errore accettabil ~ 3.44 gradi
    private boolean angleFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bttStart = findViewById(R.id.bttStart);
        bttStop = findViewById(R.id.bttStop);

        tvResult = findViewById(R.id.tvResult);

        gyroValues = new ArrayList<float[]>();

        gyroManager = (SensorManager)getSystemService((Context.SENSOR_SERVICE));
        gyro = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if(gyro != null) Log.i(TAG, "Giroscopio inizializzato correttamente");
        else Log.i(TAG, "Giroscopio non presente");

        gyroListener = this;

        angleFlag = false;

        bttStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gyroManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_NORMAL);

                ts = SystemClock.elapsedRealtimeNanos();

                gyroValues.clear();
                tvResult.setText("");
            }
        });

        bttStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gyroManager.unregisterListener(gyroListener);
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
        float delta = (sensorEvent.timestamp - ts) * NS2S;
        float[] values = new float[]{axisX, axisY, axisZ, delta};

        gyroValues.add(values);
        ts = sensorEvent.timestamp;

        if(isVariationHappening()){
            tvResult.setText("Variation is happening");
        }
        else{
            tvResult.setText("No variation is happening");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //
    private boolean isVariationHappening (){

        boolean verdict;
        float variationAngle = 0;

        for(int i = gyroValues.size(); i > 0 && i > gyroValues.size() - 3; i--){

            // Collect the values of the angluar acceleration on the z-axis
            float instantY = gyroValues.get(i-1)[2];
            float instantDelta = gyroValues.get(i-1)[3];
            variationAngle = variationAngle + instantY*instantDelta;
        }

        if (Math.abs(variationAngle) > EPSILON) {verdict = true;}
        else {verdict = false;}
        return verdict;
    }
}