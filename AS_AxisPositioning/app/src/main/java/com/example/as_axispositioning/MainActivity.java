package com.example.as_axispositioning;

import static Miscellaneous.MiscellaneousOperations.Truncate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.VectorEnabledTintResources;

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

import com.example.as_axispositioning.Space.Vector;

import java.sql.Struct;
import java.time.LocalTime;
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

    //private ReentrantLock lock = new ReentrantLock();

    private VectorSpace vectorSpace;

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

                /*double xx = Truncate(_x, 7);
                double yy = Truncate(_y, 7);
                double zz = Truncate(_z, 7);*/

                vectorSpace = new VectorSpace(new Vector(
                        Truncate(meanX / 10., 7),
                        Truncate(meanY / 10., 7),
                        Truncate(meanZ / 10., 7)
                ));

                Log.i(TAG, "Axis set.\n"+meanX / 10.+"\n"+meanY / 10.+"\n"+meanZ / 10.);



                meanX=0;
                meanY=0;
                meanZ=0;

                isAxisSet = true;

                sensorManager.unregisterListener(listener);
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
            String _str;
            Vector vector = vectorSpace.ConvertToNewReferenceSystem(new Vector(_x, _y, _z));

            if(vector.X>.5)
            {
                _str = "X: Tiltied left.";
            }
            else if(vector.X<-.5)
            {
               _str = "X: Tilted right.";
            }
            else
            {
                _str = "X: Stable.";
            }
            _str += "\nMeasured:" + _x + "\nTransform: " + vector.X;
            tvXPosition.setText(_str);

            if(vector.Y > .5)
            {
               _str = "Y: Tilted up.";
            }
            else if(vector.Y<-.5)
            {
                _str = "Y: Tilted down.";
            }
            else
            {
                _str = "Y: Stable.";
            }
            _str += "\nMeasured:" + _y + "\nTransform: " + vector.Y;
            tvYPosition.setText(_str);

            if(vector.Z>.5)
            {
                _str = "Z: Tilted boh.";
            }
            else if(vector.Z<.5)
            {
                _str = "Z: Tilted boh ma dall'altra parte.";
            }
            else
            {
                _str = "Z: Stable.";
            }

            _str += "\nMeasured:" + _z + "\nTransform: " + vector.Z;
            tvZPosition.setText(_str);
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