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

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.binding.entsoe.internal.client.EntsoeClientTest.*;
import static org.openhab.binding.entsoe.internal.common.Time.set;
import static org.openhab.binding.entsoe.internal.price.service.PriceService.*;

import java.math.BigDecimal;
import java.time.ZoneId;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.entsoe.internal.client.EntsoeClient;
import org.openhab.binding.entsoe.internal.client.dto.Publication;
import org.openhab.binding.entsoe.internal.price.PriceConfig;

/**
 * Price service unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
class PriceServiceTest {
    void parseDailyCache(int general, int seller, String file, double min, double avg) throws Exception {
        var config = new PriceConfig();
        config.zone = ZoneId.of("Europe/Helsinki");
        config.transfer = BigDecimal.valueOf(3.4);
        config.tax = BigDecimal.valueOf(2.79372);
        config.margin = BigDecimal.valueOf(0.25);
        config.general = general;
        config.seller = seller;
        var publication = (Publication) EntsoeClient.parseDocument(readFile(file));

        var instance = parse(config, publication);

        assertNotNull(instance);
        assertEquals(BigDecimal.valueOf(min), instance.minimum().getValue());
        assertEquals(BigDecimal.valueOf(avg), instance.average().getValue());
    }

    @Test
    void parseCz2023vat21DailyCache() throws Exception {
        parseDailyCache(21, 21, CZ2015, 12.44372, 28.96062);
    }

    @Test
    void parseFi2023vat10DailyCache() throws Exception {
        parseDailyCache(24, 10, FI2023, 5.25572, 14.52322);
    }

    @Test
    void parseFi2023vat24DailyCache() throws Exception {
        parseDailyCache(24, 24, FI2023, 5.10452, 15.55152);
    }

    @Test
    void publishedCz1245() {
        assertEquals("2015-12-31T12:45+01:00[Europe/Prague]", set(START, PUBLISHED).toString());
    }
}
