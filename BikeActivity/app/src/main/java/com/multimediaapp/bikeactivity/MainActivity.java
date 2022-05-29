package com.multimediaapp.bikeactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Main Activity, manages the main menu.
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    /// current orientation of the screen.
    private int currOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    private Switch swOrientation = null;
    private Button btnStart      = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swOrientation = findViewById(R.id.swOrientation);
        btnStart = findViewById(R.id.btnStart);

        swOrientation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            /**
             * Manages the change of te state of the switch, changes the orientation of the screen
             * on the position of the switch.
             * @param b true for landscape orientation, false for portrait
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    currOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                else
                    currOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

                setRequestedOrientation(currOrientation);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            /**
             * When click occours means that the bike activity is starting, so passes control to
             * ActivityManagement.
             */
            @Override
            public void onClick(View view) {
                /// create a new intent that will have to be launched to call
                /// real time stats activity in portrait
                Intent toActivityManagement = new Intent((getString(R.string.LAUNCH_BIKE_ACTIVITY)));

                toActivityManagement.putExtra(getString(R.string.ORIENTATION),currOrientation);

                startActivity(toActivityManagement);
                finish();
            }
        });
    }
}