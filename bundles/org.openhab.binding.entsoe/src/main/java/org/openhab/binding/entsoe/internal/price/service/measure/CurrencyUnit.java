package org.openhab.binding.entsoe.internal.price.service.measure;

import org.eclipse.jdt.annotation.NonNull;
import tech.units.indriya.AbstractUnit;
import tech.units.indriya.function.AbstractConverter;

import javax.measure.*;
import java.io.Serial;
import java.util.Currency;
import java.util.Map;

public class CurrencyUnit extends AbstractUnit<Money> {
    @Serial
    private static final long serialVersionUID = 294297405658990958L;

    public final @NonNull Currency currency;

    /**
     * Creates a base unit having the specified symbol, name and dimension.
     *
     * @param currency the symbol of this base unit.
     * @throws IllegalArgumentException if the specified symbol is associated to a different unit.
     * @since 2.0
     */
    public CurrencyUnit(@NonNull Currency currency) {
        this(currency, currency.getSymbol(), currency.getDisplayName());
    }

    protected CurrencyUnit(@NonNull Currency currency, String symbol, String name) {
        super(symbol);
        this.currency = currency;
        setName(name);
    }

    /**
     * Returns the converter from this unit to its unscaled {@link #toSystemUnit System Unit} unit.
     *
     * @return <code>getConverterTo(this.toSystemUnit())</code>
     * @see #toSystemUnit
     */
    @Override
    public UnitConverter getSystemConverter() {
        return AbstractConverter.IDENTITY;
    }

    @Override
    protected Unit<Money> toSystemUnit() {
        return this;
    }

    @Override
    public Map<? extends Unit<?>, Integer> getBaseUnits() {
        return null;
    }

    @Override
    public Dimension getDimension() {
        return CurrencyDimension.INSTANCE;
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
        // TODO Workaround for: java.lang.IllegalArgumentException: Cannot format given Object as a Unit
        //      at tech.units.indriya.format.SimpleUnitFormat$DefaultFormat.format(SimpleUnitFormat.java:907)
        return getSymbol();
    }

}
