package com.multimediaapp.bikeactivity.FragmentLayout;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.multimediaapp.bikeactivity.DataBase.MyContentProvider;
import com.multimediaapp.bikeactivity.R;

import java.util.ArrayList;


public class fragmentSpeed extends Fragment {

    String[] speedCol = {
            MyContentProvider._ID_Col,
            MyContentProvider.IstantSpeed_Col ,
            MyContentProvider.TimeStamp_Col
    };

    private LineChart linechart = null;
    private Cursor speedCursor = null;
    private int nSpeed = 0;
    private Context context;
    private static final float NS2S = 1.0f / 1000000000.0f; ///Constant to convert from nanoseconds to seconds
    private final int TIME_COL = 2;
    private final int SPEED_COL = 1;

    public fragmentSpeed(Context context)
    {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_speed, container, false);
        linechart = v.findViewById(R.id.speedGraph);

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

        for(int i = 0; i < nSpeed; i++)
        {
            /// add values of database into the axisValues list
            axisValues.add(new Entry(
                    (float)speedCursor.getLong(TIME_COL)* NS2S,
                    speedCursor.getFloat(SPEED_COL)));

            // move to the next data roll
            speedCursor.moveToNext();
        }

        /// creating a List of LineDataSet to pass to the linechart
        ArrayList<ILineDataSet> listOfLineDataSets = new ArrayList<>();

        LineDataSet rollLineDataSet = new LineDataSet(axisValues, "Speed");
        rollLineDataSet.setDrawCircles(false);
        rollLineDataSet.setColor(getResources().getColor(R.color.speed));

        /// add to the list the rollLineDataSet create before
        listOfLineDataSets.add(rollLineDataSet);

        /// pass the list to the linechart
        linechart.setData(new LineData(listOfLineDataSets));

        return v;
    }
}