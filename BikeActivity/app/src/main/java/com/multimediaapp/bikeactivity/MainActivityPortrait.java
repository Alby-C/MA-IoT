package com.multimediaapp.bikeactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivityPortrait extends AppCompatActivity {

    private final String TAG = "MainActivityPortrait";

    private Switch swOrientation = null;
    private Button btnStart      = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_portrait);

        swOrientation = findViewById(R.id.swOrientation);
        btnStart = findViewById(R.id.btnStart);

        swOrientation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Intent toLandscapeIntent = new Intent(getString(R.string.LAUNCH_MAIN_ACTIVITY_LANDSCAPE));

                startActivity(toLandscapeIntent);
                finish();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRealTimePortrait = new Intent((getString(R.string.LAUNCH_REAL_TIME_STATS_PORTRAIT)));

                startActivity(toRealTimePortrait);
                finish();
            }
        });
    }
}