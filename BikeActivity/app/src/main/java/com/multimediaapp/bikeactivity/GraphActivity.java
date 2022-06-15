package com.multimediaapp.bikeactivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.multimediaapp.bikeactivity.FragmentLayout.VPAdapter;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentAccel;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentRoll;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentSpeed;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentStats;


public class GraphActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private VPAdapter pagerAdapter;
    public float maxSpeed;
    public float avgSpeed;
    public float maxRightRoll;
    public float maxLeftRoll;
    public String totalTime;
    public Button homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_graph);

            tabLayout = findViewById(R.id.tabLayout);
            viewPager = findViewById(R.id.viewPager);
            tabLayout.setupWithViewPager(viewPager);
            homeButton = findViewById(R.id.homeButton);

            Intent _intent = getIntent();
            maxSpeed = _intent.getFloatExtra(getString(R.string.defaultTVMaxSpeed), 0);
            avgSpeed = _intent.getFloatExtra(getString(R.string.defaultTVAvgSpeed), 0);
            maxLeftRoll = _intent.getFloatExtra(getString(R.string.defaultTVLeftMaxRoll), 0);
            maxRightRoll = _intent.getFloatExtra(getString(R.string.defaultTVRightMaxTilt), 0);
            totalTime = _intent.getStringExtra(getString(R.string.TotalTime));


            pagerAdapter = new VPAdapter(
                    getSupportFragmentManager(),
                    FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

            pagerAdapter.addFragment(new fragmentStats(this,
                    maxSpeed, avgSpeed,
                    maxRightRoll, maxLeftRoll,
                    totalTime), "Stats");

            pagerAdapter.addFragment(new fragmentRoll(this, maxRightRoll, maxLeftRoll), "Roll");
            pagerAdapter.addFragment(new fragmentSpeed(this, maxSpeed, avgSpeed), "Speed");
            pagerAdapter.addFragment(new fragmentAccel(this), "Accel");

            viewPager.setAdapter(pagerAdapter);

            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toMainActivity = new Intent(getString(R.string.RETURN_2_MAIN_ACTIVITY));
                    startActivity(toMainActivity);
                    finish();
                }
            });
    }


}