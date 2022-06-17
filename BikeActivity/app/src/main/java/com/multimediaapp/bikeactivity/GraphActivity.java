package com.multimediaapp.bikeactivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.data.Entry;
import com.google.android.material.tabs.TabLayout;
import com.multimediaapp.bikeactivity.DataBase.MyContentProvider;
import com.multimediaapp.bikeactivity.FragmentLayout.VPAdapter;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentAccel;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentRoll;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentSpeed;
import com.multimediaapp.bikeactivity.FragmentLayout.fragmentStats;

import java.util.ArrayList;


public class GraphActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private VPAdapter pagerAdapter;
    public float maxSpeed;
    public float avgSpeed;
    public float maxRightRoll;
    public float maxLeftRoll;
    public String totalTime;

    private Context context;
    private Cursor sessionCursor;

    String[] sessionColumns = {
            MyContentProvider.MaxSpeed_Col,
            MyContentProvider.MeanSpeed_Col,
            MyContentProvider.RightRoll_Col,
            MyContentProvider.LeftRoll_Col,
            MyContentProvider.TotalTime_Col
    };

    private final int MAX_SPEED_COL = 0;
    private final int MEAN_SPEED_COL = 1;
    private final int RIGHT_ROLL_COL= 2;
    private final int LEFT_ROLL_COL= 3;
    private final int TOTAL_TIME_COL= 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_graph);

            tabLayout = findViewById(R.id.tabLayout);
            viewPager = findViewById(R.id.viewPager);
            tabLayout.setupWithViewPager(viewPager);

            context = this;

            sessionCursor = context.getContentResolver().query(
                        MyContentProvider.SESSIONS_URI,
                        sessionColumns,
                        null , null , null
                );
                sessionCursor.moveToLast();

                maxSpeed = sessionCursor.getFloat(MAX_SPEED_COL);
                avgSpeed = sessionCursor.getFloat(MEAN_SPEED_COL);
                maxRightRoll = sessionCursor.getFloat(RIGHT_ROLL_COL);
                maxLeftRoll = sessionCursor.getFloat(LEFT_ROLL_COL);
                totalTime = sessionCursor.getString(TOTAL_TIME_COL);

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