package org.openhab.binding.entsoe.internal.price.service.measure;

import tech.units.indriya.AbstractSystemOfUnits;

import javax.measure.Unit;
import java.math.BigDecimal;
import java.util.Currency;

/** <a href="https://en.wikipedia.org/wiki/Monetary_system">Monetary system</a> */
public class Monetary extends AbstractSystemOfUnits {
    private static BigDecimal amount(Number amount) {
        return amount(amount.toString());
    }

    private static BigDecimal amount(String amount) {
        return new BigDecimal(amount);
    }

    private static Currency currency(String code) {
        return Currency.getInstance(code);
    }

    public static CurrencyUnit getBaseUnit(Currency currency) {
        return new CurrencyUnit(currency);
    }

    public static CurrencyUnit getBaseUnit(String code) {
        return getBaseUnit(currency(code));
    }

    public static CurrencyUnit getCentUnit(Currency currency) {
        return new CurrencyCentUnit(currency);
    }

    public static CurrencyUnit getCentUnit(String code) {
        return getCentUnit(currency(code));
    }

    public static Money getMoney(BigDecimal amount, Unit<Money> unit) {
        return new Money(amount, unit);
    }

    public static Money getBase(BigDecimal amount, Currency currency) {
        return getMoney(amount, getBaseUnit(currency));
    }

    public static Money getBase(BigDecimal amount, String code) {
        return getMoney(amount, getBaseUnit(code));
    }

    public static Money getBase(Number amount, Currency currency) {
        return getBase(amount(amount), currency);
    }

    public static Money getBase(Number amount, String code) {
        return getBase(amount(amount), code);
    }

    public static Money getBase(String amount, Currency currency) {
        return getMoney(amount(amount), getBaseUnit(currency));
    }

    public static Money getBase(String amount, String code) {
        return getBase(amount, currency(code));
    }

    public static Money getCents(BigDecimal amount, Currency currency) {
        return getMoney(amount, getCentUnit(currency));
    }

    public static Money getCents(BigDecimal amount, String code) {
        return getMoney(amount, getCentUnit(code));
    }

    public static Money getCents(Number amount, Currency currency) {
        return getCents(amount(amount), currency);
    }

    public static Money getCents(Number amount, String code) {
        return getCents(amount(amount), currency(code));
    }

    public static Money getCents(String amount, Currency currency) {
        return getMoney(amount(amount), getCentUnit(currency));
    }

    public static Money getCents(String amount, String code) {
        return getCents(amount, currency(code));
    }

    @Override
    public String getName() {
        return "Monetary";
    }

}
