package com.multimediaapp.bikeactivity.Commutators;

import com.multimediaapp.bikeactivity.BaseClasses.BaseSensor;
import com.multimediaapp.bikeactivity.Interfaces.IGyroListener;

import Space.ReferenceSystemCommutator;
import Space.Vector;

/**
 * Manages the extraction of the components of a angular velocity vector in a determined reference system,
 * starting by his components in another reference system.
 */
public class GyroCommutator extends BaseSensor<IGyroListener> implements IGyroListener {

    private final ReferenceSystemCommutator rfCommutator;

    public GyroCommutator(ReferenceSystemCommutator rfCommutator){
        this.rfCommutator = rfCommutator;
    }

    @Override
    public void onChangeGyro(long timestamp, float[] newValues) {
        Vector commutedVector = rfCommutator.ConvertToNewReferenceSystem(new Vector(newValues[0], newValues[1], newValues[2]));

        for (IGyroListener listener :
                listeners) {
            listener.onChangeGyro(timestamp, commutedVector.toArray());
        }
    }

    @Override
    public void Start() {

    }

    @Override
    public void Stop() {

    }


}
