package com.multimediaapp.bikeactivity;

import static android.os.SystemClock.elapsedRealtimeNanos;
import static java.lang.Thread.sleep;
import static Miscellaneous.MiscellaneousOperations.Truncate;

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

import androidx.appcompat.app.AppCompatActivity;

import com.multimediaapp.bikeactivity.Sensors.Accelerometer.Accelerometer;
import com.multimediaapp.bikeactivity.Sensors.Accelerometer.Jump;
import com.multimediaapp.bikeactivity.DataBase.MyContentProvider;
import com.multimediaapp.bikeactivity.DataBase.SaveData;
import com.multimediaapp.bikeactivity.Sensors.Gyroscope.Gyro;
import com.multimediaapp.bikeactivity.Sensors.Gyroscope.Roll;
import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;
import com.multimediaapp.bikeactivity.Sensors.Speed.Speedometer;

import Space.ReferenceSystemCommutator;
import Space.Vector;
import Time.Time;

public class ActivityManagement extends AppCompatActivity implements IMeasurementHandler {

    private final String TAG = ActivityManagement.class.getSimpleName();

    //////////////////////////// Layout classes
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

    private Thread tvUpdater;

    //////////////////////////// Activity status
    private volatile boolean isRunning;              ///true if is running, false if is on pause
    private volatile boolean isPausing;              ///true if is in pause
    private volatile boolean isStopping;             ///true if is in stopping phase
    private long startingTimestamp;         ///[ns] The timestamp of the first Start of the activity
    private long totalPauseLength;          ///[ns] The total length of all the pauses
    private long startingPauseTimestamp;    ///[ns] The timestamp of the starting of the last pause

    /////////////////////////// Variables
    private float maxSpeed = 0;
    private float maxRightRoll = 0;
    private float maxLeftRoll = 0;

    //////////////////////////// Gyroscope
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

    /////////////////////////// Jump Evaluator
    private Jump jump;

    /////////////////////////// Reference systems
    private ReferenceSystemCommutator rsCommutator;
    private AccelCommutator accelCommutator;
    private GyroCommutator gyroCommutator;

    /////////////////////////// Activity duration
    private Thread chronometer;
    private Time activityDuration;

    /////////////////////////// Saving management
    private SaveData saveData;

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
        MyContentProvider.db.execSQL("DELETE FROM " + MyContentProvider.ROLL_TABLE);

        saveData = new SaveData(this);

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

                toGraphActivity.putExtra(getString(R.string.defaultTVMaxSpeed), maxSpeed);
                toGraphActivity.putExtra(getString(R.string.defaultTVAvgSpeed), avgSpeed);
                toGraphActivity.putExtra(getString(R.string.defaultTVRightMaxTilt), maxRightRoll);
                toGraphActivity.putExtra(getString(R.string.defaultTVLeftMaxRoll), maxLeftRoll);
                toGraphActivity.putExtra(getString(R.string.TotalTime), activityDuration.toString());
                startActivity(toGraphActivity);
                finish();
            }
        });

        tvUpdater = new Thread(new Runnable() {
            @Override
            public void run() {
                TextviewUpdater();
            }
        });

        /// Activity status init
        isRunning = false;
        isPausing = true;
        isStopping = false;
        totalPauseLength = 0;

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
        jump = new Jump();

        /// Roll manager
        roll = new Roll( _orientation);

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

    /**
     * Start() method run after the onChangeAccel in Activity management start for the first time
     * to getting the new reference system
     */

    private void Start(){
        accelerometer.SubscribeListener(accelCommutator);
        gyroscope.SubscribeListener(gyroCommutator);

        jump.SubscribeListener(this);
        roll.SubscribeListener(this);
        roll.SubscribeListener(saveData);

        accelCommutator.SubscribeListener(roll);
        accelCommutator.SubscribeListener(this);
        accelCommutator.SubscribeListener(saveData);

        gyroCommutator.SubscribeListener(roll);
        speedometer.SubscribeListener(this);
        speedometer.SubscribeListener(saveData);

        accelerometer.Start();
        gyroscope.Start();
        speedometer.Start();

        startingTimestamp = elapsedRealtimeNanos();
        isPausing = false;
        isRunning = true;

        Log.i(TAG, "launching threads");

        chronometer.start();
        tvUpdater.start();
    }

    private void Pause(){
        if(isRunning) {
            accelerometer.Pause();
            gyroscope.Pause();
            speedometer.Pause();

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

        ///Generated 3 threads to stop each SensorThreaded, so that everyone has the stop method
        ///triggered at the same time
        Thread[] threads = new Thread[3];

        threads[0] = new Thread(new Runnable() {
            @Override
            public void run() {
                accelerometer.Stop();
            }
        });
        threads[1] = new Thread(new Runnable() {
            @Override
            public void run() {
                gyroscope.Stop();
            }
        });
        threads[2] = new Thread(new Runnable() {
            @Override
            public void run() {
                speedometer.Stop();
            }
        });

        for (Thread th:
             threads) {
            th.start();
        }
        for (Thread th:
             threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                Log.e(TAG +"thr:" + chronometer.getName(), e.toString());
            }
        }

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
    public void onChangeRoll(long timestamp,float currRoll) {
        if (currRoll > maxRightRoll)
            maxRightRoll = currRoll;
        else if (currRoll < maxLeftRoll)
            maxLeftRoll = currRoll;

        this.currRoll = currRoll;
    }

    @Override
    public void onChangeSpeed(long timestamp, float newSpeed, float avgSpeed) {
        this.newSpeed = newSpeed;
        this.avgSpeed = avgSpeed;

        if(newSpeed > maxSpeed)
            maxSpeed = newSpeed;
    }

    @Override
    public void onJumpHappened(long flightTime) {

    }

    private float currRoll = 0;

    private float newSpeed = 0;
    private float avgSpeed = 0;

    private float accelX = 0;
    private float accelY = 0;
    private float accelZ = 0;

    private void TextviewUpdater(){
        do {
            while(isRunning) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //onChangeRoll
                        tvRightMaxTilt.setText((int) maxRightRoll + "°");
                        tvLeftMaxTilt.setText((int) maxLeftRoll + "°");
                        tvCurrTilt.setText((int) currRoll + "°");

                        ///onChangeSpeed
                        tvCurrSpeed.setText(String.format("%.2f", newSpeed));
                        tvAvgSpeed.setText(String.format("%.2f", avgSpeed));
                        tvMaxSpeed.setText(String.format("%.2f", maxSpeed));

                        ///onChangeAccel
                        tvCurrX.setText(Truncate(accelX, 1) + "\nm/s2");
                        tvCurrY.setText(Truncate(accelY, 1) + "\nm/s2");
                        tvCurrZ.setText(Truncate(accelZ, 1) + "\nm/s2");
                    }
                });


                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    Log.e(TAG + "thr:" + chronometer.getName(), e.toString());
                }
            }
        }while(!isStopping);
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
                accelerometer.UnsubscribeListener(this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        accelerometer.Stop();


                        cycles = AVERAGE_CYCLES;

                        rsCommutator = new ReferenceSystemCommutator(new Vector(meanX / cycles, meanY / cycles, meanZ / cycles));

                        accelCommutator = new AccelCommutator(rsCommutator);
                        gyroCommutator = new GyroCommutator(rsCommutator);

                        onRSCommutatorInit = false;

                        Start();
                    }
                });
            }
        }
        else{
            accelX = newValues[0];
            accelY = newValues[1];
            accelZ = newValues[2];
        }
    }

    @Override
    public void onChangeGyro(long timestamp, float[] newValues) {

    }
}