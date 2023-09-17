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

import java.util.Currency;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class MonetaryTest {
    @Test
    void toString_dimension_o() {
        assertEquals("[¤]", DIMENSION.toString());
    }

    @ParameterizedTest
    @CsvSource({ "ALL,Lekë", "AMD,֏", "AZN,₼", "BAM,KM", "BGN,лв", "BYN,Rbl", "CHF,SFr", "CZK,Kč", "DKK,DKr", "EUR,€",
            "GBP,£", "GEL,ლ", "HUF,Ft", "ISK,Kr", "JPY,¥", "MDL,L", "MKD,den", "NOK,NKr", "PLN,zł", "RON,lei",
            "RSD,РСД", "RUB,₽", "SEK,SKr", "TRY,₺", "UAH,₴", "USD,$" })
    void toString_moneyBaseUnit(String currencyCode, String expected) {
        assertEquals(expected, getUnit(Currency.getInstance(currencyCode)).toString());
    }

    @ParameterizedTest
    @CsvSource({ "EUR,c", "GBP,p", "USD,¢" })
    void toString_moneyBaseSubunit(String currencyCode, String expected) {
        assertEquals(expected, getUnit(currencyCode, true).toString());
    }

    @ParameterizedTest
    @ValueSource(strings = { "€", "MWh", "€/MWh", "c", "kWh", "c/kWh" })
    void findUnit_monetary(String symbols) {
        assertNotNull(findUnit(symbols));
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
}
