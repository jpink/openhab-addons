package org.openhab.binding.entsoe.internal.monetary;

import javax.measure.*;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public abstract class AbstractUnit<Q extends Quantity<Q>> implements Unit<Q> {
    protected final @Nullable Prefix prefix;
    private final String symbols;
    private final String name;
    private final Dimension dimension;

    protected AbstractUnit(@Nullable Prefix prefix, @NonNull String symbols, @NonNull String name,
            @NonNull Dimension dimension) {
        this.prefix = prefix;
        this.symbols = symbols;
        this.name = name;
        this.dimension = dimension;
    }

    /**
     * Returns the symbols (if any) of this unit. This method returns {@code null} if this unit has no specific symbols
     * associated with.
     *
     * @return this unit symbols, or {@code null} if this unit has not specific symbols associated with (e.g. product of
     *         units).
     * @see #toString()
     * @see javax.measure.format.UnitFormat
     */
    @Override
    public String getSymbol() {
        return symbols.length() == 1 ? symbols : null;
    }

    /**
     * Returns the name (if any) of this unit. This method returns {@code null} if this unit has no specific name
     * associated with.
     *
     * @return this unit name, or {@code null} if this unit has not specific name associated with (e.g. product of
     *         units).
     * @see #toString()
     * @see javax.measure.format.UnitFormat
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the dimension of this unit. Two units {@code u1} and {@code u2} are
     * {@linkplain #isCompatible(Unit) compatible} if and only if {@code u1.getDimension().equals(u2.getDimension())}.
     *
     * @return the dimension of this unit.
     * @see #isCompatible(Unit)
     */
    @Override
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * Indicates if this unit is compatible with the unit specified. Units don't need to be equal to be compatible. For
     * example (assuming {@code ONE} is a dimensionless unit):<br>
     *
     * <code>
     * RADIAN.equals(ONE) == false<br> RADIAN.isCompatible(ONE) == true<br> RADIAN.isEquivalentTo(ONE) <b>doesn't
     * compile</b><br>
     * </code>
     *
     * @param that the other unit to compare for compatibility.
     * @return {@code this.getDimension().equals(that.getDimension())}
     * @see #getDimension()
     */
    @Override
    public boolean isCompatible(Unit<?> that) {
        return dimension.equals(that.getDimension());
    }

    /**
     * Indicates if this unit represents the same quantity as the given unit, ignoring name and symbols. Two units are
     * equivalent if the {@linkplain #getConverterTo(Unit) conversion} between them is identity.
     *
     * <p>
     * Unlike {@link #isCompatible(Unit)} an equivalence check requires both units to be strictly type-compatible,
     * because it makes no sense to compare e.g. {@code gram} and {@code mm} for equivalence. By contrast, the
     * compatibility check can works across different quantity types.
     * </p>
     *
     * @param that the {@code Unit<Q>} to be compared with this instance.
     * @return {@code true} if {@code that ≡ this}.
     * @throws NullPointerException if the unit is null
     * @see <a href= "https://dictionary.cambridge.org/dictionary/english/equivalent">Cambridge Dictionary:
     *      equivalent</a>
     * @see <a href= "https://www.lexico.com/en/definition/equivalent">LEXICO: equivalent</a>
     * @since 2.1
     */
    @Override
    public boolean isEquivalentTo(Unit<Q> that) {
        return getConverterTo(that).isIdentity();
    }

    /**
     * Casts this unit to a parameterized unit of specified nature or throw a {@code ClassCastException} if the
     * dimension of the specified quantity and this unit's dimension do not match. For example:<br>
     *
     * <code>
     * {@literal Unit<Speed>} C = METRE.multiply(299792458).divide(SECOND).asType(Speed.class);
     * </code>
     *
     * @param type the quantity class identifying the nature of the unit.
     * @return this unit parameterized with the specified type.
     * @throws ClassCastException if the dimension of this unit is different from the specified quantity
     *             dimension.
     */
    @Override
    public <T extends Quantity<T>> Unit<T> asType(Class<T> type) throws ClassCastException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a converter of numeric values from this unit to another unit of same type. This method performs the same
     * work as {@link #getConverterToAny(Unit)} without raising checked exception.
     *
     * @param that the unit of same type to which to convert the numeric values.
     * @return the converter from this unit to {@code that} unit.
     * @throws UnconvertibleException if a converter cannot be constructed.
     * @see #getConverterToAny(Unit)
     */
    @Override
    public UnitConverter getConverterTo(Unit<Q> that) throws UnconvertibleException {
        return this.equals(that) ? IdentityConverter.INSTANCE : null; // TODO replace null
    }

    /**
     * Returns a converter from this unit to the specified unit of type unknown. This method can be used when the
     * quantity type of the specified unit is unknown at compile-time or when dimensional analysis allows for conversion
     * between units of different type.
     *
     * <p>
     * To convert to a unit having the same parameterized type, {@link #getConverterTo(Unit)} is preferred (no checked
     * exception raised).
     * </p>
     *
     * @param that the unit to which to convert the numeric values.
     * @return the converter from this unit to {@code that} unit.
     * @throws IncommensurableException if this unit is not {@linkplain #isCompatible(Unit) compatible} with
     *             {@code that} unit.
     * @throws UnconvertibleException if a converter cannot be constructed.
     * @see #getConverterTo(Unit)
     * @see #isCompatible(Unit)
     */
    @Override
    public UnitConverter getConverterToAny(Unit<?> that) throws IncommensurableException, UnconvertibleException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a system unit equivalent to this unscaled standard unit but used in expressions to distinguish between
     * quantities of a different nature but of the same dimensions.
     *
     * <p>
     * Examples of alternate units:
     * </p>
     *
     * <code>
     * {@literal Unit<Angle>} RADIAN = ONE.alternate("rad").asType(Angle.class);<br> {@literal Unit<Force>} NEWTON =
     * METRE.multiply(KILOGRAM).divide(SECOND.exponent(2)).alternate("N").asType(Force.class);<br>
     * {@literal Unit<Pressure>} PASCAL = NEWTON.divide(METRE.exponent(2)).alternate("Pa").asType(Pressure.class);<br>
     * </code>
     *
     * @param symbol the new symbols for the alternate unit.
     * @return the alternate unit.
     * @throws IllegalArgumentException if this unit is not an unscaled standard unit.
     * @throws MeasurementException if the specified symbols is not valid or is already associated to a
     *             different unit.
     */
    @Override
    public Unit<Q> alternate(String symbol) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the result of setting the origin of the scale of measurement to the given value. The returned unit is
     * convertible with all units that are convertible with this unit. For example the following code:<br>
     *
     * <code>
     * CELSIUS = KELVIN.shift(273.15);
     * </code>
     * <p>
     * creates a new unit where 0°C (the origin of the new unit) is equals to 273.15 K. Converting from the old unit to
     * the new one is equivalent to
     * <em>subtracting</em> the offset to the value in the old unit.
     *
     * @param offset the offset added (expressed in this unit).
     * @return this unit offset by the specified value.
     * @since 2.0
     */
    @Override
    public Unit<Q> shift(Number offset) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the result of setting the origin of the scale of measurement to the given value. The returned unit is
     * convertible with all units that are convertible with this unit. For example the following code:<br>
     *
     * <code>
     * CELSIUS = KELVIN.shift(273.15);
     * </code>
     * <p>
     * creates a new unit where 0°C (the origin of the new unit) is equals to 273.15 K. Converting from the old unit to
     * the new one is equivalent to
     * <em>subtracting</em> the offset to the value in the old unit.
     *
     * @param offset the offset added (expressed in this unit).
     * @return this unit offset by the specified value.
     */
    @Override
    public Unit<Q> shift(double offset) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the result of multiplying this unit by the specified factor. If the factor is an integer value, the
     * multiplication is exact (recommended). For example:<br>
     *
     * <code>
     * FOOT = METRE.multiply(3048).divide(10000); // Exact definition.<br> ELECTRON_MASS =
     * KILOGRAM.multiply(9.10938188e-31); // Approximation.
     * </code>
     *
     * @param multiplier the multiplier
     * @return this unit scaled by the specified multiplier.
     * @since 2.0
     */
    @Override
    public Unit<Q> multiply(Number multiplier) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the result of multiplying this unit by the specified factor. For example:<br>
     *
     * <code>
     * FOOT = METRE.multiply(3048).divide(10000); // Exact definition.<br> ELECTRON_MASS =
     * KILOGRAM.multiply(9.10938188e-31); // Approximation.
     * </code>
     *
     * @param multiplier the multiplier
     * @return this unit scaled by the specified multiplier.
     */
    @Override
    public Unit<Q> multiply(double multiplier) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the product of this unit with the one specified.
     *
     * @param multiplier the unit multiplier.
     * @return {@code this * multiplier}
     */
    @Override
    public Unit<?> multiply(Unit<?> multiplier) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the reciprocal (multiplicative inverse) of this unit.
     *
     * @return {@code 1 / this}
     * @see <a href="https://en.wikipedia.org/wiki/Multiplicative_inverse">Wikipedia: Multiplicative inverse</a>
     */
    @Override
    public Unit<?> inverse() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the result of dividing this unit by a divisor. If the factor is an integer value, the division is exact.
     * For example:<br>
     *
     * <code>
     * GRAM = KILOGRAM.divide(1000); // Exact definition.
     * </code>
     *
     * @param divisor the divisor value.
     * @return this unit divided by the specified divisor.
     * @since 2.0
     */
    @Override
    public Unit<Q> divide(Number divisor) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the result of dividing this unit by an approximate divisor. For example:<br>
     *
     * <code>
     * GRAM = KILOGRAM.divide(1000d);
     * </code>
     *
     * @param divisor the divisor value.
     * @return this unit divided by the specified divisor.
     */
    @Override
    public Unit<Q> divide(double divisor) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the quotient of this unit with the one specified.
     *
     * @param divisor the unit divisor.
     * @return {@code this / divisor}
     */
    @Override
    public Unit<?> divide(Unit<?> divisor) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an unit that is the n-th (integer) root of this unit. Equivalent to the mathematical expression
     * {@code unit^(1/n)}.
     *
     * @param n an integer giving the root's order as in 'n-th root'
     * @return the n-th root of this unit.
     * @throws ArithmeticException if {@code n == 0} or if this operation would result in an unit with a
     *             fractional exponent.
     */
    @Override
    public Unit<?> root(int n) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an unit raised to the n-th (integer) power of this unit. Equivalent to the mathematical expression
     * {@code unit^n}.
     *
     * @param n the exponent.
     * @return the result of raising this unit to the exponent.
     */
    @Override
    public Unit<?> pow(int n) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the unit derived from this unit using the specified converter. The converter does not need to be linear.
     * For example:<br>
     *
     * <pre>
     *     {@literal Unit<Dimensionless>} DECIBEL = Unit.ONE.transform(
     *         new LogConverter(10).inverse().concatenate(
     *             new RationalConverter(1, 10)));
     * </pre>
     *
     * @param operation the converter from the transformed unit to this unit.
     * @return the unit after the specified transformation.
     */
    @Override
    public Unit<Q> transform(UnitConverter operation) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a new unit equal to this unit prefixed by the specified {@code prefix}.
     *
     * @param prefix the prefix to apply on this unit.
     * @return the unit with the given prefix applied.
     * @since 2.0
     */
    @Override
    public Unit<Q> prefix(Prefix prefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        return obj instanceof Unit<?> && getName().equals(((Unit<?>) obj).getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return symbols;
    }
}
