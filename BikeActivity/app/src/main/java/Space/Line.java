package Space;

import androidx.annotation.NonNull;

/**
 * Represents a line in Cartesian space.
 */
public class Line {
    public final Vector dirVector;
    public final Point startPoint;

    /**
     * Initialize a line.
     * @param directionalVector The directional vector of the line.
     * @param startingPoint A point through which the line passes.
     */
    public Line(Vector directionalVector, Point startingPoint){
        if(directionalVector.isEqual(Vector.ZERO_VECTOR))
            throw new IllegalArgumentException("Cannot create a line with null directional vector");
        this.dirVector = directionalVector.toUnitVector();
        this.startPoint = startingPoint;
    }

    @NonNull
    public String toString(){
        return startPoint.toString() + " + t" + dirVector.toString();
    }
}
