package com.multimediaapp.bikeactivity;
import com.multimediaapp.bikeactivity.FragmentLayout.VPAdapter;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentAccel;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentRoll;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentSpeed;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentStats;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);

        pagerAdapter = new VPAdapter(
                getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        pagerAdapter.addFragment(new fragmentStats(this), "Stats");
        pagerAdapter.addFragment(new fragmentRoll(this), "Roll");
        pagerAdapter.addFragment(new fragmentSpeed(this), "Speed");
        pagerAdapter.addFragment(new fragmentAccel(this), "Accel");

        viewPager.setAdapter(pagerAdapter);

    }
}