package Space;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.sqrt;

/**
 * Implement operations to work in a Cartesian Space.
 */
public class CartesianSpaceOperations {
    private final String TAG = CartesianSpaceOperations.class.getSimpleName();

    private static final float PI_OVER2 = (float)(PI / 2.);

    /**
     * Determines, on the basis of the angle passed to it, in which half-space
     * the vector is located, whether the positive or negative one.
     * @param angle The angle measured between the vector and the axis of the
     *              reference system orthogonal to the plane that divides the space in two.
     * @return 1.0 if it is in the non-negative half-space, -1.0 if it is in the negative half.
     */
    public static float whichHalfSpace(float angle){
        if(angle <= PI_OVER2)
            return 1.f;
        else
            return -1.f;
    }
    /**
     * Calculate the angle between two vectors.
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return The angle between v1 and v2 in radians, in the range [0, pi].
     */
    public static float AngleBetween(Vector v1, Vector v2){
        /*            /  v1 · v2 \
            a = acos | ---------- |
                      \ |v1|·|v2|/
        */
        return (float) acos((DotProduct(v1, v2))/(v1.getModule() * v2.getModule()));
    }

    /**
     * Calculate the distance between two points.
     * @param p1 The first point.
     * @param p2 The second point.
     * @return The distance between the two points.
     */
    public static float Distance(Point p1, Point p2){
        float x = p1.X - p2.X;
        float y = p1.Y - p2.Y;
        float z = p1.Z - p2.Z;

        return (float) sqrt(x * x + y * y + z * z);
    }
    /**
     * Calculates the distance of a point from a line.
     * @param p The point from where you want to calculate the distance.
     * @param l The line from which you want to calculate the distance.
     * @return The distance between point and line.
     */
    public static float Distance(Point p, Line l){
        //d = |(P - pointOnLine)x(l, m, n)| / sqrt(l^2 + m^2 + n^2)
        return (float) (CrossProduct( new Vector( p, l.startPoint ), l.dirVector ).getModule()
            / sqrt(l.dirVector.X * l.dirVector.X + l.dirVector.Y * l.dirVector.Y + l.dirVector.Z * l.dirVector.Z));
    }

    /**
     * Calculates the line perpendicular to another line passing through a point.
     * @param l The line of which will be perpendicular.
     * @param p The point through which the line will pass.
     * @return The line passing through the point perpendicular to the other line.
     */
    public static Line PerpendicularToLine(Line l, Point p){
        // l.startPoint = (x0, y0, z0)
        //  l.dirVector = (l,m,n)
        //            p = (a,b,c)
        float t = ((-l.startPoint.X + p.X) * l.dirVector.X +   //((-x0 + a) * l +
                    (-l.startPoint.Y + p.Y) * l.dirVector.Y +   // (-y0 + b) * m +
                    (-l.startPoint.Z + p.Z) * l.dirVector.Z)    // (-z0 + c) * n)
                    /(l.dirVector.X * l.dirVector.X +           //(l^2 +
                      l.dirVector.Y * l.dirVector.Y +           // m^2 +
                      l.dirVector.Z * l.dirVector.Z);           // n^2)

        return new Line(new Vector(l.startPoint.X + l.dirVector.X * t - p.X,    //(x0 + lt - a,
                                   l.startPoint.Y + l.dirVector.Y * t - p.Y,    // y0 + mt - b,
                                   l.startPoint.Z + l.dirVector.Z * t - p.Z)    // z0 + nt -c)
                        , p);
    }

    /**
     * Calculates the line passing through a point, perpendicular to a plane.
     * @param plane The plane to which the line is perpendicular.
     * @param point The point through which the line passes.
     * @return Returns the line perpendicular to the plane passing through the point.
     */
    public static Line PerpendicularToPlane(Plane plane, Point point){
        return new Line(plane.normalVector, point);
    }

    /**
     * Calculates a plane given two lines, passing through the starting point of the first line.
     * @param l1 The first line.
     * @param l2 The second line.
     * @return Returns the plane, or null if the two lines are parallel or crooked.
     */
    public static Plane PlaneFromTwoLines(Line l1, Line l2){
        Vector normalVector = CrossProduct(l1.dirVector,l2.dirVector);
        if(normalVector.isEqual(Vector.ZERO_VECTOR)){
            //Log.i(TAG, "The two lines are parallel.");
            return  null;
        }
        if(areLinesCrooked(l1, l2)) {   //If the two lines are parallel or crooked
            return null;
        }

        return new Plane(normalVector, l1.startPoint);
    }

    /**
     * Calculate the cross product of two vectors.
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return The cross product vector.
     */
    public static Vector CrossProduct(Vector v1, Vector v2){
        return new Vector(v1.Y * v2.Z - v1.Z * v2.Y,
                          - v1.X * v2.Z + v1.Z * v2.X,
                          v1.X * v2.Y - v1.Y * v2.X);
    }

    /**
     * Calculate the dot product of two vectors.
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return The result of the dot product.
     */
    public static float DotProduct(Vector v1, Vector v2){
        return v1.X * v2.X + v1.Y * v2.Y + v1.Z * v2.Z;
    }

    /**
     * Determines if two straight lines are crooked, evaluating the determinant of the matrix:
     * / x1-x2, b1-b2, c1-c2 \
     *|   l1 ,   m1 ,   n1   |
     *\   l2 ,   m2 ,   n2  /
     * @param l1 The first line.
     * @param l2 The second line.
     * @return Returns true if they are crooked, false otherwise.
     */
    public static boolean areLinesCrooked(Line l1, Line l2){
        return 0 != (l1.startPoint.X - l2.startPoint.X) *
                    (l1.dirVector.Y * l2.dirVector.Z - l1.dirVector.Z * l2.dirVector.Y) -
                    (l1.startPoint.Y - l2.startPoint.Y) *
                    (l1.dirVector.X * l2.dirVector.Z - l1.dirVector.Z * l2.dirVector.X) +
                    (l1.startPoint.X - l2.startPoint.X) *
                    (l1.dirVector.X * l2.dirVector.Y - l1.dirVector.Y * l2.dirVector.Y);
    }
}
