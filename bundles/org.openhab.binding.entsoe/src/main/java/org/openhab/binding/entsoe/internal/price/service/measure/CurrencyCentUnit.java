package org.openhab.binding.entsoe.internal.price.service.measure;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

import javax.measure.Unit;
import javax.measure.UnitConverter;
import java.io.Serial;
import java.util.Currency;
import java.util.Map;

/** <a href="https://en.wikipedia.org/wiki/Centi-">Centi-currency</a> unit. */
public class CurrencyCentUnit extends CurrencyUnit {
    @Serial
    private static final long serialVersionUID = 569439985707470920L;

    private final CurrencyUnit baseUnit;
    private final Map<CurrencyUnit, Integer> baseUnits;

    /**
     * Creates a base unit having the specified symbol, name and dimension.
     *
     * @param currency the symbol of this base unit.
     * @throws IllegalArgumentException if the specified symbol is associated to a different unit.
     * @since 2.0
     */
    public CurrencyCentUnit(@NonNull Currency currency) {
        super(currency, "c", "Centi" + StringUtils.uncapitalize(currency.getDisplayName()));
        baseUnit = new CurrencyUnit(currency);
        baseUnits = Map.of(baseUnit, 2);
    }

    /**
     * Returns the converter from this unit to its unscaled {@link #toSystemUnit System Unit} unit.
     *
     * @return <code>getConverterTo(this.toSystemUnit())</code>
     * @see #toSystemUnit
     */
    @Override
    public UnitConverter getSystemConverter() {
        return CurrencyCentConverter.INSTANCE;
    }

    @Override
    protected CurrencyUnit toSystemUnit() {
        return baseUnit;
    }

    @Override
    public Map<? extends Unit<?>, Integer> getBaseUnits() {
        return baseUnits;
    }

}
