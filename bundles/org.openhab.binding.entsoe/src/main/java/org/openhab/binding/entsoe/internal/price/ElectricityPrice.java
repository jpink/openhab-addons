package org.openhab.binding.entsoe.internal.price;

import javax.measure.Unit;
import javax.measure.quantity.Energy;
import java.time.ZonedDateTime;
import java.util.Currency;

/** Data transfer object of electricity price interval with statistics. */
public class ElectricityPrice {
    public final ZonedDateTime start;
    public final ZonedDateTime end;

    //#region Price components
    /** The fixed electricity transfer fee. */
    public final ProductPrice transfer;

    /** The fixed energy tax amount. */
    public final ProductPrice tax;

    /** The current spot price. */
    public final ProductPrice spot;

    /** The fixed sellers margin price. */
    public final ProductPrice margin;
    //#endregion

    /** The total price what consumer has to pay. */
    public final ProductPrice price;

    public final Currency currency;
    public final Unit<Energy> unit;

    //#region Statistics
    /** A daily price index which is comparable to average price. Average price has index of 100. */
    public final int dailyIndex;

    /**
     * The cheapest daily price interval has rank 1, the second cheapest has rank 2, etc. The most expensive interval
     * has the same value as count of intervals in a day.
     */
    public final int dailyRank;

    /** A price index of the whole period which is comparable to average price. Average price has index of 100. */
    public final int totalIndex;

    /**
     * The cheapest price interval has rank 1, the second cheapest has rank 2, etc. The most expensive interval has the
     * same value as total count of intervals.
     */
    public final int totalRank;
    //#endregion

    public ElectricityPrice(ZonedDateTime start, ZonedDateTime end, ProductPrice transfer, ProductPrice tax,
            ProductPrice spot, ProductPrice margin, Currency currency, Unit<Energy> unit, int dailyIndex, int dailyRank,
            int totalIndex, int totalRank) {
        this.start = start;
        this.end = end;
        this.transfer = transfer;
        this.tax = tax;
        this.spot = spot;
        this.margin = margin;
        this.price = null;//TODO transfer + tax + spot + margin;
        this.currency = currency;
        this.unit = unit;
        this.dailyIndex = dailyIndex;
        this.dailyRank = dailyRank;
        this.totalIndex = totalIndex;
        this.totalRank = totalRank;
    }

    public boolean contains(ZonedDateTime time) {
        return start.compareTo(time) <= 0 && time.compareTo(end) < 0;
    }

}
