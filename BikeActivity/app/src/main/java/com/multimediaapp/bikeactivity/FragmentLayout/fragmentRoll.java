package com.multimediaapp.bikeactivity.FragmentLayout;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.multimediaapp.bikeactivity.DataBase.MyContentProvider;
import com.multimediaapp.bikeactivity.R;

import java.util.ArrayList;


public class fragmentRoll extends Fragment {

    String[] rollCol = { MyContentProvider._ID_Col,
            MyContentProvider.IstantRoll_Col,
            MyContentProvider.TimeStamp_Col
    };

    private LineChart linechart = null;
    private Cursor rollCursor = null;
    private int nRoll = 0;
    private Context context;
    private static final float NS2S = 1.0f / 1000000000.0f; ///Constant to convert from nanoseconds to seconds
    private final int TIME_COL = 2;
    private final int ROLL_COL = 1;

    public fragmentRoll(Context context)
    {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_roll, container, false);
        linechart = v.findViewById(R.id.rollGraph);

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
            axisValues.add(new Entry(
                    (float)rollCursor.getLong(TIME_COL)* NS2S,
                    rollCursor.getFloat(ROLL_COL)));

            // move to the next data roll
            rollCursor.moveToNext();
        }

        /// creating a List of LineDataSet to pass to the linechart
        ArrayList<ILineDataSet> listOfLineDataSets = new ArrayList<>();

        LineDataSet rollLineDataSet = new LineDataSet(axisValues, "Roll");
        rollLineDataSet.setDrawCircles(false);
        rollLineDataSet.setColor(Color.RED);

        /// add to the list the rollLineDataSet create before
        listOfLineDataSets.add(rollLineDataSet);

        /// pass the list to the linechart
        linechart.setData(new LineData(listOfLineDataSets));
        // Inflate the layout for this fragment
        return v;

    }
}