package Space;

import androidx.annotation.NonNull;

/**
 * Represents a vector in Cartesian space.
 */
public class Vector {
     //The null vector
    public static final Vector ZERO_VECTOR = new Vector(0,0,0);

    public final float X;
    public final float Y;
    public final float Z;

    /**
     * Initialize a vector.
     * @param X Abscissa.
     * @param Y Ordinate.
     * @param Z Height.
     */
    public Vector(float X, float Y, float Z){
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    /**
     * Creates a vector as a difference of two points.
     * @param p1 Point of arrival, vector head.
     * @param p2 Starting point, carrier tail.
     */
    public Vector(Point p1, Point p2){
        this.X = p1.X - p2.X;
        this.Y = p1.Y - p2.Y;
        this.Z = p1.Z - p2.Z;
    }

    /**
     * Creates a vector given direction, direction and a module.
     * @param module The module of the new vector.
     * @param unitVector The unit vector that establishes the direction
     *              (if it is not a unit vector it will still be normalized).
     */
    public Vector(float module, Vector unitVector){
        if(unitVector.getModule() != 1)
            unitVector = unitVector.toUnitVector();

        this.X = unitVector.X * module;
        this.Y = unitVector.Y * module;
        this.Z = unitVector.Z * module;
    }

    /**
     * Calculates the modulus of the current vector instance.
     * @return The instance module.
     */
    public float getModule() {
        return (float)Math.sqrt(X * X + Y * Y + Z * Z);
    }

    /**
     * Returns the unit vector of the current instance.
     * @return The unit vector of the current vector.
     */
    public Vector toUnitVector(){
        return new Vector(
                this.X / this.getModule(),
                this.Y / this.getModule(),
                this.Z / this.getModule()
        );
    }

    /**
     * Generate a line with this instance as the direction vector, and the point p as the point over which it passes.
     * @param p Point on which the line passes.
     * @return The generated line.
     */
    public Line toLine(Point p){
        return new Line(this, p);
    }

    /**
     * Returns the current instance as an array:
     * [0] = X;
     * [1] = Y;
     * [2] = Z.
     * @return The current instance as an array.
     */
    public float[] toArray(){
        return new float[]{this.X, this.Y, this.Z};
    }

    /**
     * Determines whether the current instance is equivalent to the inserted one (same direction and verse).
     * @param v The vector to compare.
     * @return true if they are equivalent, false if they are not.
     */
    public boolean isEquivalent(Vector v){
        return this.toUnitVector() == v.toUnitVector();
    }

    /**
     * Determines whether the current instance is the same as the inserted one.
     * @param v The vector to compare.
     * @return true if they are equal, false if they are not.
     */
    public boolean isEqual(Vector v){
        return this.X == v.X && this.Y == v.Y && this.Z == v.Z;
    }

    @NonNull
    public String toString(){
        return "("+this.X+", "+this.Y+", "+this.Z+")";
    }
}
