package org.openhab.binding.entsoe.internal.monetary;

import javax.measure.Dimension;
import java.util.Map;

public class CurrencyDimension implements Dimension {
    public static final CurrencyDimension INSTANCE = new CurrencyDimension();

    private CurrencyDimension() {
    }

    /**
     * Returns the product of this dimension with the one specified.
     *
     * @param multiplicand the dimension multiplicand.
     * @return {@code this * multiplicand}
     */
    @Override
    public Dimension multiply(Dimension multiplicand) {
        return multiplicand instanceof CurrencyDimension ? INSTANCE : multiplicand.multiply(this);
    }

    /**
     * Returns the quotient of this dimension with the one specified.
     *
     * @param divisor the dimension divisor.
     * @return {@code this / divisor}
     */
    @Override
    public Dimension divide(Dimension divisor) {
        return divisor instanceof CurrencyDimension ? INSTANCE : divisor.multiply(this);
    }

    /**
     * Returns this dimension raised to an exponent. <code>(this<sup>n</sup>)</code>
     *
     * @param n power to raise this {@code Dimension} to.
     * @return <code>this<sup>n</sup></code>
     */
    @Override
    public Dimension pow(int n) {
        return INSTANCE;
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
        return INSTANCE;
    }

    /**
     * Returns the (fundamental) base dimensions and their exponent whose product is this dimension, or {@code null} if
     * this dimension is a base dimension.
     *
     * @return the mapping between the fundamental dimensions and their exponent.
     */
    @Override
    public Map<? extends Dimension, Integer> getBaseDimensions() {
        return null;
    }

}
