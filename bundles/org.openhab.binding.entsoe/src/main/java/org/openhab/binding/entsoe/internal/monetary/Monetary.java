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

import static javax.measure.Quantity.Scale.*;
import static org.openhab.core.library.unit.Units.*;
import static tech.units.indriya.quantity.Quantities.getQuantity;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Time;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;

import tech.units.indriya.AbstractSystemOfUnits;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.format.SimpleUnitFormat;
import tech.units.indriya.format.UnitStyle;
import tech.units.indriya.unit.BaseUnit;
import tech.units.indriya.unit.UnitDimension;

/**
 * <a href="https://www.entsoe.eu/about/inside-entsoe/members/"></a>
 * <a href="https://en.wikipedia.org/wiki/List_of_currencies_in_Europe"></a>
 */
@SuppressWarnings("unchecked")
@NonNullByDefault
public class Monetary extends AbstractSystemOfUnits {
    public static final Monetary INSTANCE = new Monetary();
    public static final Unit<Time> MONTH = add(YEAR.divide(12), "Month", "mo", "kk");
    public static final Unit<Money> ALL = add("ALL", "Lekë", 'ë');
    public static final Unit<Money> AMD = add("AMD", '֏');
    public static final Unit<Money> AZN = add("AZN", '₼');
    public static final Unit<Money> BAM = add("BAM", "KM", '฿');
    public static final Unit<Money> BGN = add("BGN", "лв", 'л');
    public static final Unit<Money> BYN = add("BYN", "Rbl", '₨');
    public static final Unit<Money> CHF = add("CHF", "SFr", '₡');
    public static final Unit<Money> CZK = add("CZK", "Kč", '₭');
    public static final Unit<Money> DKK = add("DKK", "DKr", '߾');
    public static final Unit<Money> EUR = add("EUR", "€", '₠');
    public static final Unit<Money> EUR_CENT = addCent(EUR, "Euro cent", "c", "snt");
    public static final Unit<EnergyPrice> EURO_PER_MEGAWATT_HOUR = energyPriceUnit(EUR, MEGAWATT_HOUR);
    public static final Unit<EnergyPrice> EURO_CENT_PER_KILOWATT_HOUR = energyPriceUnit(EUR_CENT, KILOWATT_HOUR);
    public static final Unit<Money> GBP = add("GBP");
    public static final Unit<Money> GBP_PENNY = addCent(GBP, "Penny", "p");
    public static final Unit<Money> GEL = add("GEL", 'ლ');
    public static final Unit<Money> HUF = add("HUF", "Ft", '₣');
    public static final Unit<Money> ISK = add("ISK", "Kr", 'ó');
    public static final Unit<Money> JPY = add("JPY");
    public static final Unit<Money> MDL = add("MDL", "L", '₤');
    public static final Unit<Money> MKD = add("MKD", "den", '₫');
    public static final Unit<Money> NOK = add("NOK", "NKr", '₦');
    public static final Unit<Money> PLN = add("PLN", "zł", 'ł');
    public static final Unit<Money> RON = add("RON", "lei", '₾');
    public static final Unit<Money> RSD = add("RSD", "РСД", 'Д');
    public static final Unit<Money> RUB = add("RUB", '₽');
    public static final Unit<Money> SEK = add("SEK", "SKr", '₷');
    public static final Unit<Money> TRY = add("TRY", '₺');
    public static final Unit<Money> UAH = add("UAH", '₴');
    public static final Unit<Money> USD = add("USD");
    public static final Unit<Money> USD_CENT = addCent(USD, "Dollar cent", "¢");
    private static MathContext context = MathContext.DECIMAL32;

    public static Currency currency(Unit<Money> unit) {
        return Currency.getInstance(unit.getName());
    }

    public static Quantity<EnergyPrice> energyPrice(String amountAndUnit) {
        return (Quantity<EnergyPrice>) getQuantity(amountAndUnit);
    }

    public static Quantity<EnergyPrice> energyPrice(Number amount, Unit<EnergyPrice> unit) {
        return getQuantity(amount, unit, RELATIVE);
    }

    public static Unit<EnergyPrice> energyPriceUnit(Currency currency, Unit<Energy> energy) {
        return energyPriceUnit(moneyUnit(currency), energy);
    }

    public static Unit<EnergyPrice> energyPriceUnit(Unit<Money> money, Unit<Energy> energy) {
        return (Unit<EnergyPrice>) money.divide(energy);
    }

    public static <Q extends MonetaryQuantity<Q>> Quantity<Q> monetary(String amount, String unit, Class<Q> type) {
        return quantity(amount, unit).asType(type);
    }

    public static <Q extends MonetaryQuantity<Q>> ComparableQuantity<Q> monetary(long amount, Unit<Q> unit,
            Class<Q> type) {
        return monetary(new BigDecimal(amount, context), unit, type);
    }

    public static <Q extends MonetaryQuantity<Q>> ComparableQuantity<Q> monetary(double amount, Unit<Q> unit,
            Class<Q> type) {
        return monetary(new BigDecimal(amount, context), unit, type);
    }

    public static <Q extends MonetaryQuantity<Q>> ComparableQuantity<Q> monetary(String amount, Unit<Q> unit,
            Class<Q> type) {
        return monetary(new BigDecimal(amount, context), unit, type);
    }

    public static <Q extends MonetaryQuantity<Q>> ComparableQuantity<Q> monetary(BigDecimal amount, Unit<Q> unit,
            Class<Q> type) {
        return getQuantity(amount, unit, RELATIVE).asType(type);
    }

    public static Quantity<Money> money(String amountAndUnit) {
        return (Quantity<Money>) getQuantity(amountAndUnit);
    }

    public static Quantity<Money> money(Number amount, Unit<Money> unit) {
        return getQuantity(amount, unit, RELATIVE);
    }

    public static Unit<Money> moneyCentUnit(String currencyCode) {
        return MetricPrefix.CENTI(moneyUnit(currencyCode));
    }

    public static Unit<Money> moneyUnit(Currency currency) {
        return moneyUnit(currency.getCurrencyCode());
    }

    public static Unit<Money> moneyUnit(String symbols) {
        return (Unit<Money>) SimpleUnitFormat.getInstance().parse(symbols);
    }

    public static Quantity<Dimensionless> percent(int percentage) {
        return getQuantity(percentage, PERCENT, RELATIVE);
    }

    public static Quantity<?> quantity(String amountAndUnit) {
        var parts = StringUtils.split(amountAndUnit);
        if (parts.length != 2)
            throw new IllegalArgumentException("Monetary amount and unit must be separated by whitespace!");
        return quantity(parts[0], parts[1]);
    }

    public static <Q extends Quantity<Q>> Quantity<Q> quantity(String amountAndUnit, Class<Q> type) {
        return quantity(amountAndUnit).asType(type);
    }

    public static Quantity<?> quantity(String amount, String unit) {
        return getQuantity(new BigDecimal(amount, context), unit(unit), RELATIVE);
    }

    public static void setPrecision(int precision) {
        context = new MathContext(precision);
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPrice(Number amount, Unit<Q> unit, int vatRate) {
        return taxPrice(amount, unit, percent(vatRate));
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPrice(Number amount, Unit<Q> unit,
            Quantity<Dimensionless> vatRate) {
        return taxPrice(getQuantity(amount, unit, RELATIVE), vatRate);
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPrice(Quantity<Q> amount,
            Quantity<Dimensionless> vatRate) {
        return new TaxPrice<>(amount, vatRate);
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPrice(String amountAndUnit, Class<Q> type,
            int vatRate) {
        return taxPrice(quantity(amountAndUnit, type), percent(vatRate));
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPriceOfSum(Number sum, Unit<Q> unit, int vatRate) {
        return taxPriceOfSum(sum, unit, percent(vatRate));
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPriceOfSum(Number sum, Unit<Q> unit,
            Quantity<Dimensionless> vatRate) {
        return TaxPrice.ofSum(getQuantity(sum, unit, RELATIVE), vatRate);
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPriceOfSum(Quantity<Q> sum,
            Quantity<Dimensionless> vatRate) {
        return TaxPrice.ofSum(sum, vatRate);
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPriceOfSum(String totalAndUnit, Class<Q> type,
            int vatRate) {
        return taxPriceOfSum(quantity(totalAndUnit, type), percent(vatRate));
    }

    public static Quantity<TemporalPrice> temporalPrice(String amountAndUnit) {
        return (Quantity<TemporalPrice>) getQuantity(amountAndUnit);
    }

    public static Quantity<TemporalPrice> temporalPrice(Number amount, Unit<TemporalPrice> unit) {
        return getQuantity(amount, unit, RELATIVE);
    }

    public static Unit<?> unit(String symbols) {
        var unit = INSTANCE.getUnit(symbols);
        if (unit == null)
            unit = getInstance().getUnit(symbols);
        if (unit == null)
            unit = tech.units.indriya.unit.Units.getInstance().getUnit(symbols);
        if (unit == null)
            unit = SimpleUnitFormat.getInstance().parse(symbols);
        return unit;
    }

    private static Unit<Money> add(String currencyCode) {
        var symbol = Currency.getInstance(currencyCode).getSymbol();
        if (symbol == null || symbol.length() != 1)
            throw new IllegalArgumentException();
        return add(currencyCode, symbol.charAt(0));
    }

    private static Unit<Money> add(String currencyCode, char symbol) {
        return add(currencyCode, Character.toString(symbol), symbol);
    }

    private static Unit<Money> add(String currencyCode, String symbol, char dimension) {
        return INSTANCE.addCurrency(currencyCode, symbol, dimension);
    }

    private static Unit<Money> addCent(Unit<Money> baseUnit, String name, String label, String... aliases) {
        return add(MetricPrefix.CENTI(baseUnit), name, label, aliases);
    }

    private static <U extends Unit<?>> U add(U unit, String name, String symbol, String... aliases) {
        return INSTANCE.addUnit(unit, name, symbol, aliases);
    }

    private Monetary() {
    }

    public List<Unit<?>> getBaseCurrencies() {
        return units.stream()
                .filter(unit -> unit.getBaseUnits() == null && unit.getName() != null && unit.getName().length() == 3)
                .toList();
    }

    private Unit<Money> addCurrency(String currencyCode, String symbol, char dimension) {
        return addUnit(new BaseUnit<>(symbol, currencyCode, UnitDimension.parse(dimension)), currencyCode, symbol,
                currencyCode);
    }

    private <U extends Unit<?>> U addUnit(U unit, String name, String symbol, String... aliases) {
        Helper.addUnit(units, unit, name, symbol, UnitStyle.SYMBOL_AND_LABEL);
        for (String alias : aliases)
            SimpleUnitFormat.getInstance().alias(unit, alias);
        return unit;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
