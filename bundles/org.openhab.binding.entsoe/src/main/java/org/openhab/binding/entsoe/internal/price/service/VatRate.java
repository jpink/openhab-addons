package org.openhab.binding.entsoe.internal.price.service;

import org.eclipse.jdt.annotation.NonNullByDefault;

import java.text.NumberFormat;
import java.util.Locale;

@NonNullByDefault
public record VatRate(double rate) {
    private static final NumberFormat FORMAT = NumberFormat.getPercentInstance(Locale.ENGLISH);

    public VatRate {
        if (rate < 0.0)
            throw new IllegalArgumentException("VAT rate can't be negative!");
        if (rate > 1.0)
            throw new IllegalArgumentException("VAT rate can't be over 100%!");
    }

    public VatRate(int percent) {
        this(percent / 100.0);
    }

    public double price(double total) {
        return total / (1.0 + rate);
    }

    public double vatFromPrice(double price) {
        return price * rate;
    }

    public double vatFromTotal(double total) {
        return total - price(total);
    }

    public double totalFromPrice(double price) {
        return price + vatFromPrice(price);
    }

    @Override
    public String toString() {
        return "VAT " + FORMAT.format(rate);
    }

}
