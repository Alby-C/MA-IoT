package com.multimediaapp.bikeactivity.Interfaces;

/**
 * Interface for a class that wants to get updates from the linear accelerometer, jump, roll and speed sensors.
 */
public interface IMeasurementHandler extends ILinearAccelListener, IJumpListener, IRollListener, ISpeedListener {

}
