package com.multimediaapp.bikeactivity.FragmentLayout;

import static Miscellaneous.MiscellaneousOperations.NS2S;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.multimediaapp.bikeactivity.DataBase.MyContentProvider;
import com.multimediaapp.bikeactivity.R;

import java.util.ArrayList;
import java.util.Comparator;

import Miscellaneous.MiscellaneousOperations;

/**
 * Manages the data shown in the fragment_speed layout.
 */
public class fragmentSpeed extends Fragment {

    String[] speedCol = {
            MyContentProvider._ID_Col,
            MyContentProvider.InstantSpeed_Col,
            MyContentProvider.TimeStamp_Col
    };

    private float maxSpeed;
    private float avgSpeed;
    private LineChart linechart = null;
    private Cursor speedCursor = null;
    private int nSpeed = 0;
    private Context context;
    private Description description = null;
    private final int TIME_COL = 2;
    private final int SPEED_COL = 1;

    public fragmentSpeed(Context context,
                         float maxSpeed,
                         float avgSpeed){
        this.context = context;
        this.avgSpeed = avgSpeed;
        this.maxSpeed = maxSpeed;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_speed, container, false);
        linechart = v.findViewById(R.id.speedGraph);

        /// Description of roll chart
        description = new Description();
        description.setText("VELOCITÀ [m/s]");
        description.setTextSize(10f);

        /// Array of Roll and Time
        ArrayList<Entry> axisValues = new ArrayList <> ();

        /// set cursor of roll table
        speedCursor =  context.getContentResolver().query(
                MyContentProvider.SPEED_URI,
                speedCol,
                null , null , null);

        /// set cursor to the first data
        speedCursor.moveToFirst();

        /// get number of data
        nSpeed = speedCursor.getCount();

        for(int i = 0; i < nSpeed; i++) {
            /// add values of database into the axisValues list
            axisValues.add(new Entry(
                    (float)speedCursor.getLong(TIME_COL)* NS2S,
                    speedCursor.getFloat(SPEED_COL)));

            // move to the next data roll
            speedCursor.moveToNext();
        }

        axisValues.sort(new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                if(o1.getX() < o2.getX())
                    return -1;
                else if(o1.getX() > o2.getX())
                    return 1;
                else
                    return 0;
            }
        });

        axisValues = MiscellaneousOperations.getSmallerList(axisValues);

        /// creating a List of LineDataSet to pass to the linechart
        ArrayList<ILineDataSet> listOfLineDataSets = new ArrayList<>();

        LineDataSet lineDataSet = new LineDataSet(axisValues, "Speed");

        /// data set settings
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(getResources().getColor(R.color.speed));
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setLineWidth(2f);


        /// add to the list the rollLineDataSet create before
        listOfLineDataSets.add(lineDataSet);

        /// pass the list to the linechart
        linechart.setData(new LineData(listOfLineDataSets));

        /// linechart settings
        linechart.setBackgroundColor(Color.CYAN);
        linechart.setDescription(description);
        return v;
    }
}