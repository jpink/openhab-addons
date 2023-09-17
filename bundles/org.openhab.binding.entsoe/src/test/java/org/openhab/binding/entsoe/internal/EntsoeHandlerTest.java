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
package org.openhab.binding.entsoe.internal;

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.binding.entsoe.internal.price.PriceHandler.countInitialMinutes;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class EntsoeHandlerTest {
    @ParameterizedTest
    @ValueSource(ints = { 5, 10, 15, 20, 30, 60 })
    void countInitialMinutes_all_ltResolution(int resolution) {
        for (int now = 0; now < 60; now++)
            assertTrue(countInitialMinutes(resolution, now) < resolution);
    }

    @ParameterizedTest
    @CsvSource({ "15,0,0", "15,1,14", "15,2,13", "15,14,1", "15,15,0", "15,16,14", "15,29,1", "15,30,0", "15,31,14",
            "15,44,1", "15,45,0", "15,46,14", "15,59,1", "60,0,0", "60,1,59", "60,2,58", "60,30,30", "60,58,2",
            "60,59,1" })
    void countInitialMinutes_all_ltResolution(int resolution, int now, int expected) {
        assertEquals(expected, countInitialMinutes(resolution, now));
    }
}
