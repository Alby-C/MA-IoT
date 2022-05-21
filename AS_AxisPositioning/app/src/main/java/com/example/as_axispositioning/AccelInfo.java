package com.example.as_axispositioning;

import java.sql.Struct;
import java.time.LocalTime;

public class AccelInfo {
    private LocalTime sampleTime;   //orario nel quale è stato preso il campione

    private double X;
    private double Y;
    private double Z;

    public AccelInfo(LocalTime sampleTime, double X, double Y, double Z){
        this.sampleTime = sampleTime;
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    /**
     * Calcola il modulo del vettore accelerazione dell'istanza corrente.
     * @return il modulo del vettore nello spazio.
     */
    public double GetModule(){
        return Math.sqrt(X * X + Y * Y + Z * Z);
    }

    /**
     * Calcola il modulo del vettore accelerazione dell'istanza corrente, nel piano XY.
     * @return il modulo del vettore nel piano XY.
     */
    public double GetXYModule(){
        return Math.hypot(X, Y);
    }

    /**
     * Calcola il modulo del vettore accelerazione dell'istanza corrente, nel piano XZ.
     * @return il modulo del vettore nel piano XZ.
     */
    public double GetXZModule(){
        return Math.hypot(X, Z);
    }

    /**
     * Calcola il modulo del vettore accelerazione dell'istanza corrente, nel piano YZ.
     * @return il modulo del vettore nel piano YZ.
     */
    public double GetYZModule(){
        return Math.hypot(Y, Z);
    }

    /**
     * Restituisce la direzione di un vettore su un piano rispetto ad uno degli assi specificati.
     * @param axisComponent La componente del vettore di cui si vuole calcolare la direzione lungo l'asse
     *                      rispetto al quale si vuole calcolare la direzione.
     * @param plainModule Il modulo del vettore sul piano nel quale si vuole calcolare la direzione
     *                    del vettore.
     * @return L'angolo compreso tra il vettore sul piano e l'asse specifiato
     */
    public static double GetDirection(double axisComponent, double plainModule){
        return Math.acos(axisComponent / plainModule);
    }

    public double[] ToVector(){
        return new double[]{ X, Y, Z };
    }
}
