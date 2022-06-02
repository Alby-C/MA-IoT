package com.multimediaapp.bikeactivity;

import static android.os.SystemClock.elapsedRealtimeNanos;
import static java.lang.Thread.*;
import static Miscellaneous.MiscellaneousOperations.Truncate;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.multimediaapp.bikeactivity.Accelerometer.Accelerometer;
import com.multimediaapp.bikeactivity.Accelerometer.Jump;
import com.multimediaapp.bikeactivity.DataBase.MyContentProvider;
import com.multimediaapp.bikeactivity.DataBase.SaveData;
import com.multimediaapp.bikeactivity.Gyroscope.Gyro;
import com.multimediaapp.bikeactivity.Gyroscope.Roll;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;
import com.multimediaapp.bikeactivity.Interfaces.IRollListener;
import com.multimediaapp.bikeactivity.Speed.Speedometer;

import Space.ReferenceSystemCommutator;
import Space.Vector;
import Time.Time;

public class ActivityManagement extends AppCompatActivity implements IMeasurementHandler, IAccelListener, IRollListener {

    private final String TAG = ActivityManagement.class.getSimpleName();

    /// Layout classes
    private TextView tvMaxSpeed = null;
    private TextView tvAvgSpeed = null;
    private TextView tvCurrSpeed = null;
    private TextView tvCurrTilt = null;
    private TextView tvLeftMaxTilt = null;
    private TextView tvRightMaxTilt= null;
    private TextView tvCurrX = null;
    private TextView tvCurrY = null;
    private TextView tvCurrZ = null;
    private TextView tvElapsedTime = null;
    private Button btnPauseResume = null;
    private Button btnStop = null;

    //////////////////////////// Activity status
    private boolean isRunning;              ///true if is running, false if is on pause
    private boolean isPausing;              ///true if is in pause
    private boolean isStopping;             ///true if is in stopping phase
    private long startingTimestamp;         ///[ns] The timestamp of the first Start of the activity
    private long totalPauseLength;          ///[ns] The total length of all the pauses
    private long startingPauseTimestamp;    ///[ns] The timestamp of the starting of the last pause

    //////////////////////////// Gyro
    private Sensor gyro = null;
    private SensorManager gyroManager = null;
    private Gyro gyroscope = null;

    //////////////////////////// Speedometer
    private LocationManager lm = null;
    private Speedometer speedometer = null;

    /////////////////////////// Accelerometer
    private Accelerometer accelerometer = null;
    private Sensor acc = null;
    private SensorManager accManager = null;

    /////////////////////////// Roll Evaluator
    private Roll roll;

    /////////////////////////// Variables
    private float maxSpeed = 0;
    private float maxRightRoll = 0;
    private float maxLeftRoll = 0;

    /////////////////////////// Reference systems
    private ReferenceSystemCommutator rsCommutator;
    private AccelCommutator accelCommutator;
    private GyroCommutator gyroCommutator;

    /////////////////////////// Jump Evaluator
    private Jump jump;

    /////////////////////////// Activity duration
    private Thread chronometer;
    private Time activityDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int _contentView;
        int _orientation = getIntent().getIntExtra(getString(R.string.ORIENTATION),ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        switch (_orientation)
        {
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                _contentView = R.layout.activity_management_landscape;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
            default:
                _contentView = R.layout.activity_management_portrait;
                break;
        }
        setRequestedOrientation(_orientation);
        super.onCreate(savedInstanceState);
        setContentView(_contentView);

        /// Drop previous table
        MyContentProvider.db.execSQL("DELETE FROM " + MyContentProvider.ACC_TABLE);
        MyContentProvider.db.execSQL("DELETE FROM " + MyContentProvider.SPEED_TABLE);
        MyContentProvider.db.execSQL("DELETE FROM  " + MyContentProvider.ROLL_TABLE);


        /// Set all text view and button
        tvMaxSpeed = findViewById(R.id.tvMaxSpeed);
        tvAvgSpeed = findViewById(R.id.tvAverageSpeed);
        tvCurrSpeed = findViewById(R.id.tvCurrSpeed);
        tvCurrTilt = findViewById(R.id.tvCurrRoll);
        tvLeftMaxTilt = findViewById(R.id.tvLeftMaxRoll);
        tvRightMaxTilt = findViewById(R.id.tvRightMaxRoll);
        tvCurrX = findViewById(R.id.tvCurrX);
        tvCurrY = findViewById(R.id.tvCurrY);
        tvCurrZ = findViewById(R.id.tvCurrZ);
        tvElapsedTime = findViewById(R.id.tvElapsedTime);
        btnPauseResume = findViewById(R.id.btnPauseResume);
        btnStop = findViewById(R.id.btnStop);

        btnPauseResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPausing){
                    Pause();
                    btnPauseResume.setText(getText(R.string.btnResume));
                }
                else {
                    Resume();
                    btnPauseResume.setText(getText(R.string.btnPause));
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stop();

                Intent toGraphActivity = new Intent((getString(R.string.LAUNCH_GRAPH_ACTIVITY)));
                startActivity(toGraphActivity);
                finish();
            }
        });

        /// Activity status init
        isRunning = false;
        isPausing = true;
        isStopping = false;
        totalPauseLength = 0;

        SaveData saveData = new SaveData(this);

        /// Location manager instance to pass to the speedometer class
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        speedometer = new Speedometer(lm, this, this);

        /// Chronometer initialization
        chronometer = new Thread(new Runnable() {
            @Override
            public void run() {
                Chronometer();
            }
        },"chronometer");
        chronometer.setPriority(Thread.MIN_PRIORITY);
        activityDuration = new Time();

        /// Jump manager
        jump = new Jump(this);

        /// Roll manager
        roll = new Roll( _orientation);
        roll.SubscribeListener(this);
        roll.SubscribeListener(saveData);

        /// Gyro request
        gyroManager = (SensorManager)getSystemService((Context.SENSOR_SERVICE));
        gyro = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyroscope = new Gyro(gyro, gyroManager);

        /// Accelerometer request
        accManager = (SensorManager)getSystemService((Context.SENSOR_SERVICE));
        acc = gyroManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometer = new Accelerometer(acc, accManager);

        ReferenceSystemCommutatorInit();
    }

    private void ReferenceSystemCommutatorInit() {
        accelerometer.SubscribeListener(this);
        accelerometer.Start();
    }

    private void Start(){
        accelerometer.SubscribeListener(accelCommutator);
        gyroscope.SubscribeListener(gyroCommutator);

        accelCommutator.SubscribeListener(roll);
        accelCommutator.SubscribeListener(this);
        gyroCommutator.SubscribeListener(roll);

        accelerometer.Start();
        gyroscope.Start();
        speedometer.Start();

        startingTimestamp = elapsedRealtimeNanos();
        isPausing = false;
        isRunning = true;

        Log.i(TAG, "launching thread");
        chronometer.start();
    }

    private void Pause(){
        if(isRunning) {
            accelerometer.Stop();
            gyroscope.Stop();
            speedometer.Stop();

            startingPauseTimestamp = elapsedRealtimeNanos();

            isPausing = true;
            isRunning = false;
        }
    }

    private void Resume(){
        if(!isRunning){
            accelerometer.Start();
            gyroscope.Start();
            speedometer.Start();

            totalPauseLength += elapsedRealtimeNanos() - startingPauseTimestamp;
            isRunning = true;
            isPausing = false;
        }
    }

    private void Stop(){
        accelerometer.UnsubscribeListener(accelCommutator);
        gyroscope.UnsubscribeListener(gyroCommutator);

        accelCommutator.UnsubscribeListener(roll);
        accelCommutator.UnsubscribeListener(this);
        gyroCommutator.UnsubscribeListener(roll);

        accelerometer.Stop();
        gyroscope.Stop();
        speedometer.Stop();

        isRunning = false;
        isPausing = false;
        isStopping = true;

        try {
            chronometer.join(5000);
        } catch (InterruptedException e) {
            chronometer.interrupt();
        }
    }

    /**
     * Class that will be used from chronometer thread, takes care of showing the
     * the duration of the activity.
     */
    private void Chronometer(){
        Log.i(TAG +"thr:" + chronometer.getName(), "Thread started");
        int NS2S = 1000000000;
        do {
            while (isRunning) {
                /// current time - starting time - time spent in pause
                activityDuration = new Time((int)((elapsedRealtimeNanos()-startingTimestamp-totalPauseLength)/NS2S));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvElapsedTime.setText(activityDuration.toString());
                    }
                });

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(TAG +"thr:" + chronometer.getName(), e.toString());
                }
            }
        }while(!isStopping);
    }

    @Override
    public void onChangeSpeed(float newSpeed, float avgSpeed) {
        tvCurrSpeed.setText(String.format("%.2f", newSpeed));
        tvAvgSpeed.setText(String.format("%.2f",avgSpeed));
        if(newSpeed > maxSpeed)
            maxSpeed = newSpeed;
            tvMaxSpeed.setText(String.format("%.2f",maxSpeed));
    }


    @Override
    public void onChangeRoll(float roll, long timestamp) {
        if (roll > maxRightRoll){
            maxRightRoll = roll;
            tvRightMaxTilt.setText((int)maxRightRoll + "°");
        }
        else if (roll < maxLeftRoll) {
            maxLeftRoll = roll;
            tvLeftMaxTilt.setText((int)maxLeftRoll + "°");
        }
        tvCurrTilt.setText((int)roll + "°");
    }

    @Override
    public void onChangeSpeed(float newSpeed, float avgSpeed, long timestamp) {

        tvCurrSpeed.setText(String.format("%.2f", newSpeed));
        tvAvgSpeed.setText(String.format("%.2f",avgSpeed));

        if(newSpeed > maxSpeed)
            maxSpeed = newSpeed;
        tvMaxSpeed.setText(String.format("%.2f",maxSpeed));
    }

    @Override
    public void onJumpHappened(long flightTime) {

    }

    private final int AVERAGE_CYCLES = 10;
    private int cycles = AVERAGE_CYCLES;
    private float meanX = 0,meanY = 0, meanZ = 0;
    private boolean onRSCommutatorInit = true;
    /**
     * It takes care of initializing the rfCommutator.
     * @param timestamp The timestamp of the measurement.
     * @param newValues New linear acceleration values along three axis in m/s^2.
     */
    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {
        if(onRSCommutatorInit){
            if(cycles > 0){
                meanX+=newValues[0];
                meanY+=newValues[1];
                meanZ+=newValues[2];

                cycles--;
            }
            else{

                accelerometer.Stop();
                accelerometer.UnsubscribeListener(this);

                cycles = AVERAGE_CYCLES;

                rsCommutator = new ReferenceSystemCommutator(new Vector(meanX/cycles,meanY/cycles, meanZ/cycles));

                accelCommutator = new AccelCommutator(rsCommutator);
                gyroCommutator = new GyroCommutator(rsCommutator);

                onRSCommutatorInit = false;
                Start();
            }
        }
        else{
            tvCurrX.setText(Truncate(newValues[0],1)+"\nm/s2");
            tvCurrY.setText(Truncate(newValues[1],1)+"\nm/s2");
            tvCurrZ.setText(Truncate(newValues[2],1)+"\nm/s2");
        }

    }
}