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

import javax.measure.Quantity;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.entsoe.internal.monetary.EnergyPrice;
import org.openhab.binding.entsoe.internal.monetary.TaxPrice;

/**
 * Electricity price interval.
 *
 * @param start
 * @param end
 * @param transfer The fixed electricity transfer fee.
 * @param tax The fixed energy tax amount.
 * @param spot The current spot price.
 * @param margin The fixed sellers margin price.
 * @param total The total price what consumer has to pay.
 * @param dailyRank The cheapest daily price interval has rank 1, the second cheapest has rank 2, etc. The most
 *            expensive interval has the same value as count of intervals in a day.
 * @param dailyNormalized A daily normalized price which is between 0.0 and 1.0.
 * @param futureRank The cheapest daily price interval has rank 1, the second cheapest has rank 2, etc. The most
 *            expensive interval has the same value as count of intervals in the future.
 * @param futureNormalized A future normalized price which is between 0.0 and 1.0.
 */
@NonNullByDefault
public record ElectricityPrice(ZonedDateTime start, ZonedDateTime end, TaxPrice<EnergyPrice> transfer,
        TaxPrice<EnergyPrice> tax, TaxPrice<EnergyPrice> spot, TaxPrice<EnergyPrice> margin,
        Quantity<EnergyPrice> total, int dailyRank, @Nullable Quantity<?> dailyNormalized, Holder<Integer> futureRank,
        Holder<Double> futureNormalized) implements Interval {

}
