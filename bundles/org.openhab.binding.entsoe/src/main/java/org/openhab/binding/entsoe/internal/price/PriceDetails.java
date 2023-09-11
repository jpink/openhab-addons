package org.openhab.binding.entsoe.internal.price;

import java.time.Duration;

/**
 * The fixed price details.
 *
 * @param resolution The period resolution. Usually a one hour or 15 minutes.
 * @param transfer The fixed electricity transfer fee.
 * @param tax The fixed energy tax amount.
 * @param margin The fixed sellers margin price.
 * @param spotVatRate Value added tax rate for spot price. Usually the general one.
 */
public record PriceDetails(Duration resolution, ProductPrice transfer, ProductPrice tax, ProductPrice margin,
                           VatRate spotVatRate) {
}
