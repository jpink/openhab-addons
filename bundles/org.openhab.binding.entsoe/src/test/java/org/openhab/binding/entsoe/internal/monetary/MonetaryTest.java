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

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.binding.entsoe.internal.monetary.Monetary.*;
import static org.openhab.binding.entsoe.internal.monetary.Monetary.getQuantity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import tech.units.indriya.format.SimpleUnitFormat;

import javax.measure.IncommensurableException;
import javax.measure.UnconvertibleException;
import javax.measure.Unit;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MonetaryTest {
    static Stream<Arguments> collection_size() {
        return Stream.of(//
                Arguments.of(26, getBaseCurrencies()),//
                Arguments.of(26, getBaseCurrencies().stream().map(Unit::getDimension).collect(Collectors.toSet())),
                Arguments.of(30, INSTANCE.getUnits()));
    }

    @ParameterizedTest
    @MethodSource
    void collection_size(int expected, Collection<?> actual) {
        assertEquals(expected, actual.size());
    }

    @Test
    void getConverterTo_eurToUsd_incommensurable() {
        var cause = assertThrows(UnconvertibleException.class, () -> EUR.getConverterTo(USD)).getCause();
        assertInstanceOf(IncommensurableException.class, cause);
        assertEquals("€ is not compatible with $", cause.getMessage());
    }

    static Set<Unit<?>> getUnits_symbol_unique() {
        return INSTANCE.getUnits();
    }

    @ParameterizedTest
    @MethodSource
    void getUnits_symbol_unique(Unit<?> unit) {
        assertEquals(unit, SimpleUnitFormat.getInstance().parse(unit.toString()));
    }

    @ParameterizedTest
    @ValueSource(strings = { "€", "MWh", "€/MWh", "c", "kWh", "c/kWh" })
    void findUnit_monetary(String symbols) {
        assertNotNull(findUnit(symbols));
    }

    @Test
    void isCompatible_eurVsUsd_false() {
        assertFalse(EUR.isCompatible(USD));
    }

    @ParameterizedTest
    @SuppressWarnings("unchecked")
    @CsvSource({ //
            "8.75 €/mo,0.2874802 €/day", // transfer
            "3.4 c/kWh,34 €/MWh", // transfer
            "2.79372 c/kWh,27.9372 €/MWh", // tax
            "4.9 €/mo,16.09890 c/day", // seller
            "75.957 €/MWh,7.5957 c/kWh", // spot
            "0.25 c/kWh,2.5 €/MWh" // margin
    })
    void isEquivalentTo(String a, String b) {
        var qa = getQuantity(a, MonetaryQuantity.class);
        var qb = getQuantity(b, MonetaryQuantity.class);
        if (!qa.isEquivalentTo(qb))
            assertEquals(b, qb.toString());
    }

    static Stream<Arguments> toString_equals() {
        return Stream.of(//
                Arguments.of("euro", getCurrency(getMoney("€")).getDisplayName()),//
                Arguments.of("mo", findUnit("kk")),//
                Arguments.of("24 €", taxPrice(100, EUR, 24).vat()),
                Arguments.of("124 €", taxPrice(100, EUR, 24).total()),
                Arguments.of("100 €", taxPriceOfTotal(124, EUR, 24).amount()));
    }

    @ParameterizedTest
    @MethodSource
    void toString_equals(String expected, Object actual) {
        assertEquals(expected, Objects.toString(actual));
    }

    @ParameterizedTest
    @CsvSource({ "ALL,Lekë", "AMD,֏", "AZN,₼", "BAM,KM", "BGN,лв", "BYN,Rbl", "CHF,SFr", "CZK,Kč", "DKK,DKr", "EUR,€",
            "GBP,£", "GEL,ლ", "HUF,Ft", "ISK,Kr", "JPY,¥", "MDL,L", "MKD,den", "NOK,NKr", "PLN,zł", "RON,lei",
            "RSD,РСД", "RUB,₽", "SEK,SKr", "TRY,₺", "UAH,₴", "USD,$" })
    void toString_moneyBaseUnit(String currencyCode, String expected) {
        assertEquals(expected, getMoney(currencyCode).toString());
    }

    @ParameterizedTest
    @CsvSource({ "EUR,c", "GBP,p", "USD,¢" })
    void toString_moneyBaseSubunit(String currencyCode, String expected) {
        assertEquals(expected, getCent(currencyCode).toString());
    }
}
