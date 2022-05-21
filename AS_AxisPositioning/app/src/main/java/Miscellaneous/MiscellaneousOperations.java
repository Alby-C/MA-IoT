package Miscellaneous;

public class MiscellaneousOperations {
    private static final int SIGNIFICANT_DIGITS = 5;

    /**
     * Tronca le il numero inidicato al numero di cifre significative specificato dalla costante
     * SIGNIFICANT_DIGITS.
     * @param num Il numero da troncare.
     * @return Il numero num troncato.
     */
    public static double Truncate(double num){
        double sigDigits = Math.pow(10, SIGNIFICANT_DIGITS);
        long numToLong = (long)(num * sigDigits);
        return (double)numToLong / sigDigits;
        //return Math.floor(num * sigDigits) / sigDigits;
    }

    /**
     * Tronca le il numero inidicato al numero di cifre significative specificato.
     * @param num Il numero da troncare.
     * @param significantDigits Il numero di cifre significative.
     * @return Il numero num troncato.
     */
    public static double Truncate(double num, int significantDigits){
        double sigDigits = Math.pow(10, significantDigits);
        long numToLong = (long)(num * sigDigits);
        return (double)numToLong / sigDigits;
        //return Math.floor(num * sigDigits) / sigDigits;
    }
}
