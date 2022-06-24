package com.multimediaapp.bikeactivity.FragmentLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.multimediaapp.bikeactivity.R;

/**
 * Manages the data shown in the fragment_stats layout.
 */
public class fragmentStats extends Fragment {

    private Context context;
    private float maxSpeed;
    private float avgSpeed;
    private float maxRightRoll;
    private float maxLeftRoll;
    private String totalTime;
    private TextView tvMaxSpeed = null;
    private TextView tvAvgSpeed = null;
    private TextView tvMaxRightRoll = null;
    private TextView tvMaxLeftRoll = null;
    private TextView tvTotalTime = null;
    private Button homeButton = null;


    public fragmentStats(Context context,
                         float maxSpeed,
                         float avgSpeed,
                         float maxRightRoll,
                         float maxLeftRoll,
                         String totalTime)
    {
        this.context = context;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.maxLeftRoll = maxLeftRoll;
        this.maxRightRoll = maxRightRoll;
        this.totalTime = totalTime;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_stats, container, false);
        homeButton = v.findViewById(R.id.homeButton);
        tvMaxSpeed = v.findViewById(R.id.tvMaxSpeed);
        tvAvgSpeed = v.findViewById(R.id.tvAvgSpeed);
        tvMaxLeftRoll = v.findViewById(R.id.tvLeftMaxRoll);
        tvMaxRightRoll = v.findViewById(R.id.tvRightMaxRoll);
        tvTotalTime = v.findViewById(R.id.tvTotalTime);

        tvMaxSpeed.setText(getString(R.string.defaultTVMaxSpeed)+ " " + String.format("%.2f", maxSpeed)+ " km/h");
        tvAvgSpeed.setText(getString(R.string.defaultTVAvgSpeed) + " " + String.format("%.2f", avgSpeed)+ " km/h");
        tvMaxRightRoll.setText(getString(R.string.defaultTVRightMaxTilt) + " " + String.format("%.2f", maxRightRoll)+ "°");
        tvMaxLeftRoll.setText(getString(R.string.defaultTVLeftMaxRoll)+ " " + String.format("%.2f", -1 * maxLeftRoll)+ "°");
        tvTotalTime.setText(getString(R.string.TotalTime) + ": "+ totalTime + " h");

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMainActivity = new Intent(getString(R.string.RETURN_2_MAIN_ACTIVITY));
                startActivity(toMainActivity);
                // finish();
            }
        });
        return v;
    }
}