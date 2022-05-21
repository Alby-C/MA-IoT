package com.example.as_axispositioning.Space;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.sqrt;
import static Miscellaneous.MiscellaneousOperations.Truncate;

import android.util.Log;

/**
 * Implementa operazioni per lavorare in uno Spazio Cartesiano.
 */
public class CartesianSpaceOperations {
    private static final String TAG = "CartesianSpaceOperations";

    private static final double PI_OVER2 = PI / 2.;

    /**
     * Determina, in base all'angolo che gli viene passato, in che semispazio si trova il vettore,
     * se quello positivo o negativo.
     * @param angle L'angolo misurato tra il vettore e l'asse del sistema di riferimento ortogonale
     *              al piano che divide in due lo spazio.
     * @return 1.0 se si trova nel semispazio non negativo, -1.0 se si trova in quello negativo.
     */
    public static double whichHalfSpace(double angle){
        if(angle <= PI_OVER2)
            return 1.;
        else
            return -1.;
    }
    /**
     * Calcola l'angolo tra due vettori.
     * @param v1 Il primo vettore.
     * @param v2 Il secondo vettore.
     * @return L'angolo tra v1 e v2 in radianti, nell'intervallo [0, pi].
     */
    public static double AngleBetween(Vector v1, Vector v2){
        /*            /  v1 · v2 \
            a = acos | ---------- |
                      \ |v1|·|v2|/
        */
        return acos((DotProduct(v1, v2))/(v1.getModule() * v2.getModule()));
    }

    /**
     * Calcola la distanza tra due punti.
     * @param p1 Il primo punto.
     * @param p2 Il secondo punto.
     * @return La distanza tra i due punti.
     */
    public static double Distance(Point p1, Point p2){
        double x = p1.X - p2.X;
        double y = p1.Y - p2.Y;
        double z = p1.Z - p2.Z;

        return sqrt(x * x + y * y + z * z);
    }
    /**
     * Calcola la distanza di un punto da una retta.
     * @param p Il punto dal dove si vuole calcolare la distanza.
     * @param l La retta dalla quale si vuole calcolare la distanza.
     * @return La distanza tra punto e retta.
     */
    public static double Distance(Point p, Line l){
        //d = |(P - puntoSuRetta) vettor (l, m, n)| / sqrt(l^2 + m^2 + n^2)
        return CrossProduct( new Vector( p, l.startPoint ), l.dirVector ).getModule()
            / sqrt(l.dirVector.X * l.dirVector.X + l.dirVector.Y * l.dirVector.Y + l.dirVector.Z * l.dirVector.Z);
    }

    /**
     * Calcola la retta perpendicolare ad un'altra retta passante per un punto.
     * @param l La retta della quale sarà perpendicolare.
     * @param p Il punto attraverso il quale passerà la retta.
     * @return La retta passante per il punto perpendicolare all'altra retta.
     */
    public static Line PerpendicularToLine(Line l, Point p){
        // l.startPoint = (x0, y0, z0)
        //  l.dirVector = (l,m,n)
        //            p = (a,b,c)
        double t = ((-l.startPoint.X + p.X) * l.dirVector.X +   //((-x0 + a) * l +
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
     * Calcola la retta passante per un punto,perpendicolare ad un piano.
     * @param plane Il piano a cui la retta è perpendicolare.
     * @param point Il punto attraverso il quale passa la retta.
     * @return Restituisce la retta perpendicolare al piano passante per il punto.
     */
    public static Line PerpendicularToPlane(Plane plane,Point point){
        return new Line(plane.normalVector, point);
    }

    /**
     * Calcola un piano date due rette, passante per il punto iniziale della prima retta.
     * @param l1 La prima retta.
     * @param l2 La seconda retta.
     * @return Restituisce il piano, o null nel caso in cui le due rette siano parallele o sghembe.
     */
    public static Plane PlaneFromTwoLines(Line l1, Line l2){
        Vector normalVector = CrossProduct(l1.dirVector,l2.dirVector);
        if(normalVector.isEqual(Vector.ZERO_VECTOR)){
            Log.i(TAG, "The two lines are parallel.");
            return  null;
        }
        if(areLinesCrooked(l1, l2)) {   //Se le due rette sono parallele o sghembe
            Log.i(TAG, "The two lines are crooked");
            return null;
        }

        return new Plane(normalVector, l1.startPoint);
    }

    /**
     * Calcola il prodotto vettoriale di due vettori.
     * @param v1 Il primo vettore.
     * @param v2 Il secondo vettore.
     * @return Il vettore prodotto vettoriale.
     */
    public static Vector CrossProduct(Vector v1, Vector v2){
        return new Vector(v1.Y * v2.Z - v1.Z * v2.Y,
                          - v1.X * v2.Z + v1.Z * v2.X,
                          v1.X * v2.Y - v1.Y * v2.X);
    }

    /**
     * Calcola il prodotto scalare di due vettori.
     * @param v1 Il primo vettore.
     * @param v2 Il secondo vettore.
     * @return Il risultato del prodotto scalare.
     */
    public static double DotProduct(Vector v1, Vector v2){
        double v = v1.X * v2.X + v1.Y * v2.Y + v1.Z * v2.Z;
        Log.i(TAG, "" + v + "\n" + Truncate(v) );
        return v;
    }

    /**
     * Determina se due rette sono sghembe, valutando il determinante della matrice:
     * / x1-x2, b1-b2, c1-c2 \
     *|   l1 ,   m1 ,   n1   |
     *\   l2 ,   m2 ,   n2  /
     * @param l1 La prima retta.
     * @param l2 La seconda retta.
     * @return Restituisce true se sono sghembe, altrimenti false.
     */
    public static boolean areLinesCrooked(Line l1, Line l2){
        Log.i(TAG, "("+l1.startPoint.X+ "-" +l2.startPoint.X+") *"+
                "(" +l1.dirVector.Y +"*" +l2.dirVector.Z +"-"+ l1.dirVector.Z +"*"+ l2.dirVector.Y+") -"+
                "("+l1.startPoint.Y+ "-"+ l2.startPoint.Y+") *"+
                "("+l1.dirVector.X+ "*"+ l2.dirVector.Z+ "-"+ l1.dirVector.Z+ "* "+l2.dirVector.X+") +"+
                "("+l1.startPoint.X+ "-"+ l2.startPoint.X+") *"+
                "("+l1.dirVector.X+ "*"+ l2.dirVector.Y+ "-"+ l1.dirVector.Y+ "*"+ l2.dirVector.Y+")");
        return 0 != (l1.startPoint.X - l2.startPoint.X) *
                    (l1.dirVector.Y * l2.dirVector.Z - l1.dirVector.Z * l2.dirVector.Y) -
                    (l1.startPoint.Y - l2.startPoint.Y) *
                    (l1.dirVector.X * l2.dirVector.Z - l1.dirVector.Z * l2.dirVector.X) +
                    (l1.startPoint.X - l2.startPoint.X) *
                    (l1.dirVector.X * l2.dirVector.Y - l1.dirVector.Y * l2.dirVector.Y);
    }
}
