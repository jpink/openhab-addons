package org.openhab.binding.entsoe.internal.monetary;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Currency;

import javax.measure.Dimension;
import javax.measure.Prefix;
import javax.measure.Quantity;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public abstract class AbstractCurrencyUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> implements CurrencyContext {

    protected final Currency currency;

    protected final int fractionDigits;

    protected final MathContext context;

    protected AbstractCurrencyUnit(@Nullable Prefix prefix, @NonNull Currency currency, int fractionDigits,
            @NonNull MathContext context, @NonNull String symbol, @NonNull String name, @NonNull Dimension dimension) {
        super(prefix, symbol, name, dimension);
        this.fractionDigits = fractionDigits;
        this.currency = currency;
        this.context = context;
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
        return round(value) + " " + this;
    }

    @Override
    public BigDecimal round(@NonNull BigDecimal value) {
        return value.setScale(fractionDigits, getRoundingMode());
    }
}
