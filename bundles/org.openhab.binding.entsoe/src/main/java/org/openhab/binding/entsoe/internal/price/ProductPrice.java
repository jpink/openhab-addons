package org.openhab.binding.entsoe.internal.price;

import org.eclipse.jdt.annotation.Nullable;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class ProductPrice {
    public static ProductPrice fromTotal(final double total, final VatRate vatRate, Currency currency) {
        return new ProductPrice(vatRate.price(total), vatRate, vatRate.vatFromTotal(total), total, currency);
    }

    private static final NumberFormat FORMAT = NumberFormat.getCurrencyInstance(Locale.ENGLISH);

    /** The product price without value added tax. */
    final double price;

    /** The value added tax rate (0.0 - 1.0) used for the product. */
    final @Nullable VatRate vatRate;

    /** The value added tax amount. */
    final double vatAmount;

    /** The product price with tax included. */
    final double total;

    /** The price currency. */
    final Currency currency;

    private ProductPrice(double price, @Nullable VatRate vatRate, double vatAmount, double total, Currency currency) {
        this.price = price;
        this.vatRate = vatRate;
        this.vatAmount = vatAmount;
        this.total = total;
        this.currency = currency;
    }

    @Override
    public String toString() {
        return vatRate == null ? FORMAT.format(total) : FORMAT.format(total) + " " + vatRate;
    }

}
