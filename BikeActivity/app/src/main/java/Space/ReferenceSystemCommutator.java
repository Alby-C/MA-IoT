package Space;

import static Space.CartesianSpaceOperations.AngleBetween;
import static Space.CartesianSpaceOperations.Distance;
import static Space.CartesianSpaceOperations.PerpendicularToLine;
import static Space.CartesianSpaceOperations.PerpendicularToPlane;
import static Space.CartesianSpaceOperations.PlaneFromTwoLines;
import static Space.CartesianSpaceOperations.whichHalfSpace;

/**
 * Class that deals with passing from one vector space to another,
 * with two different reference systems.
 */
public class ReferenceSystemCommutator {

    private final String TAG = ReferenceSystemCommutator.class.getSimpleName();

    /**
     * Initial reference vector, on which the new reference system will be based.
     */
    private final Vector initialVector;

    private final Space startingSpace;  //Initial reference system
    private final Space destinationSpace;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Initializes a new instance of the class, setting the reference vector
     * of the initial reference system.
     * @param initialVector The initial reference vector.
     */
    public ReferenceSystemCommutator(Vector initialVector){
        this.initialVector = initialVector;
        this.startingSpace = new Space();

        Line xAxis = PerpendicularToPlane(PlaneFromTwoLines(startingSpace.yAxis.toLine(startingSpace.origin), initialVector.toLine(startingSpace.origin)),
                                            startingSpace.origin);
        Line yAxis = PerpendicularToPlane(PlaneFromTwoLines(initialVector.toLine(startingSpace.origin), xAxis),
                                            startingSpace.origin);

        this.destinationSpace = new Space(xAxis.dirVector, yAxis.dirVector, initialVector, startingSpace.origin);

    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Converts a vector measured in the previous reference system to a vector in the new reference system.
     * @param vector The vector measured in the old reference system.
     * @return The same vector, which components are relative to the new reference system.
     */
    public Vector ConvertToNewReferenceSystem(Vector vector) {
        Point p = new Point(vector, destinationSpace.origin);

        //The new vector is calculated by taking the components of the same vector along the Cartesian axes of the destinationSpace
        return new Vector(
                whichHalfSpace(AngleBetween(vector,destinationSpace.xAxis)) *
                        Distance(destinationSpace.origin, PerpendicularToLine(destinationSpace.xAxis.toLine(destinationSpace.origin), p)),
                whichHalfSpace(AngleBetween(vector,destinationSpace.yAxis)) *
                        Distance(destinationSpace.origin, PerpendicularToLine(destinationSpace.yAxis.toLine(destinationSpace.origin), p)),
                whichHalfSpace(AngleBetween(vector,destinationSpace.zAxis)) *
                        Distance(destinationSpace.origin, PerpendicularToLine(destinationSpace.zAxis.toLine(destinationSpace.origin), p))
        );
    }
}
