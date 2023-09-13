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
package org.openhab.binding.entsoe.internal.client.dto;

import java.time.ZonedDateTime;
import java.util.Currency;

import javax.measure.Unit;
import javax.measure.quantity.Energy;

import org.openhab.binding.entsoe.internal.price.service.DailyCache;
import org.openhab.binding.entsoe.internal.price.service.PriceDetails;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class TimeSeries {
    @XStreamAlias("in_Domain.mRID")
    public String domain;

    @XStreamAlias("Period")
    public Period period;

    @XStreamAlias("currency_Unit.name")
    public Currency currency;

    @XStreamAlias("price_Measure_Unit.name")
    public Unit<Energy> measure;

    public DailyCache toDailyCache(PriceDetails details, ZonedDateTime created) {
        return period.toDailyCache(details, created, domain, currency, measure);
    }
}
