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

import static org.openhab.binding.electric.internal.imp.common.Json.DURATION;
import static org.openhab.binding.electric.internal.imp.common.Json.LOCAL_DATE_TIME;
import static org.openhab.binding.electric.internal.imp.common.Json.STRING;
import static org.openhab.binding.electric.internal.imp.common.Json.ZONE;
import static org.openhab.binding.electric.internal.imp.monetary.Monetary.bigDecimal;
import static org.openhab.binding.electric.internal.imp.monetary.Monetary.divide;
import static org.openhab.binding.electric.internal.imp.monetary.Monetary.energyPrice;
import static org.openhab.binding.electric.internal.imp.monetary.Monetary.percent;
import static org.openhab.binding.electric.internal.imp.monetary.Monetary.taxPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.IntStream;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.internal.handler.entsoe.dto.Interval;
import org.openhab.binding.electric.internal.handler.entsoe.dto.Publication;
import org.openhab.binding.electric.internal.handler.price.PriceConfig;
import org.openhab.binding.electric.internal.imp.monetary.EnergyPrice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Cached available price data.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class PriceCache implements Interval {
    public static GsonBuilder builder() {
        return new GsonBuilder().registerTypeAdapter(Duration.class, DURATION)
                .registerTypeAdapter(LocalDateTime.class, LOCAL_DATE_TIME)
                .registerTypeAdapter(new TypeToken<Unit<Dimensionless>>() {
                }.getType(), STRING).registerTypeAdapter(new TypeToken<Unit<EnergyPrice>>() {
                }.getType(), STRING).registerTypeAdapterFactory(ZONE);
    }

    private static final Gson GSON = builder().create();

    public final transient PriceConfig config;
    public final ZoneId zone;
    public final transient ZonedDateTime zonedCreated, zonedStart, zonedEnd;
    public final LocalDateTime created, start, end;
    public final String domain;
    public final Duration resolution;
    public final double generalVat;
    public final double sellerVat;
    public final Unit<EnergyPrice> spotMeasure, targetUnit;
    public final BigDecimal transfer, tax, margin;
    public final List<BigDecimal> spot, prices;
    public final BigDecimal min, avg, max;
    public final List<BigDecimal> normalized;
    public final transient List<ElectricityPrice> electricityPrices;

    public PriceCache(PriceConfig config, Publication publication) throws CurrencyMismatch {
        this.config = config;

        // 1. Set metadata
        zone = config.zone;
        zonedCreated = config.local(publication.created);
        created = zonedCreated.toLocalDateTime();
        var timeInterval = publication.timeInterval;
        zonedStart = config.local(timeInterval.start);
        zonedEnd = config.local(timeInterval.end);
        start = zonedStart.toLocalDateTime();
        end = zonedEnd.toLocalDateTime();
        var timeSeries = publication.timeSeries;
        var firstSeries = timeSeries.get(0);
        domain = firstSeries.domain;
        resolution = firstSeries.period.resolution;
        generalVat = config.general / 100.0;
        sellerVat = config.seller / 100.0;
        spotMeasure = config.response(firstSeries.currency, firstSeries.measure);
        targetUnit = config.targetUnit;
        transfer = config.transfer;
        tax = config.tax;
        margin = config.margin;

        // 2. Convert spot prices
        var converter = spotMeasure.getConverterTo(targetUnit);
        spot = timeSeries.stream().flatMap(series -> series.period.points.stream())
                .map(point -> bigDecimal(converter.convert(point.amount))).toList();

        // 3. Create spot prices
        var marginTaxPrice = config.marginTaxPrice();
        var sellerVatRate = marginTaxPrice.vatRate();
        var spotTaxPrices = spot.stream().map(spot -> taxPrice(energyPrice(spot, targetUnit), sellerVatRate)).toList();

        // 4. Count prices what consumer pay (total sum values)
        var transferTaxPrice = config.transferTaxPrice();
        var taxTaxPrice = config.taxTaxPrice();
        var baseSumValue = bigDecimal(
                transferTaxPrice.sumValue().add(taxTaxPrice.sumValue()).add(marginTaxPrice.sumValue()));
        prices = spotTaxPrices.stream().map(spot -> bigDecimal(baseSumValue.add(spot.sumValue()))).toList();

        // 5. Count ranks
        var ranks = prices.stream().sorted().toList();

        // 6. Count min, average and max
        min = ranks.get(0);
        avg = divide(ranks.stream().reduce(BigDecimal.ZERO, BigDecimal::add), ranks.size());
        max = ranks.get(ranks.size() - 1);

        // 7. Count normalized values (0.0 - 1.0) which can be shown as a percent.
        // https://stats.stackexchange.com/questions/70801/how-to-normalize-data-to-0-1-range
        var divisor = max.add(min.negate());
        normalized = prices.stream()
                .map(total -> divide(total.add(min.negate()), divisor).setScale(2, RoundingMode.HALF_UP)).toList();

        // 6. Wrap everything up in electricity price.
        var minutes = resolution.toMinutes();
        var startOffset = config.local(timeInterval.start);
        var endOffset = startOffset.plusMinutes(minutes);
        electricityPrices = IntStream.range(0, spot.size()).mapToObj(index -> {
            var start = startOffset.plusMinutes(index * minutes);
            var end = endOffset.plusMinutes(index * minutes);
            var spotTaxPrice = spotTaxPrices.get(index);
            var totalPrice = prices.get(index);
            var rank = ranks.indexOf(totalPrice) + 1;
            var normalizedPrice = normalized.get(index);
            return new ElectricityPrice(start, end, transferTaxPrice, taxTaxPrice, spotTaxPrice, marginTaxPrice,
                    config.energyPrice(totalPrice), rank, percent(normalizedPrice));
        }).toList();
    }

    public ElectricityPrice currentPrice(ZonedDateTime now) {
        return electricityPrices.stream().filter(price -> price.contains(now)).findFirst().orElseThrow();
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }

    @Override
    public ZonedDateTime start() {
        return zonedStart;
    }

    @Override
    public ZonedDateTime end() {
        return zonedEnd;
    }

    public boolean hasMinutes() {
        return resolution.toMinutesPart() == 0;
    }
}
