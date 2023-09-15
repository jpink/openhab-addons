package org.openhab.binding.entsoe.internal.monetary;

import java.math.MathContext;
import java.util.Currency;

import org.eclipse.jdt.annotation.NonNull;

public class CurrencyUnit extends AbstractCurrencyUnit<Money> {
    public CurrencyUnit(@NonNull Currency currency) {
        this(currency, currency.getDefaultFractionDigits());
    }

    public CurrencyUnit(@NonNull Currency currency, int fractionDigits) {
        this(currency, fractionDigits, MathContext.UNLIMITED, currency.getSymbol(), currency.getDisplayName());
    }

    protected CurrencyUnit(@NonNull Currency currency, int fractionDigits, @NonNull MathContext context,
            @NonNull String symbol, @NonNull String name) {
        super(currency, fractionDigits, context, symbol, name, GenericDimension.MONETARY);
    }
}
