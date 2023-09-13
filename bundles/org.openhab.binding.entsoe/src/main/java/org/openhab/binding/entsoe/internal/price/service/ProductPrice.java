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

import java.util.Objects;
import java.util.function.BiFunction;

import javax.measure.Unit;
import javax.measure.quantity.Energy;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Single or multiple products price. If products have different value added tax rates, then the rate is null.
 *
 * @param price The product price without value added tax.
 * @param vatRate The value added tax rate (0.0 - 1.0) used for the product. May be null if products have
 *            different rates.
 * @param vatAmount The value added tax amount.
 * @param total The product price with tax included.
 * @param currency The currency measure of the prices.
 * @param unit The energy measure of the prices.
 */
@NonNullByDefault
public record ProductPrice(double price, @Nullable VatRate vatRate, double vatAmount, double total,
        CurrencyUnit currency, Unit<Energy> unit) {
    public static ProductPrice fromPrice(double price, VatRate vatRate, CurrencyUnit currency, Unit<Energy> unit) {
        return new ProductPrice(price, vatRate, vatRate.vatFromPrice(price), vatRate.totalFromPrice(price), currency,
                unit);
    }

    public static ProductPrice fromTotal(double total, VatRate vatRate, CurrencyUnit currency, Unit<Energy> unit) {
        return new ProductPrice(vatRate.price(total), vatRate, vatRate.vatFromTotal(total), total, currency, unit);
    }

    @Override
    public String toString() {
        return vatRate == null ? currency.format(total) : currency.format(total) + " " + vatRate;
    }

    public ProductPrice plus(ProductPrice that) {
        var currencyConverter = that.currency.getConverterTo(currency);
        var unitConverter = that.unit.getConverterTo(unit);
        BiFunction<Double, Double, Double> sum = (me, other) -> me
                + unitConverter.convert(currencyConverter.convert(other));
        return new ProductPrice(sum.apply(price, that.price), Objects.equals(vatRate, that.vatRate) ? vatRate : null,
                sum.apply(vatAmount, that.vatAmount), sum.apply(total, that.total), currency, unit);
    }
}
