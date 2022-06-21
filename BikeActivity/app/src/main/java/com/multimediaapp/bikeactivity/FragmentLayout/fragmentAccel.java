package com.multimediaapp.bikeactivity.FragmentLayout;

import static com.multimediaapp.bikeactivity.Sensors.Gyroscope.Roll.NS2S;

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


public class fragmentAccel extends Fragment {

    String[] accCol = {
            MyContentProvider._ID_Col,
            MyContentProvider.InstantLinAcc_Col,
            MyContentProvider.TimeStamp_Col
    };

    private LineChart linechart = null;
    private Cursor linAccCursor = null;
    private int nAcc = 0;
    private Context context;
    private Description description = null;
    private final int LIN_ACC_COL = 1;
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
        description.setText("ACCELERAZIONE [g]");
        description.setTextSize(10f);

        /// Array of Roll and Time
        ArrayList<Entry> axisValues = new ArrayList <> ();

        /// set cursor of roll table
        linAccCursor =  context.getContentResolver().query(
                MyContentProvider.LIN_ACC_URI,
                accCol,
                null , null , null);

        /// set cursor to the first data
        linAccCursor.moveToFirst();

        /// get number of data
        nAcc = linAccCursor.getCount();

        for(int i = 0; i < nAcc; i++)
        {
            /// add values of database into the axisValues list
            axisValues.add(new Entry(
                    (float) linAccCursor.getLong(TIME_COL)* NS2S,
                    linAccCursor.getFloat(LIN_ACC_COL)));

            // move to the next data roll
            linAccCursor.moveToNext();
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

        /// data set for accel X
        LineDataSet lineDataSet = new LineDataSet(axisValues, "Accelerazione");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(getResources().getColor(R.color.accel));
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setLineWidth(2f);

        listOfLineDataSets.add(lineDataSet);

        /// pass the list to the linechart
        linechart.setData(new LineData(listOfLineDataSets));
        /// linechart settings
        linechart.setBackgroundColor(Color.CYAN);
        linechart.setDescription(description);

        // Inflate the layout for this fragment
        return v;
    }
}