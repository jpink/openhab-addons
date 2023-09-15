package org.openhab.binding.entsoe.internal;

public class Text {
    public static String toExponent(int exponent) {
        return switch (exponent) {
            case 1 -> ""; // Could be '¹' but useless.
            case 2 -> "²";
            case 3 -> "³";
            default -> "^" + exponent;
        };
    }
}
