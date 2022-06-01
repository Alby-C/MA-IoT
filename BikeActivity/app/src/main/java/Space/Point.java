package Space;

import androidx.annotation.NonNull;

/**
 * Representation of a point in Cartesian space.
 */
public class Point {
    public final double X;
    public final double Y;
    public final double Z;

    /**
     * Initialize a point.
     * @param X Abscissa.
     * @param Y Ordinate.
     * @param Z Height.
     */
    public Point(double X, double Y, double Z){
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
