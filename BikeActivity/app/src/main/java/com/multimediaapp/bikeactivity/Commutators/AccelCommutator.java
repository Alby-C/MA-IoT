package com.multimediaapp.bikeactivity.Commutators;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;

import Space.ReferenceSystemCommutator;
import Space.Vector;

/**
 * Manages the extraction of the components of an acceleration vector in a determined reference system,
 * starting by his components in another reference system.
 */
public class AccelCommutator extends BaseSensor<IAccelListener> implements IAccelListener {

    private final ReferenceSystemCommutator rfCommutator;

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
