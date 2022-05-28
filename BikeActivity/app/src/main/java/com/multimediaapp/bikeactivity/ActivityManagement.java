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

import com.multimediaapp.bikeactivity.Gyroscope.Roll;
import com.multimediaapp.bikeactivity.Interfaces.IMeasurementHandler;
import com.multimediaapp.bikeactivity.Speed.Speedometer;

public class ActivityManagement extends AppCompatActivity implements IMeasurementHandler {

    private final String TAG = ActivityManagement.class.getSimpleName();
    /// layout class
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
   ////////////////////////////////////////////
    private Sensor gyro = null;
    private SensorManager gyroManager = null;
    private Roll roll = null;
    ////////////////////////////////////////////
    private LocationManager lm = null;
    private Speedometer speedometer = null;
    /// variables
    private float maxSpeed = 0;
    private float maxRightTilt= 0;
    private float maxLeftTilt= 0;

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

        /// set all text view and button
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

        /// location manager instance to pass to the spedometer class
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        speedometer = new Speedometer(lm, this, this);

        /// gyro request
        gyroManager = (SensorManager)getSystemService((Context.SENSOR_SERVICE));
        gyro = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        roll = new Roll(gyro, gyroManager, this, this, _orientation);
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
        currentRoll = (float)(currentRoll * 180. / Math.PI);

        if(currentRoll > maxRightTilt){
            maxRightTilt = currentRoll;
        }
        if (currentRoll< maxLeftTilt) {
            maxLeftTilt = currentRoll;
        }
        tvCurrTilt.setText(getString(R.string.defaultTVCurrTilt) + " " + String.format("%.2f", currentRoll) );
        tvLeftMaxTilt.setText(getString(R.string.defaultTVLeftMaxtTilt) + " " + maxLeftTilt);
        tvRightMaxTilt.setText((getString(R.string.defaultTVRightMaxTilt) + " " + maxRightTilt));
    }
}