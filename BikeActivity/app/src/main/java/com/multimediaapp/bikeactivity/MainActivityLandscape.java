package com.multimediaapp.bikeactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivityLandscape extends AppCompatActivity {

    private final String TAG = "MainActivityLandscape";

    private Switch swOrientation = null;
    private Button btnStart      = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_portrait);

        /// setto lo switch per l'orientazione e il bottone di start
        swOrientation = findViewById(R.id.swOrientation);
        btnStart = findViewById(R.id.btnStart);

        /// setto il bottone dello swtich a true perche siamo in modalità landscape
        swOrientation.setChecked(true);

        swOrientation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /// creo un nuovo intento che dovrà essere lanciato per chiamare la main activity
                /// ma in portarit mode
                Intent toPortraitIntent = new Intent(getString(R.string.LAUNCH_MAIN_ACTIVITY_PORTRAIT));

                startActivity(toPortraitIntent);
                finish();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /// creo un nuovo intento che dovrà essere lanciato per chiamare
                /// la real time stats activity in landascape
                Intent toRealTimeLandscape = new Intent((getString(R.string.LAUNCH_REAL_TIME_STATS_LANDSCAPE)));

                startActivity(toRealTimeLandscape);
                finish();
            }
        });
    }
}