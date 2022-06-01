package Space;

import static Space.CartesianSpaceOperations.DotProduct;
import static Miscellaneous.MiscellaneousOperations.Truncate;

import androidx.annotation.NonNull;

/**
 * Represents a reference system in Cartesian space.
 */
public class Space {
    private final String TAG = Space.class.getSimpleName();

    public final Vector xAxis;
    public final Vector yAxis;
    public final Vector zAxis;
    public final Point origin;

    /**
     * Initialize a canonical space.
     */
    public Space(){
        this.xAxis = new Vector(1, 0, 0);
        this.yAxis = new Vector(0, 1, 0);
        this.zAxis = new Vector(0, 0, 1);
        this.origin = new Point(0, 0, 0);
    }

    /**
     * Initializes a space centered at the origin.
     */
    public Space(Vector xAxis, Vector yAxis, Vector zAxis){
        if(DotProduct(xAxis, yAxis) != 0 || DotProduct(yAxis, zAxis) != 0 || DotProduct(zAxis,xAxis) != 0)  //If the vectors are not orthogonal
            throw new IllegalArgumentException("Cannot create a space with non-orthogonal axes.");
        if(xAxis.isEqual(Vector.ZERO_VECTOR) || yAxis.isEqual(Vector.ZERO_VECTOR) || zAxis.isEqual(Vector.ZERO_VECTOR))  //If one of the vectors is the null vector
            throw new IllegalArgumentException("Cannot create a space with a null axes.");

        this.xAxis = xAxis.toUnitVector();
        this.yAxis = yAxis.toUnitVector();
        this.zAxis = zAxis.toUnitVector();

        this.origin = new Point(0, 0, 0);
    }

    /**
     * Initialize an arbitrary space.
     */
    public Space(Vector xAxis, Vector yAxis, Vector zAxis, Point origin){
        if(Truncate(DotProduct(xAxis, yAxis)) != 0 || Truncate(DotProduct(yAxis, zAxis)) != 0 || Truncate(DotProduct(zAxis,xAxis)) != 0)  //If the vectors are not orthogonal
            throw new IllegalArgumentException("Cannot create a space with non-orthogonal axes");
        if(xAxis.isEqual(Vector.ZERO_VECTOR) || yAxis.isEqual(Vector.ZERO_VECTOR) || zAxis.isEqual(Vector.ZERO_VECTOR))  //If one of the vectors is the null vector
            throw new IllegalArgumentException("Cannot create a space with a null axis.");

        this.xAxis = xAxis.toUnitVector();
        this.yAxis = yAxis.toUnitVector();
        this.zAxis = zAxis.toUnitVector();

        this.origin = origin;
    }

    @NonNull
    public String toString(){
        return "x:" + xAxis.toString() + ", y:" + yAxis.toString() + ", z:" + zAxis + ", O:" + origin.toString();
    }
}
