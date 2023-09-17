/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional information.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.entsoe.internal.monetary;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Time;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.library.unit.Units;

import tech.units.indriya.AbstractSystemOfUnits;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.format.SimpleUnitFormat;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.BaseUnit;
import tech.units.indriya.unit.ProductUnit;
import tech.units.indriya.unit.UnitDimension;

/**
 * <a href="https://www.entsoe.eu/about/inside-entsoe/members/"></a>
 * <a href="https://en.wikipedia.org/wiki/List_of_currencies_in_Europe"></a>
 */
@NonNullByDefault
public class Monetary extends AbstractSystemOfUnits {
    public static final Monetary INSTANCE = new Monetary();
    public static final Unit<Time> MONTH = add(Units.YEAR.divide(12), "mo");
    private static final Map<Currency, Unit<Money>> BASE_CURRENCIES = new HashMap<>();
    public static final UnitDimension DIMENSION = (UnitDimension) UnitDimension.parse('¤');
    public static final Unit<Money> ALL = add("ALL", "Lekë");
    public static final Unit<Money> AMD = add("AMD", "֏");
    public static final Unit<Money> AZN = add("AZN", "₼");
    public static final Unit<Money> BAM = add("BAM", "KM");
    public static final Unit<Money> BGN = add("BGN", "лв");
    public static final Unit<Money> BYN = add("BYN", "Rbl");
    public static final Unit<Money> CHF = add("CHF", "SFr");
    public static final Unit<Money> CZK = add("CZK", "Kč");
    public static final Unit<Money> DKK = add("DKK", "DKr");
    public static final Unit<Money> EUR = add("EUR");
    public static final Unit<Money> EUR_CENT = addCent(EUR, "c");
    public static final Unit<EnergyPrice> EURO_PER_MEGAWATT_HOUR = add(EUR, Units.MEGAWATT_HOUR);
    public static final Unit<EnergyPrice> EURO_CENT_PER_KILOWATT_HOUR = add(EUR_CENT, Units.KILOWATT_HOUR);
    public static final Unit<Money> GBP = add("GBP");
    public static final Unit<Money> GBP_PENNY = addCent(GBP, "p");
    public static final Unit<Money> GEL = add("GEL", "ლ");
    public static final Unit<Money> HUF = add("HUF", "Ft");
    public static final Unit<Money> ISK = add("ISK", "Kr");
    public static final Unit<Money> JPY = add("JPY");
    public static final Unit<Money> MDL = add("MDL", "L");
    public static final Unit<Money> MKD = add("MKD", "den");
    public static final Unit<Money> NOK = add("NOK", "NKr");
    public static final Unit<Money> PLN = add("PLN", "zł");
    public static final Unit<Money> RON = add("RON", "lei");
    public static final Unit<Money> RSD = add("RSD", "РСД");
    public static final Unit<Money> RUB = add("RUB", "₽");
    public static final Unit<Money> SEK = add("SEK", "SKr");
    public static final Unit<Money> TRY = add("TRY", "₺");
    public static final Unit<Money> UAH = add("UAH", "₴");
    public static final Unit<Money> USD = add("USD");
    public static final Unit<Money> USD_CENT = addCent(USD, "¢");
    private static final Units OPENHAB = (Units) Units.getInstance();
    private static final tech.units.indriya.unit.Units INDRIYA = tech.units.indriya.unit.Units.getInstance();
    private static MathContext context = MathContext.DECIMAL32;

    public static Unit<?> findUnit(String symbols) {
        var unit = INSTANCE.getUnit(symbols);
        if (unit == null)
            unit = OPENHAB.getUnit(symbols);
        if (unit == null)
            unit = INDRIYA.getUnit(symbols);
        if (unit == null)
            unit = SimpleUnitFormat.getInstance().parse(symbols);
        if (unit == null)
            throw new IllegalArgumentException("Unable to find unit '" + symbols + "'!");
        return unit;
    }

    public static Unit<Money> getUnit(Currency currency) {
        var unit = BASE_CURRENCIES.get(currency);
        if (unit == null)
            throw new IllegalArgumentException("Money unit not defined for " + currency + "!");
        return unit;
    }

    public static Unit<Money> getUnit(String currencyCode, boolean cent) {
        var unit = getUnit(Currency.getInstance(currencyCode));
        return cent ? MetricPrefix.CENTI(unit) : unit;
    }

    public static Quantity<?> getQuantity(String amountAndUnit) {
        var parts = StringUtils.split(amountAndUnit);
        if (parts.length != 2)
            throw new IllegalArgumentException("Monetary amount and unit must be separated by whitespace!");
        return getQuantity(parts[0], parts[1]);
    }

    public static <Q extends MonetaryQuantity<Q>> Quantity<Q> getQuantity(String amountAndUnit, Class<Q> type) {
        return getQuantity(amountAndUnit).asType(type);
    }

    public static Quantity<?> getQuantity(String amount, String unit) {
        return Quantities.getQuantity(new BigDecimal(amount, context), findUnit(unit), Quantity.Scale.RELATIVE);
    }

    public static <Q extends MonetaryQuantity<Q>> Quantity<Q> getQuantity(String amount, String unit, Class<Q> type) {
        return getQuantity(amount, unit).asType(type);
    }

    public static <Q extends MonetaryQuantity<Q>> ComparableQuantity<Q> getQuantity(long amount, Unit<Q> unit,
            Class<Q> type) {
        return getQuantity(new BigDecimal(amount, context), unit, type);
    }

    public static <Q extends MonetaryQuantity<Q>> ComparableQuantity<Q> getQuantity(double amount, Unit<Q> unit,
            Class<Q> type) {
        return getQuantity(new BigDecimal(amount, context), unit, type);
    }

    public static <Q extends MonetaryQuantity<Q>> ComparableQuantity<Q> getQuantity(String amount, Unit<Q> unit,
            Class<Q> type) {
        return getQuantity(new BigDecimal(amount, context), unit, type);
    }

    public static <Q extends MonetaryQuantity<Q>> ComparableQuantity<Q> getQuantity(BigDecimal amount, Unit<Q> unit,
            Class<Q> type) {
        return Quantities.getQuantity(amount, unit, Quantity.Scale.RELATIVE).asType(type);
    }

    public static void setPrecision(int precision) {
        context = new MathContext(precision);
    }

    private static Unit<Money> add(String currencyCode) {
        var currency = Currency.getInstance(currencyCode);
        return add(currency, currency.getSymbol());
    }

    private static Unit<Money> add(String currencyCode, String symbol) {
        var currency = Currency.getInstance(currencyCode);
        return add(currency, symbol);
    }

    private static Unit<Money> add(Currency currency, String symbol) {
        Unit<Money> unit = add(new BaseUnit<>(symbol, DIMENSION));
        BASE_CURRENCIES.put(currency, unit);
        return unit;
    }

    private static <U extends Unit<?>> U add(U unit) {
        return add(unit, unit.toString());
    }

    private static <U extends Unit<?>> U add(U unit, String label) {
        SimpleUnitFormat.getInstance().label(unit, label);
        INSTANCE.units.add(unit);
        return unit;
    }

    private static Unit<Money> addCent(Unit<Money> baseUnit, String label) {
        return add(MetricPrefix.CENTI(baseUnit), label);
    }

    private static Unit<EnergyPrice> add(Unit<Money> money, Unit<Energy> energy) {
        return add(new ProductUnit<>(money.divide(energy)));
    }

    protected Monetary() {
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
