package com.multimediaapp.bikeactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivityPortrait extends AppCompatActivity {

    private final String TAG = "MainActivityPortrait";

    private int currOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    private Switch swOrientation = null;
    private Button btnStart      = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent _intent = getIntent();
        int _contentView;
        int _extra;

        if(_intent.getAction() == "android.intent.action.MAIN")
            _extra = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        else
            _extra = _intent.getIntExtra(getString(R.string.ORIENTATION),ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        switch (_extra){
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                _contentView = R.layout.activity_main_landscape;
                currOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
            default:
                _contentView = R.layout.activity_main_portrait;
                currOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
        }
        setRequestedOrientation(currOrientation);

        super.onCreate(savedInstanceState);
        setContentView(_contentView);


        swOrientation = findViewById(R.id.swOrientation);
        btnStart = findViewById(R.id.btnStart);

        swOrientation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /// creo un nuovo intento che dovrà essere lanciato per chiamare la main activity
                /// ma in landascape mode
                /*Intent toLandscapeIntent = new Intent(getString(R.string.LAUNCH_MAIN_ACTIVITY_LANDSCAPE));

                startActivity(toLandscapeIntent);
                finish();*/

                /*Intent toLandascapeIntent = new Intent(getString(R.string.LAUNCH_MAIN_ACTIVITY_PORTRAIT));

                if(currOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    toLandascapeIntent.putExtra(getString(R.string.CHANGE_ORIENTATION),ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else
                    toLandascapeIntent.putExtra(getString(R.string.CHANGE_ORIENTATION),ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                startActivity(toLandascapeIntent);*/

                if(currOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    currOrientation =ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                else
                    currOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

                setRequestedOrientation(currOrientation);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /// creo un nuovo intento che dovrà essere lanciato per chiamare
                /// la real time stats activity in portrait
                Intent toRealTimePortrait = new Intent((getString(R.string.LAUNCH_REAL_TIME_STATS_PORTRAIT)));

                toRealTimePortrait.putExtra(getString(R.string.ORIENTATION),currOrientation);

                startActivity(toRealTimePortrait);
                finish();
            }
        });
    }
}