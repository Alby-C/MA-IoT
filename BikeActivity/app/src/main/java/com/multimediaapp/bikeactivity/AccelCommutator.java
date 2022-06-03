package com.multimediaapp.bikeactivity;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;

import Space.ReferenceSystemCommutator;
import Space.Vector;

public class AccelCommutator extends BaseSensor<IAccelListener> implements IAccelListener {

    private ReferenceSystemCommutator rfCommutator;

    public AccelCommutator(ReferenceSystemCommutator rfCommutator){
        this.rfCommutator = rfCommutator;
    }

    @Override
    public void onChangeAccel(long timestamp, float[] newValues) {
        Vector commutedVector = rfCommutator.ConvertToNewReferenceSystem(new Vector(newValues[0], newValues[1], newValues[2]));

        for (IAccelListener listener :
                listeners) {
            listener.onChangeAccel(timestamp, commutedVector.toArray());
        }
    }

    @Override
    public void Start() {

    }

    @Override
    public void Stop() {

    }
}
