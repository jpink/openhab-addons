/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.electric.internal.handler.price.service;

import static org.openhab.binding.electric.common.Log.debug;
import static org.openhab.binding.electric.common.Time.convert;
import static org.openhab.binding.electric.common.Time.evenHour;
import static org.openhab.binding.electric.common.Time.gone;
import static org.openhab.binding.electric.common.Time.set;

import java.time.Duration;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.electric.internal.handler.entsoe.EntsoeClient;
import org.openhab.binding.electric.internal.handler.entsoe.dto.Area;
import org.openhab.binding.electric.internal.handler.entsoe.dto.Interval;
import org.openhab.binding.electric.internal.handler.entsoe.dto.MarketDocument;
import org.openhab.binding.electric.internal.handler.entsoe.dto.Publication;
import org.openhab.binding.electric.internal.handler.entsoe.exception.InvalidParameter;
import org.openhab.binding.electric.internal.handler.entsoe.exception.TooLong;
import org.openhab.binding.electric.internal.handler.entsoe.exception.TooMany;
import org.openhab.binding.electric.internal.handler.entsoe.exception.TooShort;
import org.openhab.binding.electric.internal.handler.entsoe.exception.Unauthorized;
import org.openhab.binding.electric.internal.handler.entsoe.exception.UnknownResponse;
import org.openhab.binding.electric.internal.handler.price.PriceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Electricity price service
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class PriceService implements Interval {
    /**
     * An about time when tomorrow prices are published.
     *
     * @see <a href="https://sahko.tk/">Samuel Lehtonen</a>
     */
    public static final OffsetTime PUBLISHED = OffsetTime.of(11, 45, 0, 0, ZoneOffset.UTC);
    private static final Duration ENOUGH_DATA = Duration.ofDays(1);

    private final Logger logger = LoggerFactory.getLogger(PriceService.class);
    private final PriceConfig config;
    private final EntsoeClient client;
    private PriceCache cache;

    public PriceService(PriceConfig config, EntsoeClient client)
            throws Bug, CurrencyMismatch, InterruptedException, TimeoutException, Unauthorized {
        this.config = config;
        this.client = client;
        cache = new PriceCache(config, getDayAheadPrices(evenHour()));
    }

    @Override
    public ZonedDateTime start() {
        return cache.start();
    }

    @Override
    public ZonedDateTime end() {
        return cache.end();
    }

    public Duration resolution() {
        return cache.resolution;
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
            var document = searchDayAheadPrices(start);
            if (document instanceof Publication publication) {
                return publication;
            }
            debug(logger, document);
            document = searchDayAheadPrices(start.minusDays(1));
            if (document instanceof Publication publication) {
                return publication;
            }
            debug(logger, document);
            return (Publication) searchDayAheadPrices(start.minusDays(2));
        } catch (ClassCastException e) {
            return bug(e);
        }
    }

    public @Nullable ElectricityPrice currentPrice() throws Bug {
        try {
            var now = ZonedDateTime.now();
            if (contains(now)) {
                return cache.currentPrice(now);
            } else {
                logger.warn("Data is too old!");
                return null;
            }
        } catch (Exception e) {
            return bug(e);
        }
    }

    public ZonedDateTime refresh() throws Bug, CurrencyMismatch, InterruptedException, TimeoutException, Unauthorized {
        var now = ZonedDateTime.now();
        if (contains(now) && ENOUGH_DATA.compareTo(Duration.between(now, end())) < 0) {
            logger.trace("Enough data already fetched.");
        } else {
            if (gone(set(now, PUBLISHED))) {
                var document = searchDayAheadPrices(end());
                if (document instanceof Publication publication) {
                    cache = new PriceCache(config, publication);
                } else {
                    debug(logger, document);
                }
            } else {
                logger.debug("Waiting for tomorrow's publication.");
            }
        }
        return cache.zonedCreated;
    }

    public Map<String, String> updateProperties(Map<String, String> properties) {
        properties.put("currency", config.responseCurrency.getDisplayName());
        String domain = cache.domain;
        try {
            domain = Area.valueOf(domain).meaning;
        } catch (IllegalArgumentException ignored) {
        }
        properties.put("domain", domain);
        properties.put("measure", config.responseMeasure.toString());
        properties.put("published", set(convert(ZonedDateTime.now(), config.zone), PUBLISHED).toString());
        properties.put("resolution", resolution().toMinutes() + " min");
        properties.put("start", start().format(DateTimeFormatter.ISO_TIME));
        properties.put("unit", config.spotUnit.toString());
        return properties;
    }
}
