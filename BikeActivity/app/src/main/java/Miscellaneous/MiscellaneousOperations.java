package Miscellaneous;

public class MiscellaneousOperations {
    private static final int SIGNIFICANT_DIGITS = 5;
    private static final float TRUNCATE_CONSTANT = (float) Math.pow(10, SIGNIFICANT_DIGITS);

    /**
     * Truncates the specified number to the number of significant digits specified
     * by the SIGNIFICANT_DIGITS constant.
     * @param num The number to be truncated.
     * @return The num number truncated.
     */
    public static float Truncate(double num){
        long numToLong = (long)(num * TRUNCATE_CONSTANT);
        return (float)(numToLong / TRUNCATE_CONSTANT);
    }

    /**
     * Truncates the specified number to the specified number of significant digits.
     * @param num The number to be truncated.
     * @param significantDigits The number of significant digits.
     * @return The num number truncated.
     */
    public static float Truncate(double num, int significantDigits){
        float sigDigits = (float)Math.pow(10, significantDigits);
        long numToLong = (long)(num * sigDigits);
        return (float)numToLong / sigDigits;
    }
}
