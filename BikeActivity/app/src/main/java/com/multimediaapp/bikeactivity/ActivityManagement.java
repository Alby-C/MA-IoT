package com.multimediaapp.bikeactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.multimediaapp.bikeactivity.Accelerometer.Accelerometer;
import com.multimediaapp.bikeactivity.Gyroscope.Roll;
import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;
import com.multimediaapp.bikeactivity.Speed.Speedometer;

public class ActivityManagement extends AppCompatActivity implements IMeasurementHandler {

    private final String TAG = ActivityManagement.class.getSimpleName();
    /// Layout class
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
    private Roll roll = null;

    //////////////////////////// Speedometer
    private LocationManager lm = null;
    private Speedometer speedometer = null;

    /////////////////////////// Accelerometer
    private Accelerometer accellerometer = null;
    private Sensor acc = null;
    private SensorManager accManager = null;
    private float acceleartionAxis = 0;
    private float accelZ = 0;

    /////////////////////////// Variables
    private float maxSpeed = 0;
    private float maxRightTilt= 0;
    private float maxLeftTilt= 0;
    private float angle = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

        /// Location manager instance to pass to the spedometer class
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        speedometer = new Speedometer(lm, this, this);

        /// Gyro request
        gyroManager = (SensorManager)getSystemService((Context.SENSOR_SERVICE));
        gyro = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        roll = new Roll(gyro, gyroManager, this, _orientation);

        /// Accelerometer request
        accManager = (SensorManager)getSystemService((Context.SENSOR_SERVICE));
        acc = gyroManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accellerometer = new Accelerometer(acc, accManager, this, _orientation);
    }

    @Override
    public void onChangeSpeed(float newSpeed, float avgSpeed)
    {
        tvCurrSpeed.setText(getString(R.string.defaultTVCurrSpeed) + " " + String.format("%.2f", newSpeed));
        tvAvgSpeed.setText(getString(R.string.defaultTVAvgSpeed) + " " + String.format("%.2f",avgSpeed));
        if(newSpeed > maxSpeed)
            maxSpeed = newSpeed;
            tvMaxSpeed.setText(getString(R.string.defaultTVMaxSpeed) + " " + String.format("%.2f",maxSpeed));
    }

    @Override
    public void onChangeRoll(float currentRoll)
    {
        this.angle = (float)(currentRoll * 180. / Math.PI);
    }

    @Override
    public void onChangeAcc(float acceleartionAxis, float accelZ)
    {
        this.acceleartionAxis = acceleartionAxis;
        this.accelZ = accelZ;
        /// Complementary filter to have very accuracy data
        this.angle = (float)(0.98 * this.angle + 0.02 * this.acceleartionAxis);

        if(this.angle > maxRightTilt){
            maxRightTilt = this.angle;
        }
        if (this.angle < maxLeftTilt) {
            maxLeftTilt = this.angle;
        }
        tvCurrTilt.setText(getString(R.string.defaultTVCurrTilt) + " " + String.format("%.2f", Math.abs(this.angle)) + "°" );
        tvLeftMaxTilt.setText(getString(R.string.defaultTVLeftMaxtTilt) + " " + String.format("%.2f", -1 * maxLeftTilt) + "°");
        tvRightMaxTilt.setText(getString(R.string.defaultTVRightMaxTilt) + " " + String.format("%.2f",maxRightTilt) + "°");
    }
}