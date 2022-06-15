package Miscellaneous;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class MiscellaneousOperations {
    private static final int SIGNIFICANT_DIGITS = 5;
    private static final float TRUNCATE_CONSTANT = (float) Math.pow(10, SIGNIFICANT_DIGITS);

    /**
     * Truncates the specified number to the number of significant digits specified
     * by the SIGNIFICANT_DIGITS constant.
     * @param num The number to be truncated.
     * @return The num number truncated.
     */
    public static float Truncate(double num){
        long numToLong = (long)(num * TRUNCATE_CONSTANT);
        return (float)(numToLong / TRUNCATE_CONSTANT);
    }

    /**
     * Truncates the specified number to the specified number of significant digits.
     * @param num The number to be truncated.
     * @param significantDigits The number of significant digits.
     * @return The num number truncated.
     */
    public static float Truncate(double num, int significantDigits){
        float sigDigits = (float)Math.pow(10, significantDigits);
        long numToLong = (long)(num * sigDigits);
        return (float)numToLong / sigDigits;
    }

    /**
     * Samples the list to get the relative maxima and minima.
     * @param list The list we need to sample.
     * @return The sampled list.
     */

    public static ArrayList<Entry> getSmallerList(ArrayList<Entry> list){
        ArrayList<Entry> newList = new ArrayList<>(50);
        boolean isGoingUp = false;  ///< flag to know if the values are going up or down
        int size = list.size();
        int precision = (int)(size * 0.001f);
        if(precision == 0)  //se la precisione è uguale a 0 allora la nuova listà risulterà uguale alla lista corrente
            return list;

        float prevY;
        float currY = list.get(0).getY();

        newList.add(list.get(0));

        for (Entry en :
                list) {
            if (en.getY() != currY) {
                isGoingUp = currY < en.getY();
                break;
            }
        }

        for (int i = 1; i < size; i ++){
            prevY = currY;
            currY = list.get(i).getY();

            if(isGoingUp) {
                if (currY < prevY) {
                    newList.add(list.get(i-1));
                    isGoingUp = false;
                    continue;
                }
            }
            else
            if(currY>prevY) {
                newList.add(list.get(i));
                isGoingUp = true;
                continue;
            }

            if(i % precision == 0)
                newList.add(list.get(i));
        }

        Log.i("fragmentRoll","original: "+ list.size()+", modified: "+ newList.size());

        return newList;
    }
}
