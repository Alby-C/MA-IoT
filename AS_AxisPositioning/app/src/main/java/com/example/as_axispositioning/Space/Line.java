package com.example.as_axispositioning.Space;

public class Line {
    public final Vector dirVector;
    public final Point startPoint;

    /**
     * Inizializza una retta.
     * @param directionalVector Il vettore direzionale della retta.
     * @param startingPoint Un punto per cui passa la retta.
     */
    public Line(Vector directionalVector, Point startingPoint){
        if(directionalVector.isEqual(Vector.ZERO_VECTOR))
            throw new IllegalArgumentException("Cannot create a line with null directional vector");
        this.dirVector = directionalVector.toVersor();
        this.startPoint = startingPoint;
    }

    public String toString(){
        return startPoint.toString() + " + t" + dirVector.toString();
    }
}
