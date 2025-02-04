package com.multimediaapp.bikeactivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Main Activity, manages the main menu.
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    /// Current orientation of the screen.
    private int currOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    private Switch swOrientation = null;
    private Button btnStart      = null;
    private Button btnLastSession = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        if(savedInstanceState != null){
            Intent intent = getIntent();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swOrientation = findViewById(R.id.swOrientation);
        btnStart = findViewById(R.id.btnStart);
        btnLastSession = findViewById(R.id.btnLastSession);

        swOrientation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            /**
             * Manages the change of te state of the switch, changes the orientation of the screen
             * on the position of the switch.
             * @param b true for landscape orientation, false for portrait
             */
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    currOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    Log.i(TAG, "Orientation changed: portrait");
                }
                else {
                    Log.i(TAG,"Orientation changed: landscape");
                    currOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                }
                setRequestedOrientation(currOrientation);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            /**
             * When click occurs means that the bike activity is starting, so passes control to
             * ActivityManagement.
             */
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Start clicked");
                /// Create a new intent that will have to be launched to call
                /// Real time stats activity in portrait
                Intent toActivityManagement = new Intent((getString(R.string.LAUNCH_BIKE_ACTIVITY)));

                toActivityManagement.putExtra(getString(R.string.ORIENTATION),currOrientation);

                startActivity(toActivityManagement);
                finish();
            }
        });

        btnLastSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Last session clicked");
                Intent toLastSessionGraph = new Intent(getString(R.string.LAUNCH_GRAPH_ACTIVTY_LAST_SESSION));
                startActivity(toLastSessionGraph);
                finish();
            }
        });
    }
}