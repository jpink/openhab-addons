package org.openhab.binding.entsoe.internal.monetary;

import static org.openhab.binding.entsoe.internal.monetary.GenericDimension.ENERGY;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;

import org.eclipse.jdt.annotation.NonNull;

public abstract class AbstractCurrencyUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> implements CurrencyContext {

    private final Currency currency;

    private final int fractionDigits;

    private final MathContext context;

    private final NumberFormat numberFormat;

    /*
     * protected AbstractCurrencyUnit(@NonNull Currency currency) {
     * this(currency, currency.getDefaultFractionDigits());
     * }
     * 
     * protected AbstractCurrencyUnit(@NonNull Currency currency, int fractionDigits) {
     * this(currency, new MathContext(fractionDigits));
     * }
     * 
     * protected AbstractCurrencyUnit(@NonNull Currency currency, @NonNull MathContext context) {
     * this.currency = currency;
     * this.context = context;
     * }
     */

    protected AbstractCurrencyUnit(@NonNull Currency currency, int fractionDigits, @NonNull MathContext context,
            @NonNull String symbol, @NonNull String name, @NonNull Dimension dimension) {
        super(symbol, name, dimension);
        this.fractionDigits = fractionDigits;
        this.currency = currency;
        this.context = context;
        this.numberFormat = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
        numberFormat.setCurrency(currency);
        if (fractionDigits > -1) {
            numberFormat.setMaximumFractionDigits(fractionDigits);
            numberFormat.setMinimumFractionDigits(fractionDigits);
        }
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public int getFractionDigits() {
        return fractionDigits;
    }

    @Override
    public MathContext getMathContext() {
        return context;
    }

    @Override
    public String format(@NonNull BigDecimal value) {
        return numberFormat.format(value);
    }

    @Override
    public BigDecimal round(@NonNull BigDecimal value) {
        return value.setScale(fractionDigits, getRoundingMode());
    }

    @Override
    public Unit<?> divide(Unit<?> divisor) {
        if (ENERGY.equals(divisor.getDimension()))
            return EnergyCurrency.ofQuotient(this, divisor);
        throw new UnsupportedOperationException(divisor.toString());
    }
}
