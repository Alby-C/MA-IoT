package com.example.as_axispositioning.Space;

/**
 * Rappresentazione di un punto nello spazio cartesiano.
 */
public class Point {
    public final double X;
    public final double Y;
    public final double Z;

    /**
     * Inizializza un punto.
     * @param X Ascissa.
     * @param Y Ordinata.
     * @param Z Quota.
     */
    public Point(double X, double Y, double Z){
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    /**
     * Inizializza un punto come spostamento dall'origine.
     * @param v Vettore spostamento.
     */
    public Point(Vector v){
        this.X = v.X;
        this.Y = v.Y;
        this.Z = v.Z;
    }

    /**
     * Inizializza un punto come spostamento da un altro punto.
     * @param v Vettore spostamento.
     * @param p Punto di partenza.
     */
    public Point(Vector v, Point p){
        this.X = v.X + p.X;
        this.Y = v.Y + p.Y;
        this.Z = v.Z + p.Z;
    }

    public String toString(){
        return "(" + this.X + ", " + this.Y + ", " + this.Z + ")";
    }
}
