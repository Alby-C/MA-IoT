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
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import com.example.gyroscoperealtime.Angle;

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
    private Angle currentAngle = null;

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

        if(isVariationHappening(2)){
            if(!angleFlag){
                currentAngle = new Angle();
            }
        }
        else{
            tvResult.setText("No variation is happening");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // This function is used to detect if there's a rotation happening on the axis
    // which corresponds to the integer d: 0 -> x, 1 -> y, 2 -> z
    private boolean isVariationHappening (int axis){

        boolean verdict = false;
        float variationAngle = 0;

        for(int i = gyroValues.size(); i > 0 && i > gyroValues.size() - 3; i--){

            // Collect the values of the angluar acceleration on the z-axis
            float instantAngleDelta = gyroValues.get(i-1)[axis];
            float instantTimeDelta = gyroValues.get(i-1)[3];
            variationAngle = variationAngle + instantAngleDelta*instantTimeDelta;
        }

        // Determine the angle axis
        Angle.axis axis1 = null;
        switch (axis){
        case 0: axis1 = Angle.axis.X;
        case 1: axis1 = Angle.axis.Y;
        case 2: axis1 = Angle.axis.Z;
        default:
            // Throw exception
        }

        // If the variation is greater than EPSILON we check if the flag used know if we're already
        // measuring the angle or if we should start
        if (Math.abs(variationAngle) > EPSILON) {
            verdict = true;
        }
        else {verdict = false;}
        return verdict;
    }
}