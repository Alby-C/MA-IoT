package com.multimediaapp.bikeactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class RealTimeStatsPortrait extends AppCompatActivity implements Interface {
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

    private LocationManager lm = null;
    private Speedometer speedometer = null;

    private float maxSpeed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int _contentView;
        int _orientation = getIntent().getIntExtra(getString(R.string.ORIENTATION),ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(_orientation);
        switch (_orientation){
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                _contentView = R.layout.activity_real_time_stats_landscape;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
            default:
                _contentView = R.layout.activity_real_time_stats_portrait;
                break;
        }


        super.onCreate(savedInstanceState);
        setContentView(_contentView);

        /// setto tutte le mie text view e i miei bottoni
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

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        speedometer = new Speedometer(lm, this, this);
    }

    @Override
    public void onChangeSpeed(float newSpeed, float avgSpeed) {
        tvCurrSpeed.setText(getString(R.string.defaultTVCurrSpeed) + " " + newSpeed);
        tvAvgSpeed.setText(getString(R.string.defaultTVAvgSpeed) + " " + avgSpeed);
        if(newSpeed > maxSpeed)
            maxSpeed = newSpeed;
            tvMaxSpeed.setText(getString(R.string.defaultTVMaxSpeed) + " " + maxSpeed );

    }
}