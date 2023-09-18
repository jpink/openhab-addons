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

import static tech.units.indriya.AbstractQuantity.ONE;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Taxable price
 */
@NonNullByDefault
@SuppressWarnings("unchecked")
public record TaxPrice<Q extends MonetaryQuantity<Q>> (Quantity<Q> amount, Quantity<Dimensionless> vatRate) {
    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> ofSum(Quantity<Q> sum, Quantity<Dimensionless> vatRate) {
        return new TaxPrice<>(((Quantity<Q>) sum.divide(vatRate.add(ONE))).to(sum.getUnit()), vatRate);
    }

    public Quantity<Q> vat() {
        return ((Quantity<Q>) amount.multiply(vatRate)).to(amount.getUnit());
    }

    public Quantity<Q> sum() {
        return amount.add(vat());
    }

    public Unit<Q> unit() {
        return amount.getUnit();
    }
}
