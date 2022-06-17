package com.multimediaapp.bikeactivity;

import static android.os.SystemClock.elapsedRealtimeNanos;
import static java.lang.Thread.sleep;
import static Miscellaneous.MiscellaneousOperations.Truncate;

import android.content.ContentValues;
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
import com.multimediaapp.bikeactivity.Sensors.Gyroscope.Gyroscope;
import com.multimediaapp.bikeactivity.Sensors.Gyroscope.Roll;
import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;
import com.multimediaapp.bikeactivity.Sensors.Speed.Speedometer;

import Space.ReferenceSystemCommutator;
import Space.Vector;
import Time.Time;

/**
 * Class that manages all the monitoring mechanisms of motorcycle activity,
 * such as initialization and sensor management.
 */
@SuppressWarnings("ALL")
public class ActivityManagement extends AppCompatActivity implements IMeasurementHandler {

    private final String TAG = ActivityManagement.class.getSimpleName();

    //////////////////////////// Views
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

    private Thread tvUpdater;       ///< Views updater thread

    //////////////////////////// Activity status and variables
    //Volatile keyword ensures that changes to variables are thread-safe
    private volatile boolean isRunning;              ///< true if is running, false if is on pause
    private volatile boolean isPausing;              ///< true if is in pause
    private volatile boolean isStopping;             ///< true if is in stopping phase
    private long startingTimestamp;         ///< [ns] The timestamp of the first Start of the activity
    private long totalPauseLength;          ///< [ns] The total length of all the pauses
    private long startingPauseTimestamp;    ///< [ns] The timestamp of the starting of the last pause

    private float currRoll = 0;
    private float maxLeftRoll = 0;
    private float maxRightRoll = 0;

    private float newSpeed = 0;
    private float avgSpeed = 0;
    private float maxSpeed = 0;

    private float accelX = 0;
    private float accelY = 0;
    private float accelZ = 0;

    /////////////////////////// Accelerometer
    private Accelerometer accelerometer = null;
    private Sensor acc = null;
    private SensorManager accManager = null;

    //////////////////////////// Gyroscope
    private Sensor gyro = null;
    private SensorManager gyroManager = null;
    private Gyroscope gyroscope = null;

    //////////////////////////// Speedometer
    private LocationManager lm = null;
    private Speedometer speedometer = null;

    /////////////////////////// Roll Evaluator
    private Roll roll;

    /////////////////////////// Jump Evaluator
    private Jump jump;

    /////////////////////////// Reference system commutation
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
        /// Establishing screen orientation
        int _contentView;
        int _orientation = getIntent().getIntExtra(getString(R.string.ORIENTATION),ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        switch (_orientation) {
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

        /// Drop previous table on database
        MyContentProvider.db.execSQL("DELETE FROM " + MyContentProvider.ACC_TABLE);
        MyContentProvider.db.execSQL("DELETE FROM " + MyContentProvider.SPEED_TABLE);
        MyContentProvider.db.execSQL("DELETE FROM " + MyContentProvider.ROLL_TABLE);

        saveData = new SaveData(this);

        /// Setting up all textviews and button
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

                ContentValues sessionValues = new ContentValues();

                sessionValues.put(MyContentProvider.MaxSpeed_Col, maxSpeed);
                sessionValues.put(MyContentProvider.MeanSpeed_Col, avgSpeed);
                sessionValues.put(MyContentProvider.RightRoll_Col, maxRightRoll);
                sessionValues.put(MyContentProvider.LeftRoll_Col, maxLeftRoll);
                sessionValues.put(MyContentProvider.TotalTime_Col, activityDuration.toString());

                getContentResolver().insert(MyContentProvider.SESSIONS_URI, sessionValues);

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

        /// Chronometer initialization
        chronometer = new Thread(new Runnable() {
            @Override
            public void run() {
                Chronometer();
            }
        },"Chronometer");

        chronometer.setPriority(Thread.MIN_PRIORITY);
        activityDuration = new Time();

        /// Location manager instance that has to be passed to the speedometer class
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        speedometer = new Speedometer(lm, this);

        /// Jump manager
        jump = new Jump();

        /// Roll manager
        roll = new Roll( _orientation);

        /// Gyroscope service request
        gyroManager = (SensorManager) getSystemService((Context.SENSOR_SERVICE));
        gyro = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyroscope = new Gyroscope(gyro, gyroManager);

        /// Accelerometer service request
        accManager = (SensorManager) getSystemService((Context.SENSOR_SERVICE));
        acc = gyroManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometer = new Accelerometer(acc, accManager);

        /// Starting the initialization of the reference system commutator
        accelerometer.SubscribeListener(this);
        accelerometer.Start();
    }

    /**
     * Starts the activity monitoring, starting all the sensors and subscribing
     * to their respective listeners.
     */
    private void Start() {
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

    /**
     * Pauses the activity monitoring, stopping the chronometer and the capturing
     * of sensors values.
     */
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

    /**
     * Resumes the activity monitoring, after it has been stopped.
     */
    private void Resume(){
        if(isPausing) {
            accelerometer.Start();
            gyroscope.Start();
            speedometer.Start();

            totalPauseLength += elapsedRealtimeNanos() - startingPauseTimestamp;

            isRunning = true;
            isPausing = false;
        }
    }

    /**
     * Permanently stop monitoring activity, stopping the sensors
     * and unsubscribing the listeners.
     */
    private void Stop(){
        isRunning = false;
        isPausing = false;
        isStopping = true;

        /// Generated 3 threads to stop each SensorThreaded, so that everyone has the stop method
        /// triggered at the same time
        Thread[] threads = new Thread[3];

        threads[0] = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "accelerometer stopping");
                accelerometer.Stop();
                Log.i(TAG, "accelerometer stopped");
            }
        });
        threads[1] = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "gyro stopping");
                gyroscope.Stop();
                Log.i(TAG, "gyro stopped");
            }
        });
        threads[2] = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "speedometer stopping");
                speedometer.Stop();
                Log.i(TAG, "speedometer stopped");
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

        /// Waiting for chronometer to stop, otherwise it will interrupt
        try {
            chronometer.join(5000);
        } catch (InterruptedException e) { }

        if(chronometer.isAlive())
            chronometer.interrupt();

        if(tvUpdater.isAlive())
            tvUpdater.interrupt();

        accelerometer.UnsubscribeListener(accelCommutator);
        gyroscope.UnsubscribeListener(gyroCommutator);

        jump.UnsubscribeListener(this);
        roll.UnsubscribeListener(this);
        roll.UnsubscribeListener(saveData);

        accelCommutator.UnsubscribeListener(roll);
        accelCommutator.UnsubscribeListener(this);
        accelCommutator.UnsubscribeListener(saveData);

        gyroCommutator.UnsubscribeListener(roll);
        speedometer.UnsubscribeListener(this);
        speedometer.UnsubscribeListener(saveData);
    }
    /**
     * Class that will be used from chronometer thread, takes care of showing
     * the duration of the activity.
     */
    private void Chronometer(){
        Log.i(TAG +"thr:" + chronometer.getName(), "Thread started");

        int NS2S = 1000000000;

        do {
            while (isRunning) {
                /// Activity duration = current time - starting time - time spent in pause
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

    /**
     * Method assigned to tvUpdater thread, it will get the values of the global
     * variables updated from the sensors and print them out to the screen.
     */
    private void TextviewUpdater(){
        do {
            while(isRunning) {
                String[] rollStrings= new String[]{
                        (int) maxRightRoll + "°",
                        (int) maxLeftRoll + "°",
                        (int) currRoll + "°"
                };
                String[] speedStrings = new String[]{
                        Truncate(newSpeed, 2) + "",
                        Truncate(avgSpeed, 2) + "",
                        Truncate(maxSpeed, 2) + ""
                };
                String[] accStrings = new String[]{
                        Truncate(accelX, 1) + "\nm/s2",
                        Truncate(accelY, 1) + "\nm/s2",
                        Truncate(accelZ, 1) + "\nm/s2"
                };

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /// onChangeRoll
                        tvRightMaxTilt.setText(rollStrings[0]);
                        tvLeftMaxTilt.setText(rollStrings[1]);
                        tvCurrTilt.setText(rollStrings[2]);

                        /// onChangeSpeed
                        tvCurrSpeed.setText(speedStrings[0]);
                        tvAvgSpeed.setText(speedStrings[1]);
                        tvMaxSpeed.setText(speedStrings[2]);

                        /// onChangeAccel
                        tvCurrX.setText(accStrings[0]);
                        tvCurrY.setText(accStrings[1]);
                        tvCurrZ.setText(accStrings[2]);
                    }
                });

                try {
                    sleep(200);     /// Updates Textviews every 200 ms
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
     * It takes care of initializing the rfCommutator, then it will simply get new data.
     * @param timestamp The timestamp of the measurement.
     * @param newValues New linear acceleration values along three axis in m/s^2.
     */
    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {
        if(onRSCommutatorInit){     /// rfCommutator initializing phase
            if(cycles > 0){
                meanX += newValues[0];
                meanY += newValues[1];
                meanZ += newValues[2];

                cycles--;
            }
            else{
                /// First unsubscribes the current instance from receiving updates
                /// from the accelerometer. Then passes the control to the UI thread
                /// to effectively start the activity monitoring.
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