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

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * @param currency Base currency
 * @param inCents The integer part is cents
 */
@NonNullByDefault
public record CurrencyUnit(Currency currency, boolean inCents) {
    @NonNullByDefault
    public interface Converter {
        double convert(double value);
    }

    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.ENGLISH);

    public String format(double value) {
        return FORMAT.format(value) + " " + (inCents() ? "c" : currency().getSymbol());
    }

    public Converter getConverterTo(Currency that) {
        return getConverterTo(new CurrencyUnit(that, false));
    }

    public Converter getConverterTo(CurrencyUnit that) {
        if (!currency.equals(that.currency))
            throw new IllegalArgumentException("Unable exchange " + currency + " to " + that.currency + "!");
        if (inCents == that.inCents)
            return value -> value;
        if (inCents)
            return value -> value * 100.0;
        else
            return value -> value / 100.0;
    }
}
