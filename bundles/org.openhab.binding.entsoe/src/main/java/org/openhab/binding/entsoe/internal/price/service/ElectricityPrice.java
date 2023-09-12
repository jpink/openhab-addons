package org.openhab.binding.entsoe.internal.price.service;

import org.eclipse.jdt.annotation.NonNullByDefault;

import java.time.ZonedDateTime;

/**
 * Electricity price interval.
 *
 * @param start
 * @param end
 * @param transfer The fixed electricity transfer fee.
 * @param tax The fixed energy tax amount.
 * @param spot The current spot price.
 * @param margin The fixed sellers margin price.
 * @param total The total price what consumer has to pay.
 * @param dailyRank The cheapest daily price interval has rank 1, the second cheapest has rank 2, etc. The most
 *         expensive interval has the same value as count of intervals in a day.
 * @param dailyNormalized A daily normalized price which is between 0.0 and 1.0.
 * @param futureRank The cheapest daily price interval has rank 1, the second cheapest has rank 2, etc. The most
 *         expensive interval has the same value as count of intervals in the future.
 * @param futureNormalized A future normalized price which is between 0.0 and 1.0.
 */
@NonNullByDefault
public record ElectricityPrice(ZonedDateTime start, ZonedDateTime end, ProductPrice transfer, ProductPrice tax,
                               ProductPrice spot, ProductPrice margin, ProductPrice total, int dailyRank,
                               double dailyNormalized, Holder<Integer> futureRank, Holder<Double> futureNormalized)
        implements Interval {

}
