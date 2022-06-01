package com.multimediaapp.bikeactivity.Interfaces;

public interface IMeasurementHandler{
  /**
   * Method to handle the variation of speed.
   * @param newSpeed  Current speed detected
   * @param avgSpeed  Average speed
   */
  void onChangeSpeed(float newSpeed, float avgSpeed);

  /**
   * Method to handle the variation of roll.
   * @param currentRoll Current roll detected
   */
  void onChangeRoll(float currentRoll);

  /**
   * Method to handle the jump event.
   * @param flightTime The flight time of the jump.
   */
  void onJumpHappened(long flightTime);
}
