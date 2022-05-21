package com.example.as_axispositioning;

import static com.example.as_axispositioning.Space.CartesianSpaceOperations.AngleBetween;
import static com.example.as_axispositioning.Space.CartesianSpaceOperations.Distance;
import static com.example.as_axispositioning.Space.CartesianSpaceOperations.PerpendicularToLine;
import static com.example.as_axispositioning.Space.CartesianSpaceOperations.PerpendicularToPlane;
import static com.example.as_axispositioning.Space.CartesianSpaceOperations.PlaneFromTwoLines;
import static com.example.as_axispositioning.Space.CartesianSpaceOperations.areLinesCrooked;
import static com.example.as_axispositioning.Space.CartesianSpaceOperations.whichHalfSpace;

import android.util.Log;

import com.example.as_axispositioning.Space.CartesianSpaceOperations;
import com.example.as_axispositioning.Space.Line;
import com.example.as_axispositioning.Space.Plane;
import com.example.as_axispositioning.Space.Point;
import com.example.as_axispositioning.Space.Space;
import com.example.as_axispositioning.Space.Vector;

/**
 * Classe che si occupa di passare da uno spazio vettoriale ad un'altro, con due sistemi di riferimento
 * separati
 */
public class VectorSpace {

    private static final String TAG = "VectorSpace";
    /**
     * Vettore di riferimento iniziale, su cui si baserà il nuovo sistema di riferimento.
     */
    private final Vector initialVector;

    private final Space startingSpace;  //Sistema di riferimento iniziale
    private final Space destinationSpace;

    /*/**
     * Tre angoli di rotazione dei tre piani costituenti lo spazio, per passare dal sistema di riferimento iniziale
     * a quello nuovo. Un numero positivo è una rotazione oraria, uno negativo è una rotazione antioraria.
     * Gli angoli sono nell'intervallo [0,2pi]:
     * [0] = angolo di rotazione del piano xy attorno all'asse z;
     * [1] = angolo di rotazione del piano xz attorno all'asse y;
     * [2] = angolo di rotazione del piano yz attorno all'asse x.
     *
    private double[] changeReferenceSystemAngles;*/

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Inizializza una nuova istanza della classe, impostando le componenti di riferimento del sistema
     * di riferimento iniziale.
     * @param initialVector Le componenti del vettore di riferimento iniziale.
     */
    public VectorSpace(Vector initialVector){
        this.initialVector = initialVector;
        this.startingSpace = new Space();

        Line xAxis = PerpendicularToPlane(PlaneFromTwoLines(startingSpace.yAxis.toLine(startingSpace.origin), initialVector.toLine(startingSpace.origin)),
                                            startingSpace.origin);
        Line yAxis = PerpendicularToPlane(PlaneFromTwoLines(initialVector.toLine(startingSpace.origin), xAxis),
                                            startingSpace.origin);

        this.destinationSpace = new Space(xAxis.dirVector, yAxis.dirVector, initialVector, startingSpace.origin);

        Log.i(TAG, "\nstartingSpace:\n"+startingSpace.toString()+"\ndestinationSpace:\n"+destinationSpace);
        //this.changeReferenceSystemAngles = new double[]{ 0, 0, 0};
    }

    /*/**
     * Inizializza una nuova istanza della classe, impostando le componenti di riferimento del sistema
     * di riferimento iniziale, da' anche la possibilità di inizializzare il vettore di trasformazione
     * verso il nuovo sistema di riferimento.
     * @param initialVector Le componenti del vettore di riferimento iniziale.
     * @param expectedAngles Vedi la descrizione di SetChangeReferenceSystemAngles
     * @throws Exception Lancia un'eccezione se i vettori non ha 3 parametri.
     *
    public VectorSpace(double[] initialVector, double[] expectedAngles) throws Exception {
        if(initialVector.length != 3)
            throw new Exception("Il vettore deve avere 3 componenti");
        if(expectedAngles.length != 3)
            throw new Exception("expectedAngles deve avere 3 componenti");


        //this.initialVector = initialVector;
        SetChangeReferenceSystemAngles(expectedAngles);
    }*/

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*/**
     * Imposta changeReferenceSystemAngles in base alla configurazione fornita come parametro in ingresso.
     * @param expectedAngles Il vettore indica l'angolo descritto dalla proiezione del vettore (initialComponents nel
     *                           nuovo sistema di riferimento) sui vari piani XY, XZ, YZ, i piani hanno angoli
     *                           che vanno da 0 a 2pi. Il valore -1 indica che l'angolo deve rimanere invariato.
     *                           [0] = piano xy, angolo rispetto asse x;
     *                           [1] = piano xz, angolo rispetto asse x;
     *                           [2] = piano yz, angolo rispetto asse y.
     * @throws Exception Lancia un'eccezione se il vettore passato in ingresso ha meno di 3 componenti.
     * Lancia un'eccezione se i parametri in expectedAngles sono esterni all'intervallo [0,2pi]
     * e diverso da -1
     *
    public void SetChangeReferenceSystemAngles(double[] expectedAngles) throws Exception {
        if(expectedAngles.length != 3)
            throw new Exception("Il vettore deve avere 3 componenti");

        for (double i:expectedAngles)
            if(i > 2 * Math.PI || i < 0 && i != -1)
                throw new Exception("I parametri in expected components devono essere compresi tra 0 e 2pi");

        double[] originalAngles = new double[]{
                Math.acos(initialVector[0]/GetModule(ModuleTypes.XY)),  //piano xy, angolo rispetto ad x
                Math.acos(initialVector[0]/GetModule(ModuleTypes.XZ)),  //piano xz, angolo rispetto ad x
                Math.acos(initialVector[1]/GetModule(ModuleTypes.YZ)),  //piano yz, angolo rispetto ad y
        };

        //piano XY
        if(expectedAngles[0] != -1)
            changeReferenceSystemAngles[0] = originalAngles[0] - expectedAngles[0];
        else
            changeReferenceSystemAngles[0] = 0;

        //piano XZ
        if(expectedAngles[1] != -1)
            changeReferenceSystemAngles[1] = originalAngles[1] - expectedAngles[1];
        else
            changeReferenceSystemAngles[1] = 0;

        //piano YZ
        if(expectedAngles[2] != -1)
            changeReferenceSystemAngles[2] = originalAngles[2] - expectedAngles[2];
        else
            changeReferenceSystemAngles[2] = 0;
    }*/

    /**
     * Converte un vettore misurato nel precedente sistema di riferimento, in un vettore nel nuovo sistema
     * di riferimento.
     * @param vector Il vettore misurato nel vecchio sistema di riferimento.
     * @return Lo stesso vettore, le quale componenti sono relative al nuovo sistema di riferimento.
     * @throws Exception Lancia un'eccezione se il vettore non ha 3 parametri.
     */
    public Vector ConvertToNewReferenceSystem(Vector vector) {
        Point p = new Point(vector, destinationSpace.origin);

        //il nuovo vettore viene calcolato prendendo le componenti dello stesso vettore lungo gli assi cartesiani del destinationSpace
        return new Vector(
                whichHalfSpace(AngleBetween(vector,destinationSpace.xAxis)) *
                        Distance(destinationSpace.origin, PerpendicularToLine(destinationSpace.xAxis.toLine(destinationSpace.origin), p)),
                whichHalfSpace(AngleBetween(vector,destinationSpace.yAxis)) *
                        Distance(destinationSpace.origin, PerpendicularToLine(destinationSpace.yAxis.toLine(destinationSpace.origin), p)),
                whichHalfSpace(AngleBetween(vector,destinationSpace.zAxis)) *
                        Distance(destinationSpace.origin, PerpendicularToLine(destinationSpace.zAxis.toLine(destinationSpace.origin), p))
        );
    }

    /*/**
     * Enumerazione che serve in combinazione con il metodo GetModule, per indicare il modulo rispetto a quale piano prendere:
     * XYZ = modulo nello spazio;
     * XY = modulo nel piano XY;
     * XZ = modulo nel piano XZ;
     * YZ = modulo nel piano YZ.
     *
    private enum ModuleTypes {XYZ, XY, XZ, YZ}
    private double GetModule(ModuleTypes moduleToGet){
        switch (moduleToGet){
            case XY:
                return Math.hypot(initialVector[0], initialVector[1]);
            case XZ:
                return Math.hypot(initialVector[0], initialVector[2]);
            case YZ:
                return Math.hypot(initialVector[1], initialVector[2]);
            case XYZ:
            default:
                return Math.sqrt(initialVector[0] * initialVector[0] +
                        initialVector[1] * initialVector[1] +
                        initialVector[2]* initialVector[2]);
        }
    }*/

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*/**
     * Gli angoli di Eulero descrivono la posizione di un sistema di riferimento XYZ solidale con un corpo rigido
     * attraverso una serie di rotazioni a partire da un sistema di riferimento fisso xyz. I due sistemi di riferimento
     * coincidono nell'origine.
     *
     * Se i piani xy e XY sono distinti, si intersecano in una retta (passante per l'origine) detta linea dei nodi (N).
     * Se i piani coincidono, si definisce la linea dei nodi N come l'asse X. Gli angoli di Eulero sono i tre angoli seguenti:
     *
     * [0] = alpha  è l'angolo tra l'asse x e la linea dei nodi. Detto angolo di precessione, è definito in [0,2pi);
     * [1] = beta  è l'angolo tra gli assi z e Z. Detto angolo di nutazione, è definito in [0,pi];
     * [2] = gamma  è l'angolo tra la linea dei nodi e l'asse X. Detto angolo di rotazione propria, è definito in [0,2pi).
     *
    private double[] eulerosAngles;

    private final double[][] rAlfa = new double[][]{
        { Math.cos(eulerosAngles[0]), Math.sin(eulerosAngles[0]), 0 },
        { -Math.sin(eulerosAngles[0]), Math.cos(eulerosAngles[0]), 0 },
        { 0, 0, 1 }
    };

    private double[][] rBeta = new double[][]{
            { 1, 0, 0 },
            { 0, Math.cos(eulerosAngles[1]), Math.sin(eulerosAngles[1]) },
            { 0, -Math.sin(eulerosAngles[1]), Math.cos(eulerosAngles[1]) }
    };
    private double[][] rGamma = new double[][]{
            { Math.cos(eulerosAngles[2]), Math.sin(eulerosAngles[2]), 0 },
            { -Math.sin(eulerosAngles[2]), Math.cos(eulerosAngles[2]), 0 },
            { 0, 0, 1 }
    };*/

}
