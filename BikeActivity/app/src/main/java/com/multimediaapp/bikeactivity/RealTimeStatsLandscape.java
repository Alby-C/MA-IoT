package com.multimediaapp.bikeactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class RealTimeStatsLandscape extends AppCompatActivity {

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_landscape);

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


    }
}