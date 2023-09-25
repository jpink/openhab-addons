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
package org.openhab.binding.entsoe.internal.monetary;

import static org.openhab.binding.entsoe.internal.monetary.Monetary.divide;
import static tech.units.indriya.AbstractQuantity.ONE;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Taxable price created by the sum.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
@SuppressWarnings("unchecked")
public record TaxPriceBySum<Q extends MonetaryQuantity<Q>> (Quantity<Dimensionless> vatRate,
        Quantity<Q> sum) implements TaxPrice<Q> {
    @Override
    public Quantity<Q> amount() {
        return ((Quantity<Q>) divide(sum, vatRate.add(ONE))).to(unit());
    }

    @Override
    public Unit<Q> unit() {
        return sum.getUnit();
    }

    @Override
    public TaxPrice<Q> byAmount() {
        return new TaxPriceByAmount<>(amount(), vatRate);
    }

    @Override
    public TaxPrice<Q> bySum() {
        return this;
    }
}
