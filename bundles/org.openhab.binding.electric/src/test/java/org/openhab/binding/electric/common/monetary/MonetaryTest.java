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

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.binding.electric.internal.imp.monetary.Monetary.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.measure.IncommensurableException;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openhab.binding.electric.internal.imp.monetary.MonetaryQuantity;

import tech.units.indriya.format.SimpleUnitFormat;

/**
 * Monetary unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
class MonetaryTest {
    static Stream<Arguments> bigDecimalTrailingZeros() {
        return Stream.of(//
                Arguments.of(2, "1.0", new BigDecimal("1.0")), //
                Arguments.of(1, "1", new BigDecimal("1.0").stripTrailingZeros()), //
                Arguments.of(1, "1", new BigDecimal("1.0", new MathContext(2)).stripTrailingZeros())//
        );
    }

    @ParameterizedTest
    @MethodSource
    void bigDecimalTrailingZeros(int precision, String text, BigDecimal value) {
        assertEquals(precision, value.precision());
        assertEquals(text, value.toString());
    }

    static Stream<Arguments> collectionSize() {
        return Stream.of(//
                Arguments.of(26, INSTANCE.getBaseCurrencies()), //
                Arguments.of(26,
                        INSTANCE.getBaseCurrencies().stream().map(Unit::getDimension).collect(Collectors.toSet())),
                Arguments.of(30, INSTANCE.getUnits()));
    }

    @ParameterizedTest
    @MethodSource
    void collectionSize(int expected, Collection<?> actual) {
        assertEquals(expected, actual.size());
    }

    @Test
    void getConverterToEurToUsdIncommensurable() {
        var exception = assertThrows(UnconvertibleException.class, () -> EUR.getConverterTo(USD));
        assertNotNull(exception);
        var cause = exception.getCause();
        assertNotNull(cause);
        assertInstanceOf(IncommensurableException.class, cause);
        assertEquals("€ is not compatible with $", cause.getMessage());
    }

    static Set<Unit<?>> getUnitsSymbolUnique() {
        return INSTANCE.getUnits();
    }

    @ParameterizedTest
    @MethodSource
    void getUnitsSymbolUnique(Unit<?> unit) {
        assertEquals(unit, SimpleUnitFormat.getInstance().parse(unit.toString()));
    }

    @ParameterizedTest
    @ValueSource(strings = { "€", "MWh", "€/MWh", "c", "kWh", "c/kWh" })
    void findUnitMonetary(String symbols) {
        assertNotNull(unit(symbols));
    }

    @Test
    void isCompatibleEurVsUsdFalse() {
        assertFalse(EUR.isCompatible(USD));
    }

    @ParameterizedTest
    @SuppressWarnings("unchecked")
    @CsvSource({ //
            "8.75 €/mo,0.2874802 €/day", // transfer
            "3.4 c/kWh,34 €/MWh", // transfer
            "2.79372 c/kWh,27.9372 €/MWh", // tax
            "4.9 €/mo,16.0989 c/day", // seller
            "75.957 €/MWh,7.5957 c/kWh", // spot
            "0.25 c/kWh,2.5 €/MWh" // margin
    })
    void isEquivalentTo(String a, String b) {
        var qa = quantity(a, org.openhab.binding.electric.internal.imp.monetary.MonetaryQuantity.class);
        var qb = quantity(b, MonetaryQuantity.class);
        if (!qa.isEquivalentTo(qb)) {
            assertEquals(b, qb.toString());
        }
    }

    static Stream<Arguments> toStringEquals() {
        return Stream.of(//
                Arguments.of("euro", currency(moneyUnit("€")).getDisplayName()), //
                Arguments.of("mo", unit("kk")), //
                Arguments.of("24 €", taxPrice(100, EUR, 24).vat()), Arguments.of("124 €", taxPrice(100, EUR, 24).sum()),
                Arguments.of("100 €", taxPriceOfSum(124, EUR, 24).amount()));
    }

    @ParameterizedTest
    @MethodSource
    void toStringEquals(String expected, Object actual) {
        assertEquals(expected, Objects.toString(actual));
    }

    @ParameterizedTest
    @CsvSource({ "ALL,Lekë", "AMD,֏", "AZN,₼", "BAM,KM", "BGN,лв", "BYN,Rbl", "CHF,SFr", "CZK,Kč", "DKK,DKr", "EUR,€",
            "GBP,£", "GEL,ლ", "HUF,Ft", "ISK,Kr", "JPY,¥", "MDL,L", "MKD,den", "NOK,NKr", "PLN,zł", "RON,lei",
            "RSD,РСД", "RUB,₽", "SEK,SKr", "TRY,₺", "UAH,₴", "USD,$" })
    void toStringMoneyBaseUnit(String currencyCode, String expected) {
        assertEquals(expected, moneyUnit(currencyCode).toString());
    }

    @ParameterizedTest
    @CsvSource({ "EUR,c", "GBP,p", "USD,¢" })
    void toStringMoneyBaseSubunit(String currencyCode, String expected) {
        assertEquals(expected, moneyCentUnit(currencyCode).toString());
    }
}
