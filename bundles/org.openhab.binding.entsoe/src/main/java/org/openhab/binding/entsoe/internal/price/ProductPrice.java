package org.openhab.binding.entsoe.internal.price;

import org.eclipse.jdt.annotation.Nullable;

import javax.measure.Unit;
import javax.measure.quantity.Energy;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/**
 * Single or multiple products price. If products have different value added tax rates, then the rate is null.
 *
 * @param price The product price without value added tax.
 * @param vatRate The value added tax rate (0.0 - 1.0) used for the product. May be null if products have
 *         different rates.
 * @param vatAmount The value added tax amount.
 * @param total The product price with tax included.
 * @param currency The price currency.
 * @param unit The price unit.
 */
public record ProductPrice(double price, @Nullable VatRate vatRate, double vatAmount, double total, Currency currency,
                           Unit<Energy> unit) {
    public static ProductPrice fromTotal(final double total, final VatRate vatRate, Currency currency,
            Unit<Energy> unit) {
        return new ProductPrice(vatRate.price(total), vatRate, vatRate.vatFromTotal(total), total, currency, unit);
    }

    private static final NumberFormat FORMAT = NumberFormat.getCurrencyInstance(Locale.ENGLISH);

    @Override
    public String toString() {
        return vatRate == null ? FORMAT.format(total) : FORMAT.format(total) + " " + vatRate;
    }

    public ProductPrice plus(ProductPrice other) {
        if (!Objects.equals(currency, other.currency))
            throw new IllegalArgumentException(
                    "Product price currency " + currency + "isn't same as the other " + other.currency + "!");
        return new ProductPrice(price + other.price, Objects.equals(vatRate, other.vatRate) ? vatRate : null,
                vatAmount + other.vatAmount, total + total, currency, unit);
    }

}
