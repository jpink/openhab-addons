package org.openhab.binding.entsoe.internal.price.service;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.entsoe.internal.client.EntsoeClient;
import org.openhab.binding.entsoe.internal.client.dto.Area;
import org.openhab.binding.entsoe.internal.client.dto.MarketDocument;
import org.openhab.binding.entsoe.internal.client.dto.Publication;
import org.openhab.binding.entsoe.internal.client.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/** Electricity Price Service */
@NonNullByDefault
public class PriceService implements Interval {

    private final Logger logger = LoggerFactory.getLogger(PriceService.class);
    private final PriceDetails details;
    private final EntsoeClient client;
    private final ZoneId zone;

    private DailyCache today;
    private @Nullable DailyCache tomorrow;

    public PriceService(PriceDetails details, EntsoeClient client)
            throws Bug, InterruptedException, TimeoutException, Unauthorized {
        this.details = details;
        this.client = client;
        this.zone = details.zone();
        today = getDayAheadPrices(ZonedDateTime.now()).toDailyCache(details);
    }

    @Override
    public ZonedDateTime start() {
        return today.start();
    }

    @Override
    public ZonedDateTime end() {
        return tomorrow == null ? today.end() : tomorrow.end();
    }

    public Duration resolution() {
        return today.resolution();
    }

    private <T> T bug(Throwable t) throws Bug {
        logger.error("Please report the following bug!", t);
        throw new Bug(t);
    }

    private MarketDocument searchDayAheadPrices(ZonedDateTime start)
            throws Bug, InterruptedException, TimeoutException, Unauthorized {
        try {
            return client.getDayAheadPrices(start, start.plusDays(1));
        } catch (ExecutionException | InvalidParameter | TooLong | TooMany | TooShort | UnknownResponse e) {
            return bug(e);
        }
    }

    private Publication getDayAheadPrices(ZonedDateTime start)
            throws Bug, InterruptedException, TimeoutException, Unauthorized {
        try {
            return (Publication) searchDayAheadPrices(start);
        } catch (ClassCastException e) {
            return bug(e);
        }
    }

    public @Nullable ElectricityPrice currentPrice() throws Bug {
        try {
            var now = ZonedDateTime.now();
            if (contains(now)) {
                return today.contains(now)
                        ? today.currentPrice(now)
                        : Objects.requireNonNull(tomorrow).currentPrice(now);
            }
            return null;
        } catch (Throwable t) {
            return bug(t);
        }
    }

    public ZonedDateTime refresh() throws Bug, InterruptedException, TimeoutException, Unauthorized {
        //TODO check current status
        var publication = getDayAheadPrices(ZonedDateTime.now());
        today = publication.toDailyCache(details);
        return publication.created.withZoneSameInstant(zone);
    }

    public Map<String, String> updateProperties(Map<String, String> properties) {
        properties.put("currency", today.currency().getDisplayName());
        String domain = today.domain();
        try {
            domain = Area.valueOf(domain).meaning;
        } catch (IllegalArgumentException ignored) {
        }
        properties.put("domain", domain);
        properties.put("measure", today.measure().toString());
        properties.put("resolution", resolution().toMinutes() + " min");
        properties.put("start", start().format(DateTimeFormatter.ISO_TIME));
        return properties;
    }

}
