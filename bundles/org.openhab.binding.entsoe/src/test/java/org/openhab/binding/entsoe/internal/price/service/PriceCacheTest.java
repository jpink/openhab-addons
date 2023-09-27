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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openhab.binding.entsoe.internal.client.EntsoeClientTest.CZ_FILE_2015;
import static org.openhab.binding.entsoe.internal.client.EntsoeClientTest.FI_FILE_2022;
import static org.openhab.binding.entsoe.internal.client.EntsoeClientTest.FI_FILE_2023;
import static org.openhab.binding.entsoe.internal.price.PriceConfigTest.CZ_CONFIG;
import static org.openhab.binding.entsoe.internal.price.PriceConfigTest.FI_CONFIG;
import static org.openhab.binding.entsoe.internal.price.PriceConfigTest.FI_CONFIG_2022;

import java.math.BigDecimal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.entsoe.internal.client.EntsoeClient;
import org.openhab.binding.entsoe.internal.client.EntsoeClientTest;
import org.openhab.binding.entsoe.internal.client.dto.Publication;
import org.openhab.binding.entsoe.internal.common.AbstractTest;
import org.openhab.binding.entsoe.internal.price.PriceConfig;
import org.opentest4j.AssertionFailedError;

import com.google.gson.Gson;

/**
 * Price cache unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
class PriceCacheTest extends AbstractTest {
    static final Gson GSON = PriceCache.builder().setPrettyPrinting().create();

    PriceCache create(PriceConfig config, String file) {
        var xml = new EntsoeClientTest().readXml(file);
        var publication = (Publication) EntsoeClient.parseDocument(xml);
        try {
            return new PriceCache(config, publication);
        } catch (CurrencyMismatch e) {
            throw new AssertionFailedError(null, e);
        }
    }

    void assertPriceCache(PriceConfig config, String file, double min, double avg) {
        var instance = create(config, file);
        assertEquals(BigDecimal.valueOf(min), instance.min);
        assertEquals(BigDecimal.valueOf(avg), instance.avg);
    }

    @Test
    void createCz2015() {
        assertPriceCache(CZ_CONFIG, CZ_FILE_2015, 7.045, 8.706481);
    }

    @Test
    void createFi2022() {
        assertPriceCache(FI_CONFIG_2022, FI_FILE_2022, 16.2289, 33.25296);
    }

    @Test
    void createFi2023() {
        assertPriceCache(FI_CONFIG, FI_FILE_2023, 5.94028, 6.564904);
    }

    void assertJson(PriceConfig config, String file) {
        assertEquals(readJson(file), GSON.toJson(create(config, file)));
    }

    @Test
    void jsonCz2015() {
        assertJson(CZ_CONFIG, CZ_FILE_2015);
    }

    @Test
    void jsonFi2022() {
        assertJson(FI_CONFIG_2022, FI_FILE_2022);
    }

    @Test
    void jsonFi2023() {
        assertJson(FI_CONFIG, FI_FILE_2023);
    }
}
