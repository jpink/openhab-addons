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
package org.openhab.binding.entsoe.internal.price.service;

import static org.openhab.binding.entsoe.internal.common.Time.*;
import static org.openhab.binding.entsoe.internal.monetary.Monetary.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.measure.Unit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.entsoe.internal.client.EntsoeClient;
import org.openhab.binding.entsoe.internal.client.dto.*;
import org.openhab.binding.entsoe.internal.client.exception.*;
import org.openhab.binding.entsoe.internal.monetary.EnergyPrice;
import org.openhab.binding.entsoe.internal.price.PriceConfig;
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

    public static DailyCache parse(PriceConfig config, Publication publication) throws Bug, CurrencyMismatch {
        return parse(config, config.local(publication.created), publication.timeSeries);
    }

    public static DailyCache parse(PriceConfig config, ZonedDateTime created, TimeSeries series)
            throws Bug, CurrencyMismatch {
        var unit = config.response(series.currency, series.measure);
        try {
            return parse(config, created, series.domain, series.period, unit);
        } catch (Throwable t) {
            throw new Bug(t);
        }
    }

    public static DailyCache parse(PriceConfig config, ZonedDateTime created, String domain, Period period,
            Unit<EnergyPrice> spotUnit) {
        // 1. Create spot prices
        var marginTaxPrice = config.marginTaxPrice();
        var sellersVatRate = marginTaxPrice.vatRate();
        var spotTaxPrices = period.points.stream()
                // Convert units if needed.
                .map(point -> taxPrice(energyPrice(point.amount, spotUnit).to(config.targetUnit), sellersVatRate))
                .toList();
        if (spotTaxPrices.isEmpty())
            throw new IllegalStateException("No data points!");

        // 2. Count total sum values
        var transferTaxPrice = config.transferTaxPrice();
        var taxTaxPrice = config.taxTaxPrice();
        var baseSumValue = bigDecimal(
                transferTaxPrice.sumValue().add(taxTaxPrice.sumValue()).add(marginTaxPrice.sumValue()));
        var totalSumValues = spotTaxPrices.stream().map(spot -> bigDecimal(baseSumValue.add(spot.sumValue()))).toList();

        // 3. Count ranks
        var ranks = totalSumValues.stream().sorted().toList();

        // 4. Count min, average and max
        var minimum = ranks.get(0);
        var average = divide(ranks.stream().reduce(BigDecimal.ZERO, BigDecimal::add), ranks.size());
        var maximum = ranks.get(ranks.size() - 1);

        // 5. Count normalized values (0.0 - 1.0) which can be shown as a percent.
        // https://stats.stackexchange.com/questions/70801/how-to-normalize-data-to-0-1-range
        var divisor = maximum.add(minimum.negate());
        var normalizedPrices = totalSumValues.stream()
                .map(total -> percent(divide(total.add(minimum.negate()), divisor))).toList();

        // 6. Wrap everything up in electricity price.
        var resolution = period.resolution;
        var minutes = resolution.toMinutes();
        var endOffset = config.local(period.timeInterval.start);
        var startOffset = endOffset.minusMinutes(minutes);
        var electricityPrices = period.points.stream().map(point -> {
            var position = point.position;
            var start = startOffset.plusMinutes(position * minutes);
            var end = endOffset.plusMinutes(position * minutes);
            var index = position - 1;
            var spotTaxPrice = spotTaxPrices.get(index);
            var totalPrice = totalSumValues.get(index);
            var rank = ranks.indexOf(totalPrice) + 1;
            var normalized = normalizedPrices.get(index);
            return new ElectricityPrice(start, end, transferTaxPrice, taxTaxPrice, spotTaxPrice, marginTaxPrice,
                    config.energyPrice(totalPrice), rank, normalized, new Holder<>(rank), new Holder<>(normalized));
        }).toList();

        return new DailyCache(created, domain, endOffset, config.local(period.timeInterval.end), resolution,
                electricityPrices, config.energyPrice(minimum), config.energyPrice(average),
                config.energyPrice(maximum));
    }

    private final Logger logger = LoggerFactory.getLogger(PriceService.class);
    private final PriceConfig config;
    private final EntsoeClient client;
    // private final ZoneId zone;

    private DailyCache today;
    private @Nullable DailyCache tomorrow;

    public PriceService(PriceConfig config, EntsoeClient client)
            throws Bug, CurrencyMismatch, InterruptedException, TimeoutException, Unauthorized {
        this.config = config;
        this.client = client;
        today = parse(config, getDayAheadPrices(ZonedDateTime.now()));
    }

    @Override
    public ZonedDateTime start() {
        return today.start();
    }

    @Override
    public ZonedDateTime end() {
        final var tomorrow = this.tomorrow;
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

    private void calculateStatistics() {
        // TODO
    }

    public @Nullable ElectricityPrice currentPrice() throws Bug {
        try {
            var now = ZonedDateTime.now();
            if (contains(now)) {
                if (today.contains(now))
                    return today.currentPrice(now);
                else {
                    final var tomorrow = this.tomorrow;
                    if (tomorrow != null && tomorrow.contains(now)) {
                        logger.debug("Switching tomorrow's prices today.");
                        today = tomorrow;
                        this.tomorrow = null;
                        calculateStatistics();
                        return today.currentPrice(now);
                    } else {
                        logger.warn("Missing tomorrow's prices");
                    }
                }
            } else {
                logger.warn("Data is too old!");
            }
            return null;
        } catch (Throwable t) {
            return bug(t);
        }
    }

    public ZonedDateTime refresh() throws Bug, CurrencyMismatch, InterruptedException, TimeoutException, Unauthorized {
        final var tomorrow = this.tomorrow;
        if (tomorrow == null) {
            if (gone(set(ZonedDateTime.now(), PUBLISHED))) {
                var document = searchDayAheadPrices(today.end());
                if (document instanceof Publication publication) {
                    this.tomorrow = parse(config, publication);
                    calculateStatistics();
                } else {
                    logger.debug("Got no data for tomorrow, but it should be published.");
                }
            } else {
                logger.debug("Waiting for tomorrow's publication.");
            }
            return today.created();
        } else {
            logger.trace("Tomorrow's prices already fetched.");
            return tomorrow.created();
        }
    }

    public Map<String, String> updateProperties(Map<String, String> properties) {
        properties.put("currency", config.responseCurrency.getDisplayName());
        String domain = today.domain();
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
