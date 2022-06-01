package com.multimediaapp.bikeactivity.Accelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.multimediaapp.bikeactivity.Interfaces.IAccelListener;
import com.multimediaapp.bikeactivity.Interfaces.ISensorManager;
import com.multimediaapp.bikeactivity.MainActivity;

import java.util.ArrayList;

public class Accelerometer implements SensorEventListener, ISensorManager<IAccelListener>
{
    private final String TAG = Accelerometer.class.getSimpleName();

    /// Array of listeners to the new values of the gyroscope
    private final ArrayList<IAccelListener> listeners = new ArrayList<>();
    /// True if the sensor is getting
    private boolean isRunning = false;
    /// this flag if no listeners are subscribed, is set to true in the start method
    private boolean requestToStart = false;

    private final Sensor acc;
    private final SensorManager accManager;
    private final SensorEventListener accListener;

    public Accelerometer(Sensor acc, SensorManager accManager)
    {
        this.acc = acc;
        this.accManager = accManager;
        this.accListener = this;
    }

    @Override
    public void Start() {
        if(listeners.size() == 0)
            requestToStart = true;
        else {
            accManager.registerListener(accListener, acc, SensorManager.SENSOR_DELAY_NORMAL);
            isRunning = true;
        }
    }

    @Override
    public void Stop() {
        if(!isRunning)
            accManager.unregisterListener(accListener);

        requestToStart = false;
    }

    @Override
    public void SubscribeListener(IAccelListener listener) {
        if(!this.listeners.contains(listener))
            this.listeners.add(listener);

        if(requestToStart){
            Start();
            requestToStart = false;
        }
    }

    @Override
    public void UnsubscribeListener(IAccelListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        for (IAccelListener listener :
                listeners) {
            listener.onChangeAccel(event.timestamp, event.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

}
