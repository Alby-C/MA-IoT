package com.example.as_axispositioning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final String TAG = "MainActivity";

    private Button btnSetAxis = null, btnStart = null, btnStop = null;
    private TextView tvXPosition =null, tvYPosition =null, tvZPosition =null;

    //private ArrayList<Float[]> accValues = null;    //per memorizzare i dati ricevuti dal sensore

    private SensorManager sensorManager = null;     //oggetto che mi permette di gestire i vari sensori
    private Sensor accelerometer = null;            //sensore vero e proprio
    private SensorEventListener listener = null;    //che sta in ascolto per le variazione del sensore, essendo un'interfaccia posso creare
    //una classe ad hoc per il listener, oppure per semplicità la faccio implementare da questa classe

    private float initialX = 0;
    private float initialY = 0;
    private float initialZ = 0;

    //private ReentrantLock lock = new ReentrantLock();

    //accelerazioni misurate in quel momento dal sensore
    private float currX = 0;
    private float currY = 0;
    private float currZ = 0;

    private boolean newValue = false;

    private boolean isAxisSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnSetAxis = findViewById(R.id.btnSetAxis);

        tvXPosition = findViewById(R.id.tvXPosition);
        tvYPosition = findViewById(R.id.tvYPosition);
        tvZPosition = findViewById(R.id.tvZPosition);

        //accValues = new ArrayList<Float[]>();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);           //inizializzazione
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null) {       //controllo che il sensore sia disponibile
            accelerometer = (Sensor) sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //bisogna passare attraverso sensor manager per accedere ad un sensore
            Log.i(TAG,"Sensore accelerometrico inizializzato");
        }
        else
            Log.i(TAG,"Sensore accelerometrico non esistente");

        listener = this;

        btnSetAxis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

                /*int cycles = 10;
                float meanX = 0;
                float meanY = 0;
                float meanZ = 0;*/

                isAxisSet = false;

                /*while(cycles > 0)
                {
                    Log.i(TAG, ""+cycles);
                    if(newValue)
                    {
                        //lock.lock();

                        meanX += currX;
                        meanY += currY;
                        meanZ += currZ;

                        //lock.unlock();

                        newValue = false;
                        cycles--;
                    }
                }

                initialX = meanX / 10;
                initialY = meanY / 10;
                initialZ = meanZ / 10;

                isAxisSet = true;

                //sensorManager.unregisterListener(listener);
                //Toast.makeText(this,"Axis set",Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Axis set");*/
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //faccio partire il listener
                sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL); //terzo parametro "cerca di darmi i dati con un delay di tipo normal"
                //accValues = new ArrayList<Float[]>();
                //tvFinalResult.setText("");
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorManager.unregisterListener(listener); //scollega il listener da qualunque sensore, c'è anche la versione nel quale si specifica il sensore dal quale scollegarsi

                /*String _str = "Mesured " + accValues.size() + " values:\n";
                Float[] _temp;

                for (int i = 0; i < accValues.size(); i++){
                    _temp = accValues.get(i);
                    _str += _temp[0] + ", " + _temp[1] + ", " + _temp[2] + "\n";
                }

                tvFinalResult.setText(_str);*/
            }
        });
    }

    private int cycles = 10;
    private float meanX = 0;
    private float meanY = 0;
    private float meanZ = 0;

    @Override   //metodo di callback chiamato ogni volta che il valore accelerometrico viene chiamato
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i(TAG,"Sensore cambiato.");

        float _x, _y, _z;

        _x = sensorEvent.values[0];     //i valori sono messi in ordine in un array partendo da x
        _y = sensorEvent.values[1];
        _z = sensorEvent.values[2];

        if(!isAxisSet)
        {
            Log.i(TAG, ""+cycles+"\n"+_x+"\n"+_y+"\n"+_z);

            if(cycles > 0)
            {
                //lock.lock();

                meanX += _x;
                meanY += _y;
                meanZ += _z;

                //lock.unlock();


                cycles--;
            }
            else
            {
                cycles = 10;

                initialX = meanX / 10f;
                initialY = meanY / 10f;
                initialZ = meanZ / 10f;

                meanX=0;
                meanY=0;
                meanZ=0;

                isAxisSet = true;

                sensorManager.unregisterListener(listener);
                Log.i(TAG, "Axis set.\n"+initialX+"\n"+initialY+"\n"+initialZ);
            }

            /*//lock.lock();

            currX = _x;     //i valori sono messi in ordine in un array partendo da x
            currY = _y;
            currZ = _z;

            //lock.unlock();

            newValue = true;*/
        }
        else
        {
            if(_x>initialX+.5)
            {
                tvXPosition.setText("X: Tiltied left.\t"+_x);
            }
            else if(_x<initialX-.5)
            {
                tvXPosition.setText("X: Tilted right.\t"+_x);
            }
            else
            {
                tvXPosition.setText("X: Stable.\t"+_x);
            }

            if(_y>initialY+.5)
            {
                tvYPosition.setText("Y: Tilted up.\t"+_y);
            }
            else if(_y<initialY-.5)
            {
                tvYPosition.setText("Y: Tilted down.\t"+_y);
            }
            else
            {
                tvZPosition.setText("Y: Stable.\t"+_y);
            }

            if(_z>initialZ+.5)
            {
                tvZPosition.setText("Z: Tilted boh.\t"+_z);
            }
            else if(_z<initialZ-.5)
            {
                tvZPosition.setText("Z: Tilted boh ma dall'altra parte.\t"+_z);
            }
            else
            {
                tvZPosition.setText("Z: Stable."+_z);
            }
        }

        /*accValues.add(new Float[] {_x, _y, _z});

        tvX.setText("x: " + _x);
        tvY.setText("y: " + _y);
        tvZ.setText("z: " + _z);*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "Accurtezza cambiata. La nuova accuratezza è: " + accuracy);
    }
}

//app che si intrfaccia con il sensore dell'accelerometro, e vada a visualizzare in tempo reale i dati
//dell'accelerazione mediante una textview