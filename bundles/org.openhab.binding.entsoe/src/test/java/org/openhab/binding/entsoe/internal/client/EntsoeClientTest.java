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
import static org.openhab.binding.entsoe.internal.client.dto.Area.*;
import static org.openhab.binding.entsoe.internal.price.PriceConfigTest.PRAGUE;

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
import org.openhab.binding.entsoe.internal.common.AbstractTest;
import org.openhab.core.library.unit.Units;

/**
 * ENTSO-E client unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class EntsoeClientTest extends AbstractTest {
    public static final String CZ_FILE_2015 = "2015-12-31_CZ_dayAheadPrices";
    public static final String FI_FILE_2022 = "2022-12-06_FI_dayAheadPrices_oneSeries";
    public static final String FI_FILE_2023 = "2023-09-26_FI_dayAheadPrices_twoSeries";
    public static final LocalDateTime NEW_YEAR = LocalDateTime.of(2015, 12, 31, 23, 30);
    static final ZonedDateTime START = ZonedDateTime.of(NEW_YEAR, PRAGUE);
    static final String TOKEN_TEXT = "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454";
    static final UUID TOKEN_UUID = UUID.fromString(TOKEN_TEXT);
    static final String ENDPOINT = BASE + TOKEN_UUID + "&documentType=A44&in_Domain=" + CZ + "&out_Domain=" + CZ;

    void assertPublicationMarket(Publication document, Area area, ZonedDateTime created, ZonedDateTime start,
            ZonedDateTime end) {
        assertNotNull(document);
        assertEquals(created, document.created);
        var timeSeries = document.timeSeries.get(0);
        assertNotNull(timeSeries);
        assertEquals(area.code, timeSeries.domain);
        assertEquals(Currency.getInstance("EUR"), timeSeries.currency);
        assertEquals(Units.MEGAWATT_HOUR, timeSeries.measure);
        var period = timeSeries.period;
        assertNotNull(period);
        assertEquals(Duration.ofHours(1), period.resolution);
        assertEquals(24, period.points.size());
        var timeInterval = document.timeInterval;
        assertEquals(start, timeInterval.start);
        assertEquals(end, timeInterval.end);
    }

    @Test
    void buildDayAheadPricesEndpointCzValid() throws Exception {
        assertEquals(ENDPOINT, buildDayAheadPricesEndpoint(TOKEN_UUID, CZ.code));
    }

    @Test
    void buildDayAheadPricesEndpointEurInvalid() {
        assertThrows(InvalidArea.class, () -> buildDayAheadPricesEndpoint(TOKEN_UUID, "EUR"));
    }

    @Test
    void buildDayAheadPricesUrlGuideOk() throws Exception {
        assertEquals(ENDPOINT + "&periodStart=201512312230&periodEnd=201612312230",
                createClient().buildDayAheadPricesUrl(START, START.plusYears(1)));
    }

    @Test
    void buildDayAheadPricesUrlP367DTooLong() {
        assertThrows(TooLong.class, () -> createClient().buildDayAheadPricesUrl(START, START.plusDays(367)));
    }

    @Test
    void buildDayAheadPricesUrlPT23HTooShort() {
        assertThrows(TooShort.class, () -> createClient().buildDayAheadPricesUrl(START, START.plusHours(23)));
    }

    EntsoeClient createClient() throws Exception {
        return new EntsoeClient(new HttpClient(), TOKEN_UUID, CZ.code);
    }

    @Test
    void formatCz() {
        assertEquals("201512312230", format(START));
    }

    @Test
    void formatFi() {
        assertEquals("201512312130", format(ZonedDateTime.of(NEW_YEAR, ZoneId.of("Europe/Helsinki"))));
    }

    @Test
    void formatUtc() {
        assertEquals("201512312330", format(ZonedDateTime.of(NEW_YEAR, ZoneOffset.UTC)));
    }

    @Test
    void parseDocumentGuideAcknowledgement() {
        var content = readXml("2016-03-10_noData");

        var document = (Acknowledgement) parseDocument(content);

        assertNotNull(document);
        var reason = document.reason;
        assertNotNull(reason);
        assertEquals(999, reason.code);
        assertEquals("No matching data found", reason.text);
    }

    @Test
    void parseDocumentGuidePublication() {
        var content = readXml(CZ_FILE_2015);
        var created = ZonedDateTime.parse("2016-05-10T09:18:53Z");
        var start = ZonedDateTime.parse("2015-12-31T23:00Z");
        var end = ZonedDateTime.parse("2016-04-02T22:00Z");

        var document = (Publication) parseDocument(content);

        assertPublicationMarket(document, CZ, created, start, end);
    }

    @Test
    void parseDocumentFi2023InvalidInterval() {
        var content = readXml("2023-09-25_FI_dayAheadPrices_invalidInterval");

        var document = (Acknowledgement) parseDocument(content);

        assertNotNull(document);
        var reason = document.reason;
        assertNotNull(reason);
        assertEquals(999, reason.code);
        assertEquals("Delivered time interval is not valid for this Data item.", reason.text);
    }

    @Test
    void parseDocumentFi2022OneSeries() {
        var content = readXml(FI_FILE_2022);
        var created = ZonedDateTime.parse("2023-09-27T21:23:51Z");
        var start = ZonedDateTime.parse("2022-12-05T23:00Z");

        var document = (Publication) parseDocument(content);

        assertPublicationMarket(document, FI, created, start, start.plusDays(1));
    }

    @Test
    void parseDocumentFi2023TwoSeries() {
        // Requested: 202309252100 - 202309262100
        var content = readXml(FI_FILE_2023);
        var created = ZonedDateTime.parse("2023-09-25T21:59:23Z");
        var start = ZonedDateTime.parse("2023-09-24T22:00Z");

        var document = (Publication) parseDocument(content);

        assertPublicationMarket(document, FI, created, start, start.plusDays(2));
    }

    @Test
    void parseTokenGuideInvalid() {
        assertThrows(InvalidToken.class, () -> parseToken("MYTOKEN"));
    }

    @Test
    void parseTokenSampleValid() throws InvalidToken {
        assertEquals(TOKEN_UUID, parseToken(TOKEN_TEXT));
    }
}
