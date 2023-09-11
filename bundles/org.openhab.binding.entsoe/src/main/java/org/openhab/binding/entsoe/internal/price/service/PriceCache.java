package org.openhab.binding.entsoe.internal.price.service;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.entsoe.internal.client.dto.Publication;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@NonNullByDefault
public class PriceCache {
    private record DailyCache(ZonedDateTime start, ZonedDateTime end, ProductPrice average) {
        static DailyCache of(PriceDetails details, Publication publication) {
            var zone = details.targetZone();
            var timeInterval = publication.timeSeries.period.timeInterval;
            var start = timeInterval.start.withZoneSameInstant(zone);
            var end = timeInterval.end.withZoneSameInstant(zone);

            throw new NotImplementedException("");
        }

        private record Interval(ZonedDateTime start, ZonedDateTime end, Double price) {
        }

    }

    private final PriceDetails details;
    private DailyCache today;
    private @Nullable DailyCache tomorrow;
    private List<ElectricityPrice> prices = Collections.emptyList();

    public PriceCache(PriceDetails details, Publication publication) {
        this.details = details;
        today = DailyCache.of(details, publication);
    }

    public void add(Publication publication) {
        tomorrow = DailyCache.of(details, publication);
    }

    public @Nullable ElectricityPrice getPrice(ZonedDateTime time) {
        return prices.stream().filter((interval) -> interval.contains(time)).findFirst().orElse(null);
    }

    public List<ElectricityPrice> getPrices() {
        return prices;
    }

}
