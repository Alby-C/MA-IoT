package com.multimediaapp.bikeactivity.Commutators;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.ILinearAccelListener;

import Space.ReferenceSystemCommutator;
import Space.Vector;

/**
 * Manages the extraction of the components of a linear acceleration vector in a determined reference system,
 * starting by his components in another reference system.
 */
public class LinearAccelCommutator extends BaseSensor<ILinearAccelListener> implements ILinearAccelListener {

    private final ReferenceSystemCommutator rfCommutator;

    public LinearAccelCommutator(ReferenceSystemCommutator rfCommutator){
        this.rfCommutator = rfCommutator;
    }

    @Override
    public void onChangeLinearAccel(long timestamp, float[] newValues) {
        Vector commutedVector = rfCommutator.ConvertToNewReferenceSystem(new Vector(newValues[0], newValues[1], newValues[2]));

        for (ILinearAccelListener listener :
                listeners) {
            listener.onChangeLinearAccel(timestamp, commutedVector.toArray());
        }
    }

    @Override
    public void Start() {

    }

    @Override
    public void Stop() {

    }
}
