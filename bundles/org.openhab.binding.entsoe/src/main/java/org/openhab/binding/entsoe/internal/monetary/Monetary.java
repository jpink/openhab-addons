package org.openhab.binding.entsoe.internal.monetary;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.spi.SystemOfUnits;
import java.util.Set;

/** Monetary unit system which doesn't support currency exchanges. */
public class Monetary implements SystemOfUnits {
    /**
     * @return a name
     */
    @Override
    public String getName() {
        return "Monetary";
    }

    /**
     * Returns the default unit for the specified quantity or {@code null} if none is defined for the given quantity in
     * this unit system.
     *
     * @param quantityType the quantity type.
     * @return the unit for the specified quantity.
     */
    @Override
    public <Q extends Quantity<Q>> Unit<Q> getUnit(Class<Q> quantityType) {
        return null;
    }

    /**
     * Returns a unit with the given {@linkplain String string} representation or {@code null} if none is found in this
     * unit system.
     *
     * @param string the string representation of a unit, not {@code null}.
     * @return the unit with the given string representation.
     * @since 2.0
     */
    @Override
    public Unit<?> getUnit(String string) {
        return null;
    }

    /**
     * Returns a read only view over the units explicitly defined by this system. This includes the base and derived
     * units which are assigned a special name and symbol. This set does not include new units created by arithmetic or
     * other operations.
     *
     * @return the defined collection of units.
     */
    @Override
    public Set<? extends Unit<?>> getUnits() {
        return null;
    }

    /**
     * Returns the units defined in this system having the specified dimension (convenience method).
     *
     * @param dimension the dimension of the units to be returned.
     * @return the collection of units of specified dimension.
     */
    @Override
    public Set<? extends Unit<?>> getUnits(Dimension dimension) {
        return null;
    }

}
