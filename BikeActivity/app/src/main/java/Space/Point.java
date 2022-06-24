package Space;

import androidx.annotation.NonNull;

/**
 * Represents a point in a Cartesian space.
 */
public class Point {
    public final float X;
    public final float Y;
    public final float Z;

    /**
     * Initialize a point.
     * @param X Abscissa.
     * @param Y Ordinate.
     * @param Z Height.
     */
    public Point(float X, float Y, float Z){
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    /**
     * Initializes a point as a displacement from the origin.
     * @param v Shift vector.
     */
    public Point(Vector v){
        this.X = v.X;
        this.Y = v.Y;
        this.Z = v.Z;
    }

    /**
     * Initializes a point as a move from another point.
     * @param v Shift vector.
     * @param p Starting point.
     */
    public Point(Vector v, Point p){
        this.X = v.X + p.X;
        this.Y = v.Y + p.Y;
        this.Z = v.Z + p.Z;
    }

    @NonNull
    public String toString(){
        return "(" + this.X + ", " + this.Y + ", " + this.Z + ")";
    }
}
