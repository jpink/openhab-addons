package org.openhab.binding.entsoe.internal.price;

import javax.measure.Unit;
import javax.measure.quantity.Energy;
import java.time.Duration;
import java.util.Currency;

/** The fixed price details. */
public class PriceDetails {
    public final Duration resolution;
    public final Currency currency;
    public final Unit<Energy> unit;

    /** The fixed electricity transfer fee. */
    public final ProductPrice transfer;

    /** The fixed energy tax amount. */
    public final ProductPrice tax;

    /** The fixed sellers margin price. */
    public final ProductPrice margin;

    /** Value added tax rate for spot price. Usually the general one. */
    public final VatRate spotVatRate;

    public PriceDetails(Duration resolution, Currency currency, Unit<Energy> unit, ProductPrice transfer,
            ProductPrice tax, ProductPrice margin, VatRate spotVatRate) {
        this.resolution = resolution;
        this.currency = currency;
        this.unit = unit;
        this.transfer = transfer;
        this.tax = tax;
        this.margin = margin;
        this.spotVatRate = spotVatRate;
    }

}
