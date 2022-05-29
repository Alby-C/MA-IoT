package com.multimediaapp.bikeactivity.Interfaces;

public interface IMeasurementHandler
{
  /**
   * method to handle the variation of speed
   * @param newSpeed  Current speed detected
   * @param avgSpeed  Average speed
   */
  void onChangeSpeed(float newSpeed, float avgSpeed);

  /**
   * method to handle the variation of roll
   * @param currentRoll Current roll detected
   */
  void onChangeRoll(float currentRoll);
  void onChangeAcc(float acc);
}
