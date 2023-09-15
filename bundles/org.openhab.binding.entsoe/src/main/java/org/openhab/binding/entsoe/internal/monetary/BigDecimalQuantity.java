package org.openhab.binding.entsoe.internal.monetary;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.Unit;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.jdt.annotation.NonNull;

public abstract class BigDecimalQuantity<Q extends Quantity<Q>> implements Quantity<Q> {
    private final BigDecimal value;
    private final MathContext context;
    private final Unit<Q> unit;
    private final Scale scale;

    protected BigDecimalQuantity(@NonNull BigDecimal value, @NonNull Unit<Q> unit) {
        this(value, MathContext.UNLIMITED, unit, Scale.RELATIVE);
    }

    protected BigDecimalQuantity(@NonNull BigDecimal value, @NonNull MathContext context, @NonNull Unit<Q> unit,
            @NonNull Scale scale) {
        this.value = value;
        this.context = context;
        this.unit = unit;
        this.scale = scale;
    }

    private Quantity<Q> create(@NonNull BigDecimal value) {
        return create(value, unit);
    }

    private Quantity<Q> create(@NonNull BigDecimal value, @NonNull Unit<Q> unit) {
        return create(value, context, unit, scale);
    }

    protected abstract Quantity<Q> create(@NonNull BigDecimal value, @NonNull MathContext context,
            @NonNull Unit<Q> unit, @NonNull Scale scale);

    private BigDecimal convert(Quantity<?> that) {
        var thatUnit = that.getUnit();
        // TODO allow also other implementations
        // if (thatUnit instanceof CurrencyUnit) //noinspection unchecked
        // return convertBigDecimal((Quantity<BigDecimalQuantity>) that);
        throw new IllegalArgumentException(thatUnit + " isn't currency unit!");
    }

    private BigDecimal convertBigDecimal(Quantity<Q> that) {
        var thatUnit = that.getUnit();
        return (BigDecimal) (unit.equals(thatUnit) ? that.getValue()
                : thatUnit.getConverterTo(unit).convert(that.getValue()));
    }

    private RoundingMode getRoundingMode() {
        return context.getRoundingMode();
    }

    private BigDecimal parse(Number that) {
        Objects.requireNonNull(that, "Number must have value!");
        // TODO Ensure same math context.
        return (that instanceof BigDecimal) ? (BigDecimal) that : new BigDecimal(that.toString(), context);
    }

    /**
     * Returns the sum of this {@code Quantity} with the one specified. The result shall be as if this quantity and the
     * given addend were converted to {@linkplain Unit#getSystemUnit() system unit} before to be added, and the result
     * converted back to the unit of this quantity or any other compatible unit at implementation choice.
     *
     * @param addend the {@code Quantity} to be added.
     * @return {@code this + addend}.
     */
    @Override
    public Quantity<Q> add(Quantity<Q> addend) {
        return create(value.add(convertBigDecimal(addend)));
    }

    /**
     * Returns the difference between this {@code Quantity} and the one specified. The result shall be as if this
     * quantity and the given subtrahend were converted to {@linkplain Unit#getSystemUnit() system unit} before to be
     * subtracted, and the result converted back to the unit of this quantity or any other compatible unit at
     * implementation choice.
     *
     * @param subtrahend the {@code Quantity} to be subtracted.
     * @return <code>this - subtrahend</code>.
     */
    @Override
    public Quantity<Q> subtract(Quantity<Q> subtrahend) {
        return create(value.subtract(convertBigDecimal(subtrahend)));
    }

    /**
     * Returns the quotient of this {@code Quantity} divided by the {@code Quantity} specified. The result shall be as
     * if this quantity and the given divisor were converted to {@linkplain Unit#getSystemUnit() system unit} before to
     * be divided, and the result converted back to the unit of this quantity or any other compatible unit at
     * implementation choice.
     *
     * @param divisor the {@code Quantity} divisor.
     * @return <code>this / divisor</code>.
     * @throws ClassCastException if the type of element in the specified operation is incompatible with this
     *             quantity
     */
    @Override
    public Quantity<?> divide(Quantity<?> divisor) {
        return create(value.divide(convert(divisor), getRoundingMode()));
    }

    /**
     * Returns the quotient of this {@code Quantity} divided by the {@code Number} specified. The result shall be as if
     * this quantity was converted to {@linkplain Unit#getSystemUnit() system unit} before to be divided, and the result
     * converted back to the unit of this quantity or any other compatible unit at implementation choice.
     *
     * @param divisor the {@code Number} divisor.
     * @return <code>this / divisor</code>.
     */
    @Override
    public Quantity<Q> divide(Number divisor) {
        return create(value.divide(parse(divisor), getRoundingMode()));
    }

    /**
     * Returns the product of this {@code Quantity} with the one specified. The result shall be as if this quantity and
     * the given multiplicand were converted to {@linkplain Unit#getSystemUnit() system unit} before to be multiplied,
     * and the result converted back to the unit of this quantity or any other compatible unit at implementation
     * choice.
     *
     * @param multiplicand the {@code Quantity} multiplicand.
     * @return <code>this * multiplicand</code>.
     * @throws ClassCastException if the type of element in the specified operation is incompatible with this
     *             quantity
     */
    @Override
    public Quantity<?> multiply(Quantity<?> multiplicand) {
        return create(value.multiply(convert(multiplicand)));
    }

    /**
     * Returns the product of this {@code Quantity} with the {@code Number} value specified. The result shall be as if
     * this quantity was converted to {@linkplain Unit#getSystemUnit() system unit} before to be multiplied, and the
     * result converted back to the unit of this quantity or any other compatible unit at implementation choice.
     *
     * @param multiplicand the {@code Number} multiplicand.
     * @return <code>this * multiplicand</code>.
     */
    @Override
    public Quantity<Q> multiply(Number multiplicand) {
        return create(value.multiply(parse(multiplicand)));
    }

    /**
     * Returns this {@code Quantity} converted into another (compatible) {@code Unit}.
     *
     * @param unit the {@code Unit unit} in which the returned quantity is stated.
     * @return this quantity or a new quantity equivalent to this quantity stated in the specified unit.
     * @throws ArithmeticException if the result is inexact and the quotient has a non-terminating decimal
     *             expansion.
     */
    @Override
    public Quantity<Q> to(Unit<Q> unit) {
        return create(parse(this.unit.getConverterTo(unit).convert(value)), unit);
    }

    /**
     * Returns a {@code Quantity} that is the multiplicative inverse of this {@code Quantity}, having reciprocal value
     * and reciprocal unit as given by {@code this.getUnit().inverse()}.
     *
     * @return reciprocal {@code Quantity}
     * @see <a href= "https://en.wikipedia.org/wiki/Multiplicative_inverse">Wikipedia: Multiplicative inverse</a>
     */
    @Override
    public Quantity<?> inverse() {
        return create(BigDecimal.ONE.divide(value, getRoundingMode()));
    }

    /**
     * Returns a {@code Quantity} whose value is {@code (-this.getValue())}.
     *
     * @return {@code -this}.
     */
    @Override
    public Quantity<Q> negate() {
        return create(value.negate());
    }

    /**
     * Casts this quantity to a parameterized unit of specified nature or throw a
     * <code>ClassCastException</code> if the dimension of the specified quantity
     * and this measure unit's dimension do not match. For example:
     * <p>
     * <code>
     * {@literal Quantity<Length>} length = Quantities.getQuantity("2 km").asType(Length.class);
     * </code> or <code>
     * {@literal Quantity<Speed>} C = length.multiply(299792458).divide(second).asType(Speed.class);
     * </code>
     * </p>
     *
     * @param type the quantity class identifying the nature of the quantity.
     * @return this quantity parameterized with the specified type.
     * @throws ClassCastException if the dimension of this unit is different from the specified quantity
     *             dimension.
     * @throws UnsupportedOperationException if the specified quantity class does not have a SI unit for the
     *             quantity.
     * @see Unit#asType(Class)
     */
    @Override
    public <T extends Quantity<T>> Quantity<T> asType(Class<T> type) throws ClassCastException {
        throw new NotImplementedException("TODO"); // TODO
    }

    /**
     * Returns the value of this {@code Quantity}.
     *
     * @return a value.
     */
    @Override
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Returns the unit of this {@code Quantity}.
     *
     * @return the unit (shall not be {@code null}).
     */
    @Override
    public Unit<Q> getUnit() {
        return unit;
    }

    /**
     * Returns the {@code Scale} of this {@code Quantity}, if it's absolute or relative.
     *
     * @return the scale, if it's an absolute or relative quantity.
     * @see <a href="https://en.wikipedia.org/wiki/Absolute_scale">Wikipedia: Absolute scale</a>
     * @since 2.0
     */
    @Override
    public Scale getScale() {
        return scale;
    }

    /**
     * Compares two instances of {@code Quantity 
     * 
    <Q>}, performing the conversion of units if necessary.
     *
     * @param that the {@code quantity<Q>} to be compared with this instance.
     * @return {@code true} if {@code that ≡ this}.
     * @throws NullPointerException if the quantity is null
     * @see <a href= "https://dictionary.cambridge.org/dictionary/english/equivalent">Cambridge Dictionary:
     *      equivalent</a>
     * @see <a href= "https://www.lexico.com/en/definition/equivalent">LEXICO: equivalent</a>
     * @since 2.1
     */
    @Override
    public boolean isEquivalentTo(Quantity<Q> that) {
        return value.equals(convertBigDecimal(that));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        return (obj instanceof Quantity<?> that) && getValue().equals(that.getValue())
                && getUnit().equals(that.getUnit());
    }

    @Override
    public int hashCode() {
        return value.hashCode() + unit.hashCode();
    }

    @Override
    public String toString() {
        return value + " " + unit;
    }
}
