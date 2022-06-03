package com.multimediaapp.bikeactivity;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.multimediaapp.bikeactivity.DataBase.MyContentProvider;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {
    String[] speedCol = {
            MyContentProvider._ID_Col,
            MyContentProvider.IstantSpeed_Col ,
            MyContentProvider.TimeStamp_Col
    };

    String[] rollCol = { MyContentProvider._ID_Col,
            MyContentProvider.IstantRoll_Col,
            MyContentProvider.TimeStamp_Col
    };

    String[] accCol = { MyContentProvider._ID_Col,
            MyContentProvider.IstantAcc_Col,
            MyContentProvider.TimeStamp_Col
    };

    LineChart linechart;

    private Cursor rollCursor;
    private int nRoll = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        linechart =  findViewById(R.id.rollGraph);

        ArrayList<Entry> yAXES = new ArrayList <> ();

        /// set cursor of roll table
       rollCursor =  getContentResolver().query(
                MyContentProvider.ROLL_URI,
                rollCol,
                null , null , null);

       rollCursor.moveToFirst();

       for(int i = 0; i < rollCursor.getCount(); i++)
        {
            yAXES.add(new Entry((float)rollCursor.getLong(2)/1000000000.f, rollCursor.getFloat(1)));
            /// move to the next roll
            rollCursor.moveToNext();
        }


        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet linedataset1 = new LineDataSet(yAXES, "Roll");
        linedataset1.setDrawCircles(false);
        linedataset1.setColor(Color.RED);
        lineDataSets.add(linedataset1);

        linechart.setData(new LineData(lineDataSets));

    }
}