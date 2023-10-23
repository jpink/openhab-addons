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
package org.openhab.binding.electric.internal.handler.entsoe;

import static org.openhab.binding.electric.common.Time.utc;
import static org.openhab.binding.electric.common.monetary.Monetary.EURO;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.electric.common.openhab.Xml;
import org.openhab.binding.electric.internal.handler.StatusKey;
import org.openhab.binding.electric.internal.handler.entsoe.dto.Acknowledgement;
import org.openhab.binding.electric.internal.handler.entsoe.dto.MarketDocument;
import org.openhab.binding.electric.internal.handler.entsoe.dto.Publication;
import org.openhab.binding.electric.internal.handler.price.PriceProvider;
import org.openhab.binding.electric.internal.handler.price.Product;
import org.openhab.binding.electric.internal.handler.price.ProductPrice;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.Thing;

import com.thoughtworks.xstream.XStreamException;

/**
 * The {@link EntsoeClient} class is HTTP API client, which fetches data from ENTSO-E Transparency Platform.
 *
 * @author Jukka Papinkivi - Initial contribution
 * @see <a href="https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html">User
 *      guide</a>
 */
@NonNullByDefault
public class EntsoeClient extends PriceProvider<EntsoeClient.Config> {
    private static final String DEFAULT_BASE = "https://web-api.tp.entsoe.eu/api";

    public static class Config {
        /**
         * REST API Base URL. Known values: <a href="https://web-api.tp.entsoe.eu/api">Default</a>
         * <a href="https://iop-transparency.entsoe.eu/api">Code sample</a>
         */
        String base = DEFAULT_BASE;
        String token = "";
        String area = "";
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private static final Xml XML = new Xml(Acknowledgement.class, Publication.class);

    /**
     * Formats {@link ZonedDateTime} to `yyyyMMddHHmm` and expresses it always in UTC.
     *
     * @param dateTime Date and time in any time zone.
     * @return `yyyyMMddHHmm` formatted UTC string.
     */
    public static String format(ZonedDateTime dateTime) {
        return FORMATTER.format(utc(dateTime));
    }

    public static MarketDocument parseDocument(String content) {
        return (MarketDocument) XML.deserialize(content);
    }

    private final HttpClient client;
    private URI baseUrl = URI.create(DEFAULT_BASE);
    private String securityToken = "";
    private String areaCode = "";
    private Currency currency = EURO;

    public EntsoeClient(HttpClientFactory clientFactory, Thing thing) {
        super(thing, Config.class);
        client = clientFactory.getCommonHttpClient();
    }

    @Override
    public void initialize() {
        var config = getConfiguration();
        try {
            baseUrl = new URL(config.base.trim()).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            logger.error("Invalid base URL '{}'", config.base, e);
            setStatus(StatusKey.INVALID_BASE_URL);
            return;
        }
        try {
            securityToken = UUID.fromString(config.token.trim()).toString();
        } catch (IllegalArgumentException e) {
            logger.error("Token '{}' isn't valid UUID!", config.token, e);
            setStatus(StatusKey.ENTSOE_INVALID_TOKEN);
            return;
        }
        var area = config.area.trim();
        switch (area.length()) {
            case 2, 16 -> areaCode = area;
            default -> {
                logger.error("Area '{}' must be 2 or 16 characters!", area);
                setStatus(StatusKey.ENTSOE_INVALID_AREA);
                return;
            }
        }
        currency = getPriceService().getCurrency();
        setUnknown();
    }

    @Override
    public Product getProduct() {
        return Product.SPOT;
    }

    @Override
    public List<ProductPrice> getPrices() {
        return Collections.emptyList();// TODO
    }

    private void tryGetDayAheadPrices() {
        try {
            getDayAheadPrices(ZonedDateTime.now(), ZonedDateTime.now());// TODO
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            setOfflineCommunicationBug(e);
        } catch (IllegalStateException e) {
            logger.error("Unauthorized. Missing or invalid security token!", e);
            setStatus(StatusKey.UNAUTHORIZED);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            setOfflineCommunicationError(e);
        }
    }

    /**
     * @see <a href=
     *      "https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html#_day_ahead_prices_12_1_d">4.2.10.
     *      Day Ahead Prices [12.1.D]</a>
     */
    private MarketDocument getDayAheadPrices(ZonedDateTime periodStart, ZonedDateTime periodEnd)
            throws ExecutionException, InterruptedException, TimeoutException {
        logger.info("Getting day ahead prices for {}/{} period.", periodStart, periodEnd);
        var duration = Duration.between(periodStart, periodEnd);
        if (duration.toDays() < 1) {
            throw new IllegalArgumentException("Range " + duration + " must be at least one day!");
        }
        if (periodEnd.compareTo(periodStart.plusYears(1)) > 0) { // Handles leap years also.
            throw new IllegalArgumentException("Range " + duration + " must be a year at most!");
        }
        var request = client.newRequest(baseUrl).method(HttpMethod.GET) //
                .param("securityToken", securityToken) //
                .param("documentType", "A44") //
                .param("in_Domain", areaCode) //
                .param("out_Domain", areaCode) //
                .param("periodStart", format(periodStart)) //
                .param("periodEnd", format(periodEnd));
        if (logger.isDebugEnabled()) {
            // Hiding security token from logs.
            logger.debug("GET {}", request.getURI().toString().replace(securityToken, "…"));
        }
        var stopwatch = StopWatch.createStarted();
        var response = request.send();
        stopwatch.stop();
        var status = response.getStatus();
        var content = response.getContentAsString();
        logger.trace("Got response in {} with status {} and content:\n{}\n", stopwatch, status, content);
        try {
            var document = parseDocument(content);
            switch (status) {
                case 200 -> {
                    return document;
                }
                case 400 -> throw new IllegalArgumentException("Invalid query parameter: " + document);
                case 401 -> throw new IllegalStateException(document.toString());
                case 429 -> throw new IllegalArgumentException(
                        "Too many requests - max allowed 400 per minutes from each unique IP: " + document);
                default -> throw new UnsupportedOperationException("Unknown response status " + status + "!");
            }
        } catch (ClassCastException | UnsupportedOperationException | XStreamException e) {
            throw new UnsupportedOperationException("Unknown response: " + content, e);
        }
    }
}
