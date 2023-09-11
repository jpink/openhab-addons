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
package org.openhab.binding.entsoe.internal.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.binding.entsoe.internal.client.EntsoeClient.*;
import static org.openhab.binding.entsoe.internal.client.dto.Area.*;

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
import org.openhab.binding.entsoe.internal.client.dto.Acknowledgement;
import org.openhab.binding.entsoe.internal.client.dto.Area;
import org.openhab.binding.entsoe.internal.client.dto.Publication;
import org.openhab.binding.entsoe.internal.client.exception.InvalidArea;
import org.openhab.binding.entsoe.internal.client.exception.InvalidToken;
import org.openhab.binding.entsoe.internal.client.exception.TooLong;
import org.openhab.binding.entsoe.internal.client.exception.TooShort;
import org.openhab.core.library.unit.Units;

@NonNullByDefault
public class EntsoeClientTest {
    private static final LocalDateTime NEW_YEAR = LocalDateTime.of(2015, 12, 31, 23, 30);
    private static final ZonedDateTime START = ZonedDateTime.of(NEW_YEAR, ZoneId.of("Europe/Prague"));
    private static final String TOKEN_TEXT = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454";
    private static final UUID TOKEN_UUID = UUID.fromString(TOKEN_TEXT);
    private static final String ENDPOINT =
            BASE + TOKEN_UUID + "&documentType=A44&in_Domain=" + CZ + "&out_Domain=" + CZ;

    private void assertPublicationMarket(Publication document, Area area, ZonedDateTime created, ZonedDateTime start,
            ZonedDateTime end) {
        assertNotNull(document);
        assertEquals(created, document.created);
        var timeSeries = document.timeSeries;
        assertNotNull(timeSeries);
        assertEquals(area.code, timeSeries.domain);
        assertEquals(Currency.getInstance("EUR"), timeSeries.currency);
        assertEquals(Units.MEGAWATT_HOUR, timeSeries.measure);
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
    public void buildDayAheadPricesEndpoint_CZ_Valid() throws Exception {
        assertEquals(ENDPOINT, buildDayAheadPricesEndpoint(TOKEN_UUID, CZ.code));
    }

    @Test
    public void buildDayAheadPricesEndpoint_EUR_Invalid() {
        assertThrows(InvalidArea.class, () -> buildDayAheadPricesEndpoint(TOKEN_UUID, "EUR"));
    }

    @Test
    public void buildDayAheadPricesUrl_Guide_Ok() throws Exception {
        assertEquals(ENDPOINT + "&periodStart=201512312230&periodEnd=201612312230",
                createClient().buildDayAheadPricesUrl(START, START.plusYears(1)));
    }

    @Test
    public void buildDayAheadPricesUrl_P367D_TooLong() {
        assertThrows(TooLong.class, () -> createClient().buildDayAheadPricesUrl(START, START.plusDays(367)));
    }

    @Test
    public void buildDayAheadPricesUrl_PT23H_TooShort() {
        assertThrows(TooShort.class, () -> createClient().buildDayAheadPricesUrl(START, START.plusHours(23)));
    }

    private EntsoeClient createClient() throws Exception {
        return new EntsoeClient(new HttpClient(), TOKEN_UUID, CZ.code);
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
    public void parseDocument_Guide_Acknowledgement() {
        var content = readFile("2016-03-10_noData.xml");

        var document = (Acknowledgement) parseDocument(content);

        assertNotNull(document);
        var reason = document.reason;
        assertNotNull(reason);
        assertEquals(999, reason.code);
        assertEquals("No matching data found", reason.text);
    }

    @Test
    public void parseDocument_Guide_Publication() {
        var content = readFile("2015-12-31_CZ_dayAheadPrices.xml");
        var created = ZonedDateTime.of(2016, 5, 10, 9, 18, 53, 0, ZoneOffset.UTC);
        var start = ZonedDateTime.of(2015, 12, 31, 23, 0, 0, 0, ZoneOffset.UTC);

        var document = (Publication) parseDocument(content);

        assertPublicationMarket(document, CZ, created, start, start.plusDays(1));
    }

    @Test
    public void parseDocument_FI2023_Publication() {
        var content = readFile("2023-09-09_FI_dayAheadPrices.xml");
        var created = ZonedDateTime.of(2023, 9, 9, 10, 58, 2, 0, ZoneOffset.UTC);
        var start = ZonedDateTime.of(2023, 9, 8, 22, 0, 0, 0, ZoneOffset.UTC);

        var document = (Publication) parseDocument(content);

        assertPublicationMarket(document, FI, created, start, start.plusDays(1));
    }

    @Test
    public void parseToken_Guide_Invalid() {
        assertThrows(InvalidToken.class, () -> parseToken("MYTOKEN"));
    }

    @Test
    public void parseToken_Sample_Valid() throws InvalidToken {
        assertEquals(TOKEN_UUID, parseToken(TOKEN_TEXT));
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
