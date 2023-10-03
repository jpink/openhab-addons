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

import static org.openhab.binding.electric.common.monetary.Monetary.bigDecimal;

import java.math.BigDecimal;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Taxable price.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
@SuppressWarnings("unchecked")
public interface TaxPrice<Q extends MonetaryQuantity<Q>> {
    Quantity<Q> amount();

    default BigDecimal amountValue() {
        return bigDecimal(amount().getValue());
    }

    Quantity<Dimensionless> vatRate();

    default Quantity<Q> vat() {
        return ((Quantity<Q>) amount().multiply(vatRate())).to(unit());
    }

    default BigDecimal vatValue() {
        return bigDecimal(vat().getValue());
    }

    Quantity<Q> sum();

    default BigDecimal sumValue() {
        return bigDecimal(sum().getValue());
    }

    Unit<Q> unit();

    TaxPrice<Q> byAmount();

    TaxPrice<Q> bySum();
}
