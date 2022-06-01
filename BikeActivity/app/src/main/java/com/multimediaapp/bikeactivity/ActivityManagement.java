package com.multimediaapp.bikeactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.multimediaapp.bikeactivity.Accelerometer.Accelerometer;
import com.multimediaapp.bikeactivity.Accelerometer.Jump;
import com.multimediaapp.bikeactivity.Gyroscope.Gyro;
import com.multimediaapp.bikeactivity.Gyroscope.Roll;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;
import com.multimediaapp.bikeactivity.Speed.Speedometer;

import Space.ReferenceSystemCommutator;
import Space.Vector;

public class ActivityManagement extends AppCompatActivity implements IMeasurementHandler, IAccelListener {

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
    private Button bttPause = null;
    private Button bttStop = null;

   ///////////////////////////// Gyro
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

    private Jump jump;


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

        /// Set all text view and button
        tvMaxSpeed = findViewById(R.id.tvMaxSpeed);
        tvAvgSpeed = findViewById(R.id.tvAvgSpeed);
        tvCurrSpeed = findViewById(R.id.tvCurrSpeed);
        tvCurrTilt = findViewById(R.id.tvCurrTilt);
        tvLeftMaxTilt = findViewById(R.id.tvLeftMaxTilt);
        tvRightMaxTilt = findViewById(R.id.tvRightMaxTilt);
        tvCurrX = findViewById(R.id.tvCurrX);
        tvCurrY = findViewById(R.id.tvCurrY);
        tvCurrZ = findViewById( R.id.tvCurrZ);
        bttPause = findViewById(R.id.bttPause);
        bttStop = findViewById(R.id.bttStop);

        /// Location manager instance to pass to the speedometer class
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        speedometer = new Speedometer(lm, this, this);

        /// Jump manager
        jump = new Jump(this);

        /// Roll manager
        roll = new Roll(this, _orientation);

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
        gyroCommutator.SubscribeListener(roll);

        accelerometer.Start();
        gyroscope.Start();
    }

    @Override
    public void onChangeSpeed(float newSpeed, float avgSpeed) {
        tvCurrSpeed.setText(getString(R.string.defaultTVCurrSpeed) + " " + String.format("%.2f", newSpeed));
        tvAvgSpeed.setText(getString(R.string.defaultTVAvgSpeed) + " " + String.format("%.2f",avgSpeed));
        if(newSpeed > maxSpeed)
            maxSpeed = newSpeed;
            tvMaxSpeed.setText(getString(R.string.defaultTVMaxSpeed) + " " + String.format("%.2f",maxSpeed));
    }

    @Override
    public void onChangeRoll(float roll) {
        if (roll > maxRightRoll){
            maxRightRoll = roll;
            tvRightMaxTilt.setText(getString(R.string.defaultTVRightMaxTilt) + " " + (int)maxRightRoll + "°");
        }
        else if (roll < maxLeftRoll) {
            maxLeftRoll = roll;
            tvLeftMaxTilt.setText(getString(R.string.defaultTVLeftMaxtTilt) + " " + (int)maxLeftRoll + "°");
        }

        tvCurrTilt.setText(getString(R.string.defaultTVCurrTilt) + " " + (int)roll + "°");
    }

    @Override
    public void onJumpHappened(long flightTime) {

    }

    private final int AVERAGE_CYCLES = 10;
    private int cycles = AVERAGE_CYCLES;
    private float meanX = 0,meanY = 0, meanZ = 0;
    /**
     * It takes care of initializing the rfCommutator.
     * @param timestamp The timestamp of the measurement.
     * @param newValues New linear acceleration values along three axis in m/s^2.
     */
    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {
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

            Start();
        }
    }
}