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
 * Electricity price record.
 *
 * @author Jukka Papinkivi - Initial contribution
 *
 * @param start
 * @param end
 * @param transfer The fixed electricity transfer fee.
 * @param tax The fixed energy tax amount.
 * @param spot The current spot price.
 * @param margin The fixed sellers margin price.
 * @param total The total price what consumer has to pay.
 * @param rank The cheapest price on available interval has rank 1, the second cheapest has rank 2, etc. The most
 *            expensive interval has the same value as count of intervals.
 * @param normalized A normalized price on available interval which is between 0.0 and 1.0.
 */
@NonNullByDefault
public record ElectricityPrice(ZonedDateTime start, ZonedDateTime end, TaxPrice<EnergyPrice> transfer,
        TaxPrice<EnergyPrice> tax, TaxPrice<EnergyPrice> spot, TaxPrice<EnergyPrice> margin,
        Quantity<EnergyPrice> total, int rank, Quantity<Dimensionless> normalized) implements Interval {
}
