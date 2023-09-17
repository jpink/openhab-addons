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

import java.time.ZoneId;
import java.util.Currency;

import org.junit.jupiter.api.Test;
import org.openhab.binding.entsoe.internal.client.EntsoeClient;
import org.openhab.binding.entsoe.internal.client.dto.Publication;
import org.openhab.core.library.unit.Units;

class DailyCacheTest {
    static final VatRate GENERIC = new VatRate(24);

    ProductPrice price(double total) {
        return ProductPrice.fromTotal(total, GENERIC, new CurrencyUnit(Currency.getInstance("EUR"), true),
                Units.KILOWATT_HOUR);
    }

    @Test
    void toDailyCache_FI2023_DailyCache() throws Bug {
        var publication = (Publication) EntsoeClient.parseDocument(readFile(FI2023));
        var transfer = price(3.4);
        var tax = price(2.79372);
        var margin = price(0.25);
        var details = new PriceDetails(ZoneId.of("Europe/Helsinki"), transfer, tax, GENERIC, margin);

        var instance = publication.toDailyCache(details);

        assertNotNull(instance);
    }
}
