package org.openhab.binding.entsoe.internal.price.service.measure;

import tech.units.indriya.AbstractQuantity;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.internal.function.Calculator;
import tech.units.indriya.internal.function.ScaleHelper;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;

import java.io.Serial;

/**
 * Same class as tech.units.indriya.quantity.NumberQuantity but opened the final class.
 *
 * @see tech.units.indriya.quantity.NumberQuantity
 */
public class AbstractNumberQuantity<Q extends Quantity<Q>> extends AbstractQuantity<Q> {
    @Serial
    private static final long serialVersionUID = 3736755670132778167L;

    private final Number value;

    protected AbstractNumberQuantity(Number number, Unit<Q> unit, Scale sc) {
        super(unit, sc);
        value = Calculator.of(number).peek(); // takes care of invalid number values (infinity, ...)
    }

    @Override
    public ComparableQuantity<Q> add(Quantity<Q> that) {
        return ScaleHelper.addition(this, that,
                (thisValue, thatValue) -> Calculator.of(thisValue).add(thatValue).peek());
    }

    @Override
    public ComparableQuantity<Q> subtract(Quantity<Q> that) {
        return ScaleHelper.addition(this, that,
                (thisValue, thatValue) -> Calculator.of(thisValue).subtract(thatValue).peek());
    }

    @Override
    public ComparableQuantity<?> divide(Quantity<?> that) {
        return ScaleHelper.multiplication(this, that,
                (thisValue, thatValue) -> Calculator.of(thisValue).divide(thatValue).peek(), Unit::divide);
    }

    @Override
    public ComparableQuantity<Q> divide(Number divisor) {
        return ScaleHelper.scalarMultiplication(this, thisValue -> Calculator.of(thisValue).divide(divisor).peek());
    }

    @Override
    public ComparableQuantity<?> multiply(Quantity<?> that) {
        return ScaleHelper.multiplication(this, that,
                (thisValue, thatValue) -> Calculator.of(thisValue).multiply(thatValue).peek(), Unit::multiply);
    }

    @Override
    public ComparableQuantity<Q> multiply(Number factor) {
        return ScaleHelper.scalarMultiplication(this, thisValue -> Calculator.of(thisValue).multiply(factor).peek());
    }

    @Override
    public ComparableQuantity<?> inverse() {
        final Number resultValueInThisUnit = Calculator.of(getValue()).reciprocal().peek();
        return Quantities.getQuantity(resultValueInThisUnit, getUnit().inverse(), getScale());
    }

    @Override
    public Quantity<Q> negate() {
        final Number resultValueInThisUnit = Calculator.of(getValue()).negate().peek();
        return Quantities.getQuantity(resultValueInThisUnit, getUnit(), getScale());
    }

    @Override
    public Number getValue() {
        return value;
    }

}

