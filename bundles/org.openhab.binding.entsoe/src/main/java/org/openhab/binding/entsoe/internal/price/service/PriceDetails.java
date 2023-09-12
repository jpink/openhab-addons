package org.openhab.binding.entsoe.internal.price.service;

import org.eclipse.jdt.annotation.NonNullByDefault;

import java.time.ZoneId;

/**
 * The fixed price parameters for price service.
 *
 * @param zone The target zone for date times.
 * @param transfer The fixed electricity transfer fee.
 * @param tax The fixed energy tax amount.
 * @param sellersVatRate Value added tax rate for spot price. Usually the general one.
 * @param margin The fixed sellers margin price.
 */
@NonNullByDefault
public record PriceDetails(ZoneId zone, ProductPrice transfer, ProductPrice tax, VatRate sellersVatRate,
                           ProductPrice margin) {
}