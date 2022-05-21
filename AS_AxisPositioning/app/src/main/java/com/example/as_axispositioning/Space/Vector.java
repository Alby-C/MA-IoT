package com.example.as_axispositioning.Space;

/**
 * Rappresenta un vettore nello spazio cartesiano.
 */
public class Vector {
    /**
     * Il vettore nullo
     */
    public static final Vector ZERO_VECTOR = new Vector(0,0,0);

    public final double X;
    public final double Y;
    public final double Z;

    /**
     * Inizializza un punto.
     * @param X Ascissa.
     * @param Y Ordinata.
     * @param Z Quota.
     */
    public Vector(double X, double Y, double Z){
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    /**
     * Crea un vettore come differenza di due punti.
     * @param p1 Punto di arrivo, testa del vettore.
     * @param p2 Punto di partenza, coda del vettore.
     */
    public Vector(Point p1, Point p2){
        this.X = p1.X - p2.X;
        this.Y = p1.Y - p2.Y;
        this.Z = p1.Z - p2.Z;
    }

    /**
     * Crea un vettore data direzione, verso e un modulo.
     * @param module Il modulo del nuovo vettore.
     * @param versor Il versore che stabilisce direzione e verso (nel caso un cui non sia un versore verrà
     *               comunque normalizzato).
     */
    public Vector(double module, Vector versor){
        if(versor.getModule() != 1)
            versor = versor.toVersor();

        this.X = versor.X * module;
        this.Y = versor.Y * module;
        this.Z = versor.Z * module;
    }

    /**
     * Calcola il modulo della corrente istanza vettore.
     * @return Il modulo dell'istanza.
     */
    public double getModule() {
        return Math.sqrt(X * X + Y * Y + Z * Z);
    }

    /**
     * Restituisce il versore della corrente istanza.
     * @return Il versore del vettore corrente.
     */
    public Vector toVersor(){
        return new Vector(
                this.X / this.getModule(),
                this.Y / this.getModule(),
                this.Z / this.getModule()
        );
    }

    /**
     * Genera una retta con questa istanza come vettore direzionale, e il punto p come punto su cui passa.
     * @param p Punto su cui passa la retta.
     * @return La retta generata.
     */
    public Line toLine(Point p){
        return new Line(this, p);
    }

    /**
     * Restituisce l'istanza corrente sottoforma di array:
     * [0] = X;
     * [1] = Y;
     * [2] = Z.
     * @return L'istanza corrente sottoforma di array.
     */
    public double[] toArray(){
        return new double[]{this.X, this.Y, this.Z};
    }

    /**
     * Determina se l'istanza corrente è equivalente a quella inserita (stessa direzione e verso)
     * @param v Il vettore da comparare
     * @return true se sono equivalenti, false se non lo sono
     */
    public boolean isEquivalent(Vector v){
        return this.toVersor() == v.toVersor();
    }

    /**
     * Determina se l'istanza corrente è uguale a quella inserita
     * @param v Il vettore da comparare
     * @return true se sono uguali, false se non lo sono
     */
    public boolean isEqual(Vector v){
        return this.X == v.X && this.Y == v.Y && this.Z == v.Z;
    }

    public String toString(){
        return "("+this.X+", "+this.Y+", "+this.Z+")";
    }
}
