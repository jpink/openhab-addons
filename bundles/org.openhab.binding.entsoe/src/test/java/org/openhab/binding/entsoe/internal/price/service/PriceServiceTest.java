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

import java.math.BigDecimal;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.openhab.binding.entsoe.internal.client.EntsoeClient;
import org.openhab.binding.entsoe.internal.client.dto.Publication;
import org.openhab.binding.entsoe.internal.price.PriceConfig;

class PriceServiceTest {
    @Test
    void toDailyCache_FI2023_DailyCache() throws Bug, CurrencyMismatch {
        var config = new PriceConfig();
        config.zone = ZoneId.of("Europe/Helsinki");
        config.transfer = BigDecimal.valueOf(3.4);
        config.tax = BigDecimal.valueOf(2.79372);
        config.margin = BigDecimal.valueOf(0.25);
        config.general = 24;
        config.seller = 24;
        var publication = (Publication) EntsoeClient.parseDocument(readFile(FI2023));

        var instance = PriceService.parse(config, publication);

        assertNotNull(instance);
    }
}
