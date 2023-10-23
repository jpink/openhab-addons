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

import static javax.measure.Quantity.Scale.RELATIVE;
import static org.openhab.binding.electric.common.Text.isBlank;
import static org.openhab.binding.electric.common.Text.splitWhitespace;
import static org.openhab.core.library.unit.Units.JOULE;
import static org.openhab.core.library.unit.Units.KILOWATT_HOUR;
import static org.openhab.core.library.unit.Units.MEGAWATT_HOUR;
import static org.openhab.core.library.unit.Units.PERCENT;
import static org.openhab.core.library.unit.Units.YEAR;
import static org.openhab.core.library.unit.Units.getInstance;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Time;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Monetary system of units.
 * <a href="https://www.entsoe.eu/about/inside-entsoe/members/"></a>
 * <a href="https://en.wikipedia.org/wiki/List_of_currencies_in_Europe"></a>
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@SuppressWarnings("unchecked")
@NonNullByDefault
public class Monetary extends tech.units.indriya.AbstractSystemOfUnits {
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
    public static final Currency EURO = Currency.getInstance("EUR");
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
    public static MathContext mathContext = MathContext.DECIMAL32;
    public static final Quantity<Dimensionless> ZERO = percent(0);

    public static <Q extends Quantity<Q>> Quantity<Q> add(Quantity<Q> quantity, Quantity<Q> addend) {
        var sum = quantity.add(addend);
        return quantity(sum.getValue(), sum.getUnit());
    }

    public static BigDecimal bigDecimal(BigDecimal value) {
        return (value.precision() > mathContext.getPrecision()
                ? new BigDecimal(value.toString().toCharArray(), mathContext)
                : value).stripTrailingZeros();
    }

    public static BigDecimal bigDecimal(Number number) {
        if (number instanceof BigDecimal val) {
            return bigDecimal(val);
        } else if (number instanceof BigInteger val) {
            return new BigDecimal(val, mathContext);
        } else if (number instanceof Double || number instanceof Float) {
            return BigDecimal.valueOf(number.doubleValue());
        } else if (number instanceof Long) {
            return new BigDecimal(number.longValue(), mathContext);
        } else if (number instanceof Integer) {
            return new BigDecimal(number.intValue(), mathContext);
        } else {
            // E.g. tech.units.indriya.function.RationalNumber
            return new BigDecimal(number.toString(), mathContext);
        }
    }

    public static BigDecimal bigDecimal(String value) {
        return new BigDecimal(value, mathContext).stripTrailingZeros();
    }

    public static Currency currency(@Nullable String currencyCode) {
        return isBlank(currencyCode) ? defaultCurrency() : Currency.getInstance(currencyCode);
    }

    public static Currency currency(Unit<Money> unit) {
        return Currency.getInstance(unit.getName());
    }

    public static BigDecimal divide(BigDecimal dividend, Number divisor) {
        return dividend.divide(bigDecimal(divisor), mathContext).stripTrailingZeros();
    }

    public static Quantity<?> divide(Quantity<?> dividend, Quantity<?> divisor) {
        var quantity = dividend.divide(divisor);
        return quantity(quantity.getValue(), quantity.getUnit());
    }

    public static Currency defaultCurrency() {
        return Currency.getInstance(Locale.getDefault());
    }

    public static Quantity<EnergyPrice> energyPrice(String amountAndUnit) {
        return (Quantity<EnergyPrice>) quantity(amountAndUnit);
    }

    public static Quantity<EnergyPrice> energyPrice(Number value, Unit<EnergyPrice> unit) {
        return quantity(value, unit);
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

    public static <Q extends MonetaryQuantity<Q>> Quantity<Q> monetary(long amount, Unit<Q> unit, Class<Q> type) {
        return monetary(bigDecimal(amount), unit, type);
    }

    public static <Q extends MonetaryQuantity<Q>> Quantity<Q> monetary(double amount, Unit<Q> unit, Class<Q> type) {
        return monetary(bigDecimal(amount), unit, type);
    }

    public static <Q extends MonetaryQuantity<Q>> Quantity<Q> monetary(String amount, Unit<Q> unit, Class<Q> type) {
        return monetary(bigDecimal(amount), unit, type);
    }

    public static <Q extends MonetaryQuantity<Q>> Quantity<Q> monetary(BigDecimal amount, Unit<Q> unit, Class<Q> type) {
        return quantity(amount, unit).asType(type); // TODO remove asType
    }

    public static Quantity<Money> money(String amountAndUnit) {
        return (Quantity<Money>) quantity(amountAndUnit);
    }

    public static Quantity<Money> money(Number amount, Unit<Money> unit) {
        return quantity(amount, unit);
    }

    public static Unit<Money> moneyCentUnit(Currency currency) {
        return MetricPrefix.CENTI(moneyUnit(currency));
    }

    public static Unit<Money> moneyCentUnit(String currencyCode) {
        return MetricPrefix.CENTI(moneyUnit(currencyCode));
    }

    public static Unit<Money> moneyUnit(Currency currency) {
        return moneyUnit(currency.getCurrencyCode());
    }

    public static Unit<Money> moneyUnit(String symbols) {
        return (Unit<Money>) parseUnit(symbols);
    }

    public static Unit<Energy> parseEnergyUnit(String symbols) {
        var unit = parseUnit(symbols);
        if (unit.isCompatible(JOULE)) {
            return (Unit<Energy>) unit;
        }
        throw new IllegalArgumentException("'" + symbols + "' isn't energy unit!");
    }

    public static Unit<?> parseUnit(String symbols) {
        return INSTANCE.simpleUnitFormat.parse(symbols);
    }

    public static Quantity<Dimensionless> percent(int percentage) {
        return quantity(percentage, PERCENT);
    }

    public static Quantity<Dimensionless> percent(Number percentage) {
        return quantity(percentage, PERCENT);
    }

    public static Quantity<?> quantity(String amountAndUnit) {
        var parts = splitWhitespace(amountAndUnit);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Monetary amount and unit must be separated by whitespace!");
        }
        return quantity(parts[0], parts[1]);
    }

    public static <Q extends Quantity<Q>> Quantity<Q> quantity(String amountAndUnit, Class<Q> type) {
        return quantity(amountAndUnit).asType(type);
    }

    public static Quantity<?> quantity(String value, String unit) {
        return tech.units.indriya.quantity.Quantities.getQuantity(bigDecimal(value), unit(unit), RELATIVE);
    }

    public static <Q extends Quantity<Q>> Quantity<Q> quantity(Number value, Unit<Q> unit) {
        return tech.units.indriya.quantity.Quantities.getQuantity(bigDecimal(value), unit, RELATIVE);
    }

    public static void setPrecision(int precision) {
        mathContext = new MathContext(precision);
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPrice(Number amount, Unit<Q> unit, int vatRate) {
        return taxPrice(amount, unit, percent(vatRate));
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPrice(Number amount, Unit<Q> unit,
            Quantity<Dimensionless> vatRate) {
        return taxPrice(quantity(amount, unit), vatRate);
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPrice(Quantity<Q> amount,
            Quantity<Dimensionless> vatRate) {
        return new TaxPriceByAmount<>(amount, vatRate);
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
        return taxPriceOfSum(quantity(sum, unit), vatRate);
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPriceOfSum(Quantity<Q> sum,
            Quantity<Dimensionless> vatRate) {
        return new TaxPriceBySum<>(vatRate, sum);
    }

    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> taxPriceOfSum(String totalAndUnit, Class<Q> type,
            int vatRate) {
        return taxPriceOfSum(quantity(totalAndUnit, type), percent(vatRate));
    }

    public static Quantity<TemporalPrice> temporalPrice(String amountAndUnit) {
        return (Quantity<TemporalPrice>) quantity(amountAndUnit);
    }

    public static Quantity<TemporalPrice> temporalPrice(Number amount, Unit<TemporalPrice> unit) {
        return quantity(amount, unit);
    }

    public static <Q extends Quantity<Q>> Quantity<Q> to(Quantity<Q> quantity, Unit<Q> unit) {
        return quantity(quantity.to(unit).getValue(), unit);
    }

    public static Unit<?> unit(String symbols) {
        var unit = INSTANCE.getUnit(symbols);
        if (unit == null) {
            unit = getInstance().getUnit(symbols);
        }
        if (unit == null) {
            unit = tech.units.indriya.unit.Units.getInstance().getUnit(symbols);
        }
        if (unit == null) {
            unit = parseUnit(symbols);
        }
        return unit;
    }

    private static Unit<Money> add(String currencyCode) {
        var symbol = Currency.getInstance(currencyCode).getSymbol();
        if (symbol == null || symbol.length() != 1) {
            throw new IllegalArgumentException();
        }
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

    private final tech.units.indriya.format.SimpleUnitFormat simpleUnitFormat = tech.units.indriya.format.SimpleUnitFormat
            .getInstance();

    private Monetary() {
    }

    public List<Unit<?>> getBaseCurrencies() {
        return units.stream()
                .filter(unit -> unit.getBaseUnits() == null && unit.getName() != null && unit.getName().length() == 3)
                .toList();
    }

    private Unit<Money> addCurrency(String currencyCode, String symbol, char dimension) {
        return addUnit(new tech.units.indriya.unit.BaseUnit<>(symbol, currencyCode,
                tech.units.indriya.unit.UnitDimension.parse(dimension)), currencyCode, symbol, currencyCode);
    }

    private <U extends Unit<?>> U addUnit(U unit, String name, String symbol, String... aliases) {
        Helper.addUnit(units, unit, name, symbol, tech.units.indriya.format.UnitStyle.SYMBOL_AND_LABEL);
        for (String alias : aliases) {
            simpleUnitFormat.alias(unit, alias);
        }
        return unit;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
