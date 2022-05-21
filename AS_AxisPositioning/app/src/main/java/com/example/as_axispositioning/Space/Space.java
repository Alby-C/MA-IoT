package com.example.as_axispositioning.Space;

import static com.example.as_axispositioning.Space.CartesianSpaceOperations.DotProduct;

import static Miscellaneous.MiscellaneousOperations.Truncate;

import android.util.Log;

/**
 * Rappresenta un sistema di riferimento nello spazio cartesiano.
 */
public class Space {
    private static final String TAG = "Space";

    public final Vector xAxis;
    public final Vector yAxis;
    public final Vector zAxis;
    public final Point origin;

    /**
     * Inizializza uno spazio canonico.
     */
    public Space(){
        this.xAxis = new Vector(1, 0, 0);
        this.yAxis = new Vector(0, 1, 0);
        this.zAxis = new Vector(0, 0, 1);
        this.origin = new Point(0, 0, 0);
    };

    /**
     * Inizializza uno spazio centrato nell'origine.
     * @param xAxis
     * @param yAxis
     * @param zAxis
     */
    public Space(Vector xAxis, Vector yAxis, Vector zAxis){
        if(DotProduct(xAxis, yAxis) != 0 || DotProduct(yAxis, zAxis) != 0 || DotProduct(zAxis,xAxis) != 0)  //se non sono ortogonali i vettori
            throw new IllegalArgumentException("Cannot create a space with non ortogonal axises");
        if(xAxis.isEqual(Vector.ZERO_VECTOR) || yAxis.isEqual(Vector.ZERO_VECTOR) || zAxis.isEqual(Vector.ZERO_VECTOR))  //se uno dei vettori è il vettore nullo
            throw new IllegalArgumentException("Cannot create a space with a null axis");

        this.xAxis = xAxis.toVersor();
        this.yAxis = yAxis.toVersor();
        this.zAxis = zAxis.toVersor();

        this.origin = new Point(0, 0, 0);
    }

    /**
     * Inizializza uno spazio arbitrario.
     * @param xAxis
     * @param yAxis
     * @param zAxis
     * @param origin
     */
    public Space(Vector xAxis, Vector yAxis, Vector zAxis, Point origin){
        Log.i(TAG, "xAxis: " + xAxis.toString() + "\nyAxis: "+ yAxis.toString() + "\nzAxis: "+zAxis.toString() );

        if( Truncate(DotProduct(xAxis, yAxis)) != 0 || Truncate(DotProduct(yAxis, zAxis)) != 0 || Truncate(DotProduct(zAxis,xAxis)) != 0)  //se non sono ortogonali i vettori
            throw new IllegalArgumentException("Cannot create a space with non ortogonal axises");
        if(xAxis.isEqual(Vector.ZERO_VECTOR) || yAxis.isEqual(Vector.ZERO_VECTOR) || zAxis.isEqual(Vector.ZERO_VECTOR))  //se uno dei vettori è il vettore nullo
            throw new IllegalArgumentException("Cannot create a space with a null axis");

        this.xAxis = xAxis.toVersor();
        this.yAxis = yAxis.toVersor();
        this.zAxis = zAxis.toVersor();

        this.origin = origin;
    }

    public String toString(){
        return "x:" + xAxis.toString() + ", y:" + yAxis.toString() + ", z:" + zAxis + ", O:" + origin.toString();
    }
}
