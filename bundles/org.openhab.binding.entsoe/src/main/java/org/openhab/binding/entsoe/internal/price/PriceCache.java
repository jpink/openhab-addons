package org.openhab.binding.entsoe.internal.price;

import org.eclipse.jdt.annotation.Nullable;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import javax.measure.Unit;
import javax.measure.quantity.Energy;

public class PriceCache {
    private static class DailyCache {
        private static class Interval {
            final ZonedDateTime start;
            final ZonedDateTime end;
            final Double price;

            Interval(ZonedDateTime start, ZonedDateTime end, Double price) {
                this.start = start;
                this.end = end;
                this.price = price;
            }

        }

        final ZonedDateTime start;
        //final ZonedDateTime end;
        //final Double average;

        DailyCache(ZonedDateTime start, Duration resolution, List<Float> prices, Float priceAddition) {
            this.start = start;
        }

    }

    /** Additions to spot price: transfer price, tax and margin price. */
    private final Float addition;
    private final Currency currency;
    private final Unit<Energy> source;
    private final Unit<Energy> target;
    private DailyCache today;
    private @Nullable DailyCache tomorrow;
    private List<ElectricityPrice> prices = Collections.emptyList();

    public PriceCache(ZonedDateTime start, Duration resolution, List<Float> prices, Float priceAddition,
            Currency currency, Unit<Energy> source, Unit<Energy> target) {
        this.addition = priceAddition;
        this.currency = currency;
        this.source = source;
        this.target = target;
        //today = new DailyCache();
    }

    public @Nullable ElectricityPrice getPrice(ZonedDateTime time) {
        return prices.stream().filter((interval) -> interval.contains(time)).findFirst().orElse(null);
    }

    public List<ElectricityPrice> getPrices() {
        return prices;
    }

}
