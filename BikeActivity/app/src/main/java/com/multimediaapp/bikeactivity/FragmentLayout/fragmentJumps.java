package com.multimediaapp.bikeactivity.FragmentLayout;

import static com.multimediaapp.bikeactivity.Sensors.Gyroscope.Roll.NS2S;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.Entry;
import com.multimediaapp.bikeactivity.DataBase.MyContentProvider;
import com.multimediaapp.bikeactivity.R;

import java.util.ArrayList;
import java.util.Comparator;


public class fragmentJumps extends Fragment {

    String[] accCol = {
            MyContentProvider._ID_Col,
            MyContentProvider.InstantAcc_Col,
            MyContentProvider.TimeStamp_Col
    };
    private final int ACC_COL = 1;
    private final int TIME_COL = 2;
    ArrayList<Entry> accValues;

    private Context context;
    private TextView tvJumps = null;

    public fragmentJumps(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_jumps, container, false);
        tvJumps = v.findViewById(R.id.tvJumps);

        accValues = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                MyContentProvider.ACC_URI,
                accCol,
                null, null, null);

        /// set cursor to the first data
        cursor.moveToFirst();

        /// get number of data
        int nAcc = cursor.getCount();

        for(int i = 0; i < nAcc; i++)
        {
            /// add values of database into the axisValues list
            accValues.add(new Entry(
                    (float)cursor.getLong(TIME_COL)* NS2S,
                    cursor.getFloat(ACC_COL)));

            cursor.moveToNext();
        }

        accValues.sort(new Comparator<Entry>() {
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

        StringBuilder toPrint = new StringBuilder();

        for(int i = 0; i< accValues.size(); i++){
            if(accValues.get(i).getY() < 1.5f) {
                toPrint.append("flag at: ").append(accValues.get(i).getX()).append("\n");
                i = JumpEvaluator(i, toPrint);
            }

        }

        tvJumps.setText(toPrint.toString());
        return v;
    }

    private int JumpEvaluator(int index, StringBuilder str) {
        float prevModule;
        float module = 0;
        boolean goingToPeak = false;

        float jumpStartTimestamp = 0;

        for (int i = index; i >= 0; i--) {
            prevModule = module;
            module = accValues.get(i).getY();

            if(goingToPeak || (goingToPeak = module > 9.5f)) {
                if (module < prevModule) { ///peak reached
                    jumpStartTimestamp = accValues.get(i + 1).getX();
                    str.append("initial peak: ").append(jumpStartTimestamp).append("\n");
                    break;
                }
            }
        }

        goingToPeak = false;
        module = 0;

        for (int i = index; i < accValues.size(); i++) {
            prevModule = module;
            module = accValues.get(i).getY();

            if (goingToPeak || (goingToPeak = module > 9.5f) ) {
                if(module < prevModule) {
                    str.append("final peak: ").append(accValues.get(i - 1).getX()).append("\n");
                    str.append("length: ").append(accValues.get(i - 1).getX() - jumpStartTimestamp).append("\n\n");
                    return i;
                }
            }
        }
        return index;
    }
}

