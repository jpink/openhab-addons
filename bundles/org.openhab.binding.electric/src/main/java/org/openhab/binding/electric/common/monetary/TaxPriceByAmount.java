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
package org.openhab.binding.electric.common.monetary;

import static org.openhab.binding.electric.common.monetary.Monetary.add;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Taxable price created by amount.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public record TaxPriceByAmount<Q extends MonetaryQuantity<Q>> (Quantity<Q> amount,
        Quantity<Dimensionless> vatRate) implements TaxPrice<Q> {
    @Override
    public Quantity<Q> sum() {
        return add(amount, vat());
    }

    @Override
    public Unit<Q> unit() {
        return amount.getUnit();
    }

    @Override
    public TaxPrice<Q> byAmount() {
        return this;
    }

    @Override
    public TaxPrice<Q> bySum() {
        return new TaxPriceBySum<>(vatRate, sum());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        return this == o || o instanceof TaxPrice<?> p && amount.equals(p.amount()) && vatRate.equals(p.vatRate());
    }
}
