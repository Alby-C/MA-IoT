package Space;

import androidx.annotation.NonNull;

/**
 * Represents a plane in a Cartesian space.
 */
public class Plane {
    public final Vector normalVector;
    public final Point startPoint;

    /**
     * Initialize a plan.
     * @param normalVector Plane normal vector.
     * @param startingPoint Point the plane passes through.
     */
    public Plane(Vector normalVector, Point startingPoint){
        if(normalVector.isEqual(Vector.ZERO_VECTOR))
            throw new IllegalArgumentException("Cannot create a plane with null directional vector");
        this.normalVector = normalVector.toUnitVector();
        this.startPoint = startingPoint;
    }

    @NonNull
    public String toString(){
        return "P:" + startPoint.toString() + ", n:" + normalVector.toString();
    }
}
