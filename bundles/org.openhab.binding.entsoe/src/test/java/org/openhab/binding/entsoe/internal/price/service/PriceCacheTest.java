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

import com.google.gson.Gson;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.entsoe.internal.client.EntsoeClient;
import org.openhab.binding.entsoe.internal.client.dto.Publication;
import org.openhab.binding.entsoe.internal.price.PriceConfig;

import java.math.BigDecimal;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openhab.binding.entsoe.internal.Constants.UNIT_CENT_PER_KWH;
import static org.openhab.binding.entsoe.internal.client.EntsoeClientTest.readFile;

/**
 * Price cache unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
class PriceCacheTest {
    private static final Gson GSON = PriceCache.builder().setPrettyPrinting().create();

    void assertJson(int general, int seller, String file) throws CurrencyMismatch {
        var config = new PriceConfig();
        config.zone = ZoneId.of("Europe/Helsinki");
        config.transfer = BigDecimal.valueOf(3.4);
        config.tax = BigDecimal.valueOf(2.79372);
        config.margin = BigDecimal.valueOf(0.25);
        config.unit = UNIT_CENT_PER_KWH;
        config.general = general;
        config.seller = seller;
        var publication = (Publication) EntsoeClient.parseDocument(readFile(file + ".xml"));
        var expected = readFile(file + ".json");

        var cache = new PriceCache(config, publication);

        assertEquals(expected, GSON.toJson(cache));
    }

    @Test
    void jsonFi2023vat24() throws CurrencyMismatch {
        assertJson(24, 24, "2023-09-26_FI_dayAheadPrices_twoSeries");
    }
}
