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
import javax.measure.quantity.Dimensionless;

import org.eclipse.jdt.annotation.NonNullByDefault;
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
 * @param futureRankHolder The cheapest daily price interval has rank 1, the second cheapest has rank 2, etc.
 *            The most expensive interval has the same value as count of intervals in the future.
 * @param futureNormalizedHolder A future normalized price which is between 0.0 and 1.0.
 */
@NonNullByDefault
public record ElectricityPrice(ZonedDateTime start, ZonedDateTime end, TaxPrice<EnergyPrice> transfer,
        TaxPrice<EnergyPrice> tax, TaxPrice<EnergyPrice> spot, TaxPrice<EnergyPrice> margin,
        Quantity<EnergyPrice> total, int dailyRank, Quantity<Dimensionless> dailyNormalized,
        Holder<Integer> futureRankHolder, Holder<Quantity<Dimensionless>> futureNormalizedHolder) implements Interval {
    public int futureRank() {
        return futureRankHolder.value;
    }

    public void futureRank(int rank) {
        futureRankHolder.value = rank;
    }

    public Quantity<Dimensionless> futureNormalized() {
        return futureNormalizedHolder.value;
    }

    public void futureNormalized(Quantity<Dimensionless> normalized) {
        futureNormalizedHolder.value = normalized;
    }
}
