package com.multimediaapp.bikeactivity;
import com.multimediaapp.bikeactivity.FragmentLayout.VPAdapter;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentAccel;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentRoll;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentSpeed;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentStats;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;


public class GraphActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private VPAdapter pagerAdapter;
    public float maxSpeed;
    public float avgSpeed;
    public float maxRightRoll;
    public float maxLeftRoll;
    public String totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent _intent = getIntent();
        maxSpeed = _intent.getFloatExtra(getString(R.string.defaultTVMaxSpeed),0);
        avgSpeed = _intent.getFloatExtra(getString(R.string.defaultTVAvgSpeed), 0);
        maxLeftRoll = _intent.getFloatExtra(getString(R.string.defaultTVLeftMaxRoll),0);
        maxRightRoll = _intent.getFloatExtra(getString(R.string.defaultTVRightMaxTilt),0);
        totalTime = _intent.getStringExtra(getString(R.string.TotalTime));

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);

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

    }
}