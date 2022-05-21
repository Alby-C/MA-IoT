package com.example.as_axispositioning.Space;

/**
 * Rappresenta un piano nello spazio cartesiano.
 */
public class Plane {
    public final Vector normalVector;
    public final Point startPoint;

    /**
     * Inizializza un piano.
     * @param normalVector  Vettore normale allo spazio.
     * @param startingPoint Punto per cui passa lo spazio.
     */
    public Plane(Vector normalVector, Point startingPoint){
        if(normalVector.isEqual(Vector.ZERO_VECTOR))
            throw new IllegalArgumentException("Cannot create a plane with null directional vector");
        this.normalVector = normalVector.toVersor();
        this.startPoint = startingPoint;
    }

    public String toString(){
        return "P:" + startPoint.toString() + ", n:" + normalVector.toString();
    }
}
