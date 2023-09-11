package org.openhab.binding.entsoe.internal.price.service;

import org.eclipse.jdt.annotation.NonNullByDefault;

import javax.measure.Unit;
import javax.measure.quantity.Energy;
import java.time.ZoneId;

/**
 * The fixed price details.
 *
 * @param targetZone The target zone for date times.
 * @param transfer The fixed electricity transfer fee.
 * @param tax The fixed energy tax amount.
 * @param targetUnit The target unit for prices.
 * @param spotVatRate Value added tax rate for spot price. Usually the general one.
 * @param margin The fixed sellers margin price.
 */
@NonNullByDefault
public record PriceDetails(ZoneId targetZone, ProductPrice transfer, ProductPrice tax, Unit<Energy> targetUnit,
                           VatRate spotVatRate, ProductPrice margin) {
}
