package org.openhab.binding.entsoe.internal.price.service.measure;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.unit.BaseUnit;
import tech.units.indriya.unit.ProductUnit;
import tech.units.indriya.unit.Units;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static tech.units.indriya.unit.UnitDimension.NONE;

/** This class is copy of tech.units.indriya.unit.UnitDimension because it is final. */
public class CurrencyDimension implements Dimension, Serializable {
    public static final CurrencyDimension INSTANCE = new CurrencyDimension('¤');
    private static final Logger LOGGER = Logger.getLogger(CurrencyDimension.class.getName());
    @Serial
    private static final long serialVersionUID = 4742526755432120634L;

    /**
     * Holds the pseudo unit associated to this dimension.
     */
    private final Unit<?> pseudoUnit;

    /**
     * Returns the dimension for the specified quantity type by aggregating the results from the default
     * {@link javax.measure.spi.SystemOfUnits SystemOfUnits} or <code>null</code> if the specified quantity is unknown.
     *
     * @param quantityType the quantity type.
     * @return the dimension for the quantity type or <code>null</code>.
     * @since 1.1
     */
    public static <Q extends Quantity<Q>> Dimension of(Class<Q> quantityType) {
        // TODO: Track services and aggregate results (register custom types)
        Unit<Q> siUnit = Units.getInstance().getUnit(quantityType);
        if (siUnit == null && LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Quantity type: " + quantityType + " unknown");
        }
        return (siUnit != null) ? siUnit.getDimension() : null;
    }

    /**
     * Returns the unit dimension having the specified symbol.
     *
     * @param symbol the associated symbol.
     */
    @SuppressWarnings("rawtypes")
    private CurrencyDimension(char symbol) {
        pseudoUnit = new BaseUnit("[" + symbol + ']', NONE);
    }

    /**
     * Constructor from pseudo-unit (not visible).
     *
     * @param pseudoUnit the pseudo-unit.
     */
    private CurrencyDimension(Unit<?> pseudoUnit) {
        this.pseudoUnit = pseudoUnit;
    }

    /**
     * Returns the product of this dimension with the one specified. If the specified dimension is not a
     * <code>UnitDimension</code>, then
     * <code>that.multiply(this)</code> is returned.
     *
     * @param that the dimension multiplicand.
     * @return <code>this * that</code>
     * @since 1.0
     */
    public Dimension multiply(Dimension that) {
        return that instanceof CurrencyDimension ? this.multiply((CurrencyDimension) that) : that.multiply(this);
    }

    /**
     * Returns the product of this dimension with the one specified.
     *
     * @param that the dimension multiplicand.
     * @return <code>this * that</code>
     * @since 1.0
     */
    private CurrencyDimension multiply(CurrencyDimension that) {
        return new CurrencyDimension(this.pseudoUnit.multiply(that.pseudoUnit));
    }

    /**
     * Returns the quotient of this dimension with the one specified. If the specified dimension is not a
     * <code>UnitDimension</code>, then
     * <code>that.divide(this).pow(-1)</code> is returned.
     *
     * @param that the dimension divisor.
     * @return <code>this / that</code>
     * @since 1.0
     */
    public Dimension divide(Dimension that) {
        return that instanceof CurrencyDimension ? this.divide((CurrencyDimension) that) : that.divide(this).pow(-1);
    }

    /**
     * Returns the quotient of this dimension with the one specified.
     *
     * @param that the dimension divisor.
     * @return <code>this / that</code>
     * @since 1.0
     */
    private CurrencyDimension divide(CurrencyDimension that) {
        return new CurrencyDimension(ProductUnit.ofQuotient(pseudoUnit, that.pseudoUnit));
    }

    /**
     * Returns this dimension raised to an exponent.
     *
     * @param n the exponent.
     * @return the result of raising this dimension to the exponent.
     * @since 1.0
     */
    public CurrencyDimension pow(int n) {
        return new CurrencyDimension(pseudoUnit.pow(n));
    }

    /**
     * Returns the given root of this dimension.
     *
     * @param n the root's order.
     * @return the result of taking the given root of this dimension.
     * @throws ArithmeticException if <code>n == 0</code>.
     * @since 1.0
     */
    public CurrencyDimension root(int n) {
        return new CurrencyDimension(pseudoUnit.root(n));
    }

    /**
     * Returns the fundamental (base) dimensions and their exponent whose product is this dimension or <code>null</code>
     * if this dimension is a fundamental dimension.
     *
     * @return the mapping between the base dimensions and their exponent.
     * @since 1.0
     */
    @SuppressWarnings("rawtypes")
    public Map<? extends Dimension, Integer> getBaseDimensions() {
        Map<? extends Unit, Integer> pseudoUnits = pseudoUnit.getBaseUnits();
        if (pseudoUnits == null) {
            return null;
        }
        final Map<CurrencyDimension, Integer> baseDimensions = new HashMap<>();
        for (Map.Entry<? extends Unit, Integer> entry : pseudoUnits.entrySet()) {
            baseDimensions.put(new CurrencyDimension(entry.getKey()), entry.getValue());
        }
        return baseDimensions;
    }

    @Override
    public String toString() {
        return pseudoUnit.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CurrencyDimension other) {
            return Objects.equals(pseudoUnit, other.pseudoUnit);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pseudoUnit);
    }

}
