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

import Miscellaneous.MiscellaneousOperations;


public class fragmentAccel extends Fragment {

    String[] accCol = {
            MyContentProvider._ID_Col,
            MyContentProvider.InstantAccX_Col,
            //MyContentProvider.InstantAccY_Col,
            //MyContentProvider.InstantAccZ_Col,
            MyContentProvider.TimeStamp_Col
    };

    private LineChart linechart = null;
    private Cursor accCursor = null;
    private int nAcc = 0;
    private Context context;
    private Description description = null;
    private static final float NS2S = 1.0f / 1000000000.0f; ///Constant to convert from nanoseconds to seconds
    private final int ACC_COL = 1;
    private final int TIME_COL = 2;


    public fragmentAccel(Context context)
    {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_accel, container, false);

        // Inflate the layout for this fragment
        linechart = v.findViewById(R.id.accelGraph);

        /// Description of roll chart
        description = new Description();
        description.setText("ACCELERATION CHART");
        description.setTextSize(10f);

        /// Array of Roll and Time
        ArrayList<Entry> AccValues = new ArrayList <> ();

        /// set cursor of roll table
        accCursor =  context.getContentResolver().query(
                MyContentProvider.ACC_URI,
                accCol,
                null , null , null);

        /// set cursor to the first data
        accCursor.moveToFirst();

        /// get number of data
        nAcc = accCursor.getCount();

        for(int i = 0; i < nAcc; i++)
        {
            /// add values of database into the axisValues list
            AccValues.add(new Entry(
                    (float)accCursor.getLong(TIME_COL)* NS2S,
                    accCursor.getFloat(ACC_COL)));

            // move to the next data roll
            accCursor.moveToNext();
        }

        AccValues = MiscellaneousOperations.getSmallerList(AccValues);

        /// creating a List of LineDataSet to pass to the linechart
        ArrayList<ILineDataSet> listOfLineDataSets = new ArrayList<>();

        /// data set for accel X
        LineDataSet rollLineDataSetAccX = new LineDataSet(AccValues, "Acc X");
        rollLineDataSetAccX.setDrawCircles(false);
        rollLineDataSetAccX.setColor(getResources().getColor(R.color.xAxisAccel));
        rollLineDataSetAccX.setValueTextSize(10f);
        rollLineDataSetAccX.setLineWidth(3f);

        listOfLineDataSets.add(rollLineDataSetAccX);

        /// pass the list to the linechart
        linechart.setData(new LineData(listOfLineDataSets));
        /// linechart settings
        linechart.setBackgroundColor(Color.CYAN);
        linechart.setDescription(description);

        // Inflate the layout for this fragment
        return v;
    }
}