package com.multimediaapp.bikeactivity.FragmentLayout;

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


public class fragmentRoll extends Fragment {

    String[] rollCol = { MyContentProvider._ID_Col,
            MyContentProvider.InstantRoll_Col,
            MyContentProvider.TimeStamp_Col
    };

    private float maxRightRoll;
    private float maxLeftRoll;
    private LineChart linechart = null;
    private Cursor rollCursor = null;
    private int nRoll = 0;
    private Context context;
    private Description description = null;
    private static final float NS2S = 1.0f / 1000000000.0f; ///Constant to convert from nanoseconds to seconds
    private final int TIME_COL = 2;
    private final int ROLL_COL = 1;

    public fragmentRoll(Context context,
                        float maxRightRoll,
                        float maxLeftRoll)
    {
        this.context = context;
        this.maxLeftRoll = maxLeftRoll;
        this.maxRightRoll = maxRightRoll;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_roll, container, false);
        linechart = v.findViewById(R.id.rollGraph);

        /// Description of roll chart
        description = new Description();
        description.setText("ROLL CHART");
        description.setTextSize(10f);

        /// Array of Roll and Time
        ArrayList<Entry> axisValues = new ArrayList <> ();

        /// set cursor of roll table
        rollCursor =  context.getContentResolver().query(
                   MyContentProvider.ROLL_URI,
                   rollCol,
                  null , null , null);

        /// set cursor to the first data
        rollCursor.moveToFirst();

        /// get number of data
        nRoll = rollCursor.getCount();

        for(int i = 0; i < nRoll; i++)
        {
            /// add values of database into the axisValues list
            axisValues.add(new Entry((float)rollCursor.getLong(TIME_COL)* NS2S,
                    rollCursor.getFloat(ROLL_COL)
                    ));

            // move to the next data roll
            rollCursor.moveToNext();
        }

        /// creating a List of LineDataSet to pass to the linechart
        ArrayList<ILineDataSet> listOfLineDataSets = new ArrayList<>();

        axisValues.sort(new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                if(o1.getX() <= o2.getX())
                    return -1;
                else
                    return 1;
            }
        });

        axisValues = MiscellaneousOperations.getSmallerList(axisValues);

        LineDataSet rollLineDataSet = new LineDataSet(axisValues, "Roll");
        /// Data set settings
        rollLineDataSet.setDrawCircles(false);
        rollLineDataSet.setColor(getResources().getColor(R.color.roll));
        rollLineDataSet.setValueTextSize(10f);
        rollLineDataSet.setLineWidth(3f);


        /// add to the list the rollLineDataSet create before
        listOfLineDataSets.add(rollLineDataSet);

        /// pass the list to the linechart
        linechart.setData(new LineData(listOfLineDataSets));

        /// linechart settings
        linechart.setBackgroundColor(Color.CYAN);
        linechart.setDescription(description);

        // Inflate the layout for this fragment
        return v;

    }
}