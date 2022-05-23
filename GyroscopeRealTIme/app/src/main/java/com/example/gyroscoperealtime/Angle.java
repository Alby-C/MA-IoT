package com.example.gyroscoperealtime;


import java.sql.Timestamp;
import java.util.List;

public class Angle {
    public enum axis {X, Y, Z}

    // Angle amplitude in degrees
    public float degrees;
    // Rotation in which the axis
    public axis rotationAxis;

    public Timestamp startingTime;

    public Timestamp endingTime;

    public Angle() {}

    public Angle(float degrees, axis rotationAxis){
        this.degrees = degrees;
        this.rotationAxis = rotationAxis;
    }

    // This method is used to update the agnle amplitude as we get new values. timeDelta is given in seconds
    public float updateAngleAmplitude(float degreeDelta, float timeDelta){
        this.degrees += degreeDelta*timeDelta;
        return degrees;
    }
}
