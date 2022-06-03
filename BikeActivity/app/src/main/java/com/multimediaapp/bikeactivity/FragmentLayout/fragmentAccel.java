package com.multimediaapp.bikeactivity.FragmentLayout;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.multimediaapp.bikeactivity.DataBase.MyContentProvider;
import com.multimediaapp.bikeactivity.R;


public class fragmentAccel extends Fragment {

    String[] accCol = { MyContentProvider._ID_Col,
            MyContentProvider.IstantAcc_Col,
            MyContentProvider.TimeStamp_Col
    };

    private LineChart linechart = null;
    private Cursor accCursor = null;
    private int nAcc = 0;
    private Context context;
    private static final float NS2S = 1.0f / 1000000000.0f; ///Constant to convert from nanoseconds to seconds
    private final int TIME_COL = 2;
    private final int ACC_COL = 1;

    public fragmentAccel(Context context)
    {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accel, container, false);
    }
}