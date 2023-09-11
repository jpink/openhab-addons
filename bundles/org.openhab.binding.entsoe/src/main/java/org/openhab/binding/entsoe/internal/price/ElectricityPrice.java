package org.openhab.binding.entsoe.internal.price;

import javax.measure.Unit;
import javax.measure.quantity.Energy;
import java.time.ZonedDateTime;

/**
 * Electricity price interval with statistics.
 *
 * @param start
 * @param end
 * @param transfer The fixed electricity transfer fee.
 * @param tax The fixed energy tax amount.
 * @param spot The current spot price.
 * @param margin The fixed sellers margin price.
 * @param price The total price what consumer has to pay.
 * @param unit
 * @param dailyIndex A daily price index which is comparable to average price. Average price has index of 100.
 * @param dailyRank The cheapest daily price interval has rank 1, the second cheapest has rank 2, etc. The most
 *         expensive interval has the same value as count of intervals in a day.
 * @param totalIndex A price index of the whole period which is comparable to average price. Average price has
 *         index of 100.
 * @param totalRank The cheapest price interval has rank 1, the second cheapest has rank 2, etc. The most
 *         expensive interval has the same value as total count of intervals.
 */
public record ElectricityPrice(ZonedDateTime start, ZonedDateTime end, ProductPrice transfer, ProductPrice tax,
                               ProductPrice spot, ProductPrice margin, ProductPrice price, Unit<Energy> unit,
                               int dailyIndex, int dailyRank, int totalIndex, int totalRank) {
    public ElectricityPrice(ZonedDateTime start, ZonedDateTime end, ProductPrice transfer, ProductPrice tax,
            ProductPrice spot, ProductPrice margin, Unit<Energy> unit, int dailyIndex, int dailyRank, int totalIndex,
            int totalRank) {
        this(start, end, transfer, tax, spot, margin, transfer.plus(tax).plus(spot).plus(margin), unit, dailyIndex,
                dailyRank, totalIndex, totalRank);
    }

    public boolean contains(ZonedDateTime time) {
        return start.compareTo(time) <= 0 && time.compareTo(end) < 0;
    }

}
