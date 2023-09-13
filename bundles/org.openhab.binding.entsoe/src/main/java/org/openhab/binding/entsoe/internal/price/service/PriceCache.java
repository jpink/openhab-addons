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

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.entsoe.internal.client.dto.Publication;

@NonNullByDefault
public class PriceCache {
    private record DailyCache(ZonedDateTime start, ZonedDateTime end, ProductPrice average) {

        static DailyCache of(PriceDetails details, Publication publication) {
            var zone = details.zone();
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
