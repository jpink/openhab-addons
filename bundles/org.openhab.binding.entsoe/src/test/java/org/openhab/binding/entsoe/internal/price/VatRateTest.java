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
package org.openhab.binding.entsoe.internal.price;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openhab.binding.entsoe.internal.price.service.VatRate;

class VatRateTest {
    static final double PRICE = 100;
    static final VatRate GENERAL = new VatRate(24);
    static final double VAT = 24;
    static final double TOTAL = 124;

    @ParameterizedTest
    @ValueSource(doubles = { -0.1, 1.1 })
    void create_invalid_illegal(double rate) {
        assertThrows(IllegalArgumentException.class, () -> new VatRate(rate));
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 101 })
    void create_invalid_illegal(int rate) {
        assertThrows(IllegalArgumentException.class, () -> new VatRate(rate));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 24, 100 })
    void create_valid_ok(int rate) {
        new VatRate(rate);
    }

    @Test
    void price_124_100() {
        assertEquals(PRICE, GENERAL.price(TOTAL));
    }

    @Test
    void vatFromTotal_124_24() {
        assertEquals(VAT, GENERAL.vatFromTotal(TOTAL));
    }

    @Test
    void toString_24() {
        assertEquals("VAT 24%", GENERAL.toString());
    }
}
