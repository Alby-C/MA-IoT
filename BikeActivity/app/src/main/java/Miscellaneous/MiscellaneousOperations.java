package Miscellaneous;

public class MiscellaneousOperations {
    private static final int SIGNIFICANT_DIGITS = 5;

    /**
     * Truncates the specified number to the number of significant digits specified
     * by the SIGNIFICANT_DIGITS constant.
     * @param num The number to be truncated.
     * @return The num number truncated.
     */
    public static double Truncate(double num){
        double sigDigits = Math.pow(10, SIGNIFICANT_DIGITS);
        long numToLong = (long)(num * sigDigits);
        return (double)numToLong / sigDigits;
    }

    /**
     * Truncates the specified number to the specified number of significant digits.
     * @param num The number to be truncated.
     * @param significantDigits The number of significant digits.
     * @return The num number truncated.
     */
    public static double Truncate(double num, int significantDigits){
        double sigDigits = Math.pow(10, significantDigits);
        long numToLong = (long)(num * sigDigits);
        return (double)numToLong / sigDigits;
    }
}
