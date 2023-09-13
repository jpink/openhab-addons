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

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import javax.measure.Unit;
import javax.measure.quantity.Energy;

import org.eclipse.jdt.annotation.NonNullByDefault;

@NonNullByDefault
public record DailyCache(ZonedDateTime created, String domain, ZonedDateTime start, ZonedDateTime end,
        Duration resolution, Currency currency, Unit<Energy> measure, List<ElectricityPrice> prices,
        DoubleSummaryStatistics statistics) implements Interval {

    public ElectricityPrice currentPrice(ZonedDateTime now) {
        return prices.stream().filter(price -> price.contains(now)).findFirst().orElseThrow();
    }
}
