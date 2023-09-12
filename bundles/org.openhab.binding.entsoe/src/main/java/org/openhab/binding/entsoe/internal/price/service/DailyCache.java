package org.openhab.binding.entsoe.internal.price.service;

import org.eclipse.jdt.annotation.NonNullByDefault;

import javax.measure.Unit;
import javax.measure.quantity.Energy;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.DoubleSummaryStatistics;
import java.util.List;

@NonNullByDefault
public record DailyCache(ZonedDateTime created, String domain, ZonedDateTime start, ZonedDateTime end,
                         Duration resolution, Currency currency, Unit<Energy> measure, List<ElectricityPrice> prices,
                         DoubleSummaryStatistics statistics) implements Interval {

    public ElectricityPrice currentPrice(ZonedDateTime now) {
        return prices.stream().filter(price -> price.contains(now)).findFirst().orElseThrow();
    }

}