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
package org.openhab.binding.entsoe.internal.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.binding.entsoe.internal.client.EntsoeClient.*;
import static org.openhab.binding.entsoe.internal.client.dto.Area.CZ;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.junit.jupiter.api.Test;
import org.openhab.binding.entsoe.internal.client.dto.PublicationMarket;

@NonNullByDefault
public class EntsoeClientTest {
    private static final LocalDateTime NEW_YEAR = LocalDateTime.of(2015, 12, 31, 23, 30);
    private static final ZonedDateTime START = ZonedDateTime.of(NEW_YEAR, ZoneId.of("Europe/Prague"));
    private static final UUID TOKEN = UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");
    private static final String ENDPOINT = BASE_URL + TOKEN + "&documentType=A44&in_Domain=" + CZ + "&out_Domain=" + CZ;

    private void assertPublicationMarket(PublicationMarket document, ZonedDateTime start, ZonedDateTime end) {
        assertNotNull(document);
        var timeSeries = document.timeSeries;
        assertNotNull(timeSeries);
        assertEquals(Currency.getInstance("EUR"), timeSeries.currency);
        assertEquals("MWH", timeSeries.measure);
        var period = timeSeries.period;
        assertNotNull(period);
        var timeInterval = period.timeInterval;
        assertEquals(start, timeInterval.start);
        assertEquals(end, timeInterval.end);
        assertEquals(Duration.ofHours(1), period.resolution);
        assertEquals(24, period.points.size());
        assertEquals(24, period.getPrices().size());
    }

    @Test
    public void buildDayAheadPricesEndpoint_CZ() {
        assertEquals(ENDPOINT, buildDayAheadPricesEndpoint(TOKEN, CZ.code));
    }

    @Test
    public void buildDayAheadPricesUrl_Guide_Ok() {
        assertEquals(ENDPOINT + "&periodStart=201512312230&periodEnd=201612312230",
                createClient().buildDayAheadPricesUrl(START, START.plusYears(1)));
    }

    @Test
    public void buildDayAheadPricesUrl_PT23H_Illegal() {
        assertThrows(IllegalArgumentException.class,
                () -> createClient().buildDayAheadPricesUrl(START, START.plusHours(23)));
    }

    @Test
    public void buildDayAheadPricesUrl_P367D_Illegal() {
        assertThrows(IllegalArgumentException.class,
                () -> createClient().buildDayAheadPricesUrl(START, START.plusDays(367)));
    }

    private EntsoeClient createClient() {
        return new EntsoeClient(new HttpClient(), TOKEN, CZ.code);
    }

    @Test
    public void format_CZ() {
        assertEquals("201512312230", format(START));
    }

    @Test
    public void format_FI() {
        assertEquals("201512312130", format(ZonedDateTime.of(NEW_YEAR, ZoneId.of("Europe/Helsinki"))));
    }

    @Test
    public void format_UTC() {
        assertEquals("201512312330", format(ZonedDateTime.of(NEW_YEAR, ZoneOffset.UTC)));
    }

    @Test
    public void parsePublicationMarket_Guide() {
        var content = readFile("2015-12-31_CZ_dayAheadPrices.xml");
        var start = ZonedDateTime.of(2015, 12, 31, 23, 0, 0, 0, ZoneOffset.UTC);

        var document = parsePublicationMarket(content);

        assertPublicationMarket(document, start, start.plusDays(1));
    }

    @Test
    public void parsePublicationMarket_FI2023() {
        var content = readFile("2023-09-09_FI_dayAheadPrices.xml");
        var start = ZonedDateTime.of(2023, 9, 8, 22, 0, 0, 0, ZoneOffset.UTC);

        var document = parsePublicationMarket(content);

        assertPublicationMarket(document, start, start.plusDays(1));
    }

    private String readFile(String filename) {
        var clazz = getClass();
        try (var input = clazz.getResourceAsStream(filename)) {
            if (input == null) {
                fail("Resource file '" + filename + "' of " + clazz + " not found!");
            } else {
                return new String(input.readAllBytes(), StandardCharsets.US_ASCII);
            }
        } catch (IOException ex) {
            fail(ex);
        }
        throw new IllegalStateException();
    }
}
