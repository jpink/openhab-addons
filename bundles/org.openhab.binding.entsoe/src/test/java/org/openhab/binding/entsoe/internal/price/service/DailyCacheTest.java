package org.openhab.binding.entsoe.internal.price.service;

import org.junit.jupiter.api.Test;
import org.openhab.binding.entsoe.internal.client.EntsoeClient;
import org.openhab.binding.entsoe.internal.client.dto.Publication;
import org.openhab.core.library.unit.Units;

import java.time.ZoneId;
import java.util.Currency;

import static org.openhab.binding.entsoe.internal.client.EntsoeClientTest.*;

import static org.junit.jupiter.api.Assertions.*;

class DailyCacheTest {
    private static final VatRate generic = new VatRate(24);

    private ProductPrice price(double total) {
        return ProductPrice.fromTotal(total, generic, new CurrencyUnit(Currency.getInstance("EUR"), true),
                Units.KILOWATT_HOUR);
    }

    @Test
    public void toDailyCache_FI2023_DailyCache() throws Bug {
        var publication = (Publication) EntsoeClient.parseDocument(readFile(FI2023));
        var transfer = price(3.4);
        var tax = price(2.79372);
        var margin = price(0.25);
        var details = new PriceDetails(ZoneId.of("Europe/Helsinki"), transfer, tax, generic, margin);

        var instance = publication.toDailyCache(details);

        assertNotNull(instance);
    }

}