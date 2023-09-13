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

import java.time.Duration;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.entsoe.internal.client.dto.PublicationMarket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * The {@link EntsoeClient} class is HTTP API client, which fetches
 * data from ENTSO-E Transparency Platform.
 *
 * @see <a href="https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html">User
 *      guide</a>
 *
 * @author Jukka Papinkivi - Initial contribution
 */
public class EntsoeClient {
    public static final String BASE_URL = "https://web-api.tp.entsoe.eu/api?securityToken=";
    private static final String DAY_AHEAD_PRICES_DOCUMENT = "&documentType=A44";
    private static final Duration DAY_AHEAD_PRICES_MIN = Duration.ofDays(1);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private static final Duration MAX_RANGE = Duration.ofDays(366);
    private static final XStream XSTREAM = new XStream();

    /**
     * @param securityToken Web Api Security Token
     * @param area <A href="https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html#_areas">Area EIC code</A>
     */
    public static String buildDayAheadPricesEndpoint(UUID securityToken, String area) {
        return BASE_URL + securityToken + DAY_AHEAD_PRICES_DOCUMENT + "&in_Domain=" + area + "&out_Domain=" + area;
    }

    /**
     * Formats {@link ZonedDateTime} to `yyyyMMddHHmm` and expresses it always in UTC.
     *
     * @param dateTime Date and time in any time zone.
     * @return `yyyyMMddHHmm` formatted UTC string.
     */
    public static String format(ZonedDateTime dateTime) {
        return FORMATTER.format(toUTC(dateTime));
    }

    public static PublicationMarket parsePublicationMarket(String content) {
        return (PublicationMarket) XSTREAM.fromXML(content);
    }

    public static ZonedDateTime toUTC(ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(ZoneOffset.UTC);
    }

    static {
        XSTREAM.allowTypeHierarchy(PublicationMarket.class);
        XSTREAM.processAnnotations(PublicationMarket.class);
        XSTREAM.ignoreUnknownElements();
    }

    private final HttpClient client;
    private final String dayAheadPricesEndpoint;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public EntsoeClient(HttpClient client, UUID securityToken, String area) {
        this.client = client;
        dayAheadPricesEndpoint = buildDayAheadPricesEndpoint(securityToken, area);
        logger.debug("Created for `{}` area.", area);
    }

    public String buildDayAheadPricesUrl(ZonedDateTime periodStart, ZonedDateTime periodEnd) {
        var duration = Duration.between(periodStart, periodEnd);
        if (DAY_AHEAD_PRICES_MIN.compareTo(duration) > 0) {
            throw new IllegalArgumentException("Time interval " + duration + " must be at least a one day!");
        }
        if (MAX_RANGE.compareTo(duration) < 0) {
            throw new IllegalArgumentException("Time interval " + duration + " is limited to a one year!");
        }
        return dayAheadPricesEndpoint + "&periodStart=" + format(periodStart) + "&periodEnd=" + format(periodEnd);
    }

    /**
     * @see <a href=
     *      "https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html#_day_ahead_prices_12_1_d">4.2.10.
     *      Day Ahead Prices [12.1.D]</a>
     */
    public PublicationMarket getDayAheadPrices(ZonedDateTime periodStart, ZonedDateTime periodEnd)
            throws ExecutionException {
        logger.debug("Getting day ahead prices for {}/{} period.", periodStart, periodEnd);
        var url = buildDayAheadPricesUrl(periodStart, periodEnd);
        logger.trace("GET {}", url);
        try {
            var response = client.GET(url);
            var status = response.getStatus();
            var content = response.getContentAsString();
            logger.trace(content);
            return switch (status) {
                case 200 ->
                    //TODO No matching data found, if reason code is 999.
                        parsePublicationMarket(content);
                case 400 -> {
                    logger.error("URL `{}` has invalid query parameters!", url);
                    yield null;
                }
                case 401 -> {
                    logger.error("Unauthorized. Missing or invalid security token!");
                    yield null;
                }
                case 429 -> {
                    logger.warn("Too many requests - max allowed 400 per minutes from each unique IP");
                    yield null;
                }
                default -> {
                    logger.error("GET `{}` responded unknown status {} with content: {}", url, status, content);
                    yield null;
                }
            };
        } catch (InterruptedException ex) {
            logger.debug("GET {} interrupted!", url);
        } catch (TimeoutException ex) {
            logger.debug("GET {} timeout!", url);
        }
        return null;
    }
}
