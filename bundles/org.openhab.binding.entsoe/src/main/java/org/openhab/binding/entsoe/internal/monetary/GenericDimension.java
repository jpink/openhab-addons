package org.openhab.binding.entsoe.internal.monetary;

import static org.openhab.binding.entsoe.internal.Maps.*;
import static org.openhab.binding.entsoe.internal.Text.toExponent;

import java.util.*;

import javax.measure.Dimension;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.core.library.unit.Units;

public record GenericDimension(@NonNull String symbols, Map<Dimension, Integer> baseDimensions) implements Dimension {

    public static final Dimension ENERGY = Units.JOULE.getDimension();
    public static final Dimension MONETARY = new GenericDimension("[¤]", null);
    public static final Dimension MONETARY_ENERGY = MONETARY.divide(ENERGY);

    @SuppressWarnings("unchecked")
    private static @NonNull Map<Dimension, Integer> getDimensions(Dimension dimension) {
        var baseDimensions = dimension.getBaseDimensions();
        return baseDimensions == null ? Collections.singletonMap(dimension, 1)
                : (Map<Dimension, Integer>) baseDimensions;
    }

    private static Dimension of(Map<Dimension, Integer> baseDimensions) {
        baseDimensions = filter(baseDimensions, (baseDimension, exponent) -> exponent != 0);
        if (baseDimensions.isEmpty())
            throw new IllegalArgumentException("Dimension one not supported!");
        if (baseDimensions.size() == 1) {
            var entry = baseDimensions.entrySet().iterator().next();
            if (entry.getValue() == 1)
                return entry.getKey();
        }

        var exponentBySymbol = new TreeMap<>(mapKeys(baseDimensions, entry -> entry.getKey().toString())); // For
                                                                                                           // sorting
        var builder = new StringBuilder();
        filter(exponentBySymbol, (dimension, exponent) -> exponent > 0)
                .forEach((dimension, exponent) -> builder.append(dimension).append(toExponent(exponent)).append('·'));
        var length = builder.length();
        builder.replace(length - 1, length, "/"); // Replace the last '·' with '/'.
        filter(exponentBySymbol, (dimension, exponent) -> exponent < 0)
                .forEach((dimension, exponent) -> builder.append(dimension).append(toExponent(-exponent)).append('·'));

        // Remove the last '·' from symbols.
        return new GenericDimension(builder.substring(0, builder.length() - 1), baseDimensions);
    }

    public GenericDimension {
        if (baseDimensions == null) {
            Objects.requireNonNull(symbols);
            if (symbols.length() != 3)
                throw new IllegalArgumentException("Base dimension symbol must be one character!");
            if (symbols.charAt(0) != '[' || symbols.charAt(2) != ']')
                throw new IllegalArgumentException("Base dimension symbol must be in square brackets!");
        } else if (baseDimensions.containsValue(0))
            throw new IllegalArgumentException("Remove base dimensions which equal 1!");
    }

    private Dimension of(int n) {
        return of(mapValues(getDimensions(this),
                entry -> equals(entry.getKey()) ? entry.getValue() + n : entry.getValue()));
    }

    private Dimension ofCombination(Map<Dimension, Integer> that) {
        return of(combine(getDimensions(this), that, Integer::sum));
    }

    /**
     * Returns the product of this dimension with the one specified.
     *
     * @param multiplicand the dimension multiplicand.
     * @return {@code this * multiplicand}
     */
    @Override
    public Dimension multiply(Dimension multiplicand) {
        return ofCombination(getDimensions(multiplicand));
    }

    /**
     * Returns the quotient of this dimension with the one specified.
     *
     * @param divisor the dimension divisor.
     * @return {@code this / divisor}
     */
    @Override
    public Dimension divide(Dimension divisor) {
        return ofCombination(mapValues(getDimensions(divisor), entry -> -entry.getValue()));
    }

    /**
     * Returns this dimension raised to an exponent. <code>(this<sup>n</sup>)</code>
     *
     * @param n power to raise this {@code Dimension} to.
     * @return <code>this<sup>n</sup></code>
     */
    @Override
    public Dimension pow(int n) {
        return of(n);
    }

    /**
     * Returns the given root of this dimension.
     *
     * @param n the root's order.
     * @return the result of taking the given root of this dimension.
     * @throws ArithmeticException if {@code n == 0}.
     */
    @Override
    public Dimension root(int n) {
        return of(-n);
    }

    /**
     * Returns the (fundamental) base dimensions and their exponent whose product is this dimension, or {@code null} if
     * this dimension is a base dimension.
     *
     * @return the mapping between the fundamental dimensions and their exponent.
     */
    @Override
    public Map<? extends Dimension, Integer> getBaseDimensions() {
        return baseDimensions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        return (obj instanceof Dimension that)
                && (baseDimensions == null ? (that.getBaseDimensions() == null && symbols.equals(that.toString()))
                        : baseDimensions.equals(that.getBaseDimensions()));
    }

    @Override
    public String toString() {
        return symbols;
    }
}
