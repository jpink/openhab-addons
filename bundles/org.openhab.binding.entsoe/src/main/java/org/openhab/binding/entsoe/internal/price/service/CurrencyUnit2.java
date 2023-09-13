package org.openhab.binding.entsoe.internal.price.service;

import org.eclipse.jdt.annotation.NonNullByDefault;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * @param currency Base currency
 * @param inCents The integer part is cents
 */
@NonNullByDefault
public record CurrencyUnit2(Currency currency, boolean inCents) {
    @NonNullByDefault
    public interface Converter {
        double convert(double value);

    }

    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.ENGLISH);

    public String format(double value) {
        return FORMAT.format(value) + " " + (inCents() ? "c" : currency().getSymbol());
    }

    public Converter getConverterTo(Currency that) {
        return getConverterTo(new CurrencyUnit2(that, false));
    }

    public Converter getConverterTo(CurrencyUnit2 that) {
        if (!currency.equals(that.currency))
            throw new IllegalArgumentException("Unable exchange " + currency + " to " + that.currency + "!");
        if (inCents == that.inCents)
            return value -> value;
        if (inCents)
            return value -> value * 100.0;
        else
            return value -> value / 100.0;
    }

}
