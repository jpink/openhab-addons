package org.openhab.binding.entsoe.internal.monetary;

import static org.openhab.binding.entsoe.internal.monetary.GenericDimension.ENERGY;

import java.math.MathContext;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import javax.measure.Prefix;
import javax.measure.Unit;
import javax.measure.quantity.Energy;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class CurrencyUnit extends AbstractCurrencyUnit<Money> {
    public CurrencyUnit(@NonNull Currency currency) {
        this(currency, currency.getDefaultFractionDigits());
    }

    public CurrencyUnit(@NonNull Currency currency, int fractionDigits) {
        this(null, currency, fractionDigits, MathContext.UNLIMITED);
    }

    public CurrencyUnit(@NonNull Prefix prefix, @NonNull Currency currency) {
        this(prefix, currency, currency.getDefaultFractionDigits(), MathContext.UNLIMITED);
    }

    public CurrencyUnit(@NonNull Prefix prefix, @NonNull Currency currency, int fractionDigits) {
        this(prefix, currency, fractionDigits, MathContext.UNLIMITED);
    }

    public CurrencyUnit(@Nullable Prefix prefix, @NonNull Currency currency, int fractionDigits,
            @NonNull MathContext context) {
        super(prefix, currency, fractionDigits, context, prefix == null ? currency.getSymbol() : prefix.getSymbol(),
                prefix == null ? currency.getDisplayName()
                        : prefix.getName().toLowerCase(Locale.ROOT) + currency.getDisplayName(),
                GenericDimension.MONETARY);
    }

    @Override
    public CurrencyUnit getSystemUnit() {
        return new CurrencyUnit(currency);
    }

    @Override
    public Map<? extends Unit<?>, Integer> getBaseUnits() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Unit<?> divide(Unit<?> divisor) {
        if (ENERGY.equals(divisor.getDimension()))
            return new EnergyCurrency(this, (Unit<Energy>) divisor);
        throw new UnsupportedOperationException(divisor.toString());
    }

    /**
     * Returns a new unit equal to this unit prefixed by the specified {@code prefix}.
     *
     * @param prefix the prefix to apply on this unit.
     * @return the unit with the given prefix applied.
     * @since 2.0
     */
    @Override
    public Unit<Money> prefix(Prefix prefix) {
        return new CurrencyUnit(prefix, currency, fractionDigits, context);
    }
}
