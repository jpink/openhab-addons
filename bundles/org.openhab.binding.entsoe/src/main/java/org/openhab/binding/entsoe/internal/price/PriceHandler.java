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
package org.openhab.binding.entsoe.internal.price;

import static org.openhab.binding.entsoe.internal.Constants.CHANNEL_1;
import static java.util.Collections.emptyList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.entsoe.internal.EntsoeHandlerFactory;
import org.openhab.binding.entsoe.internal.client.EntsoeClient;
import org.openhab.binding.entsoe.internal.client.dto.Area;
import org.openhab.binding.entsoe.internal.client.dto.Publication;
import org.openhab.binding.entsoe.internal.client.exception.*;
import org.openhab.binding.entsoe.internal.price.service.Bug;
import org.openhab.binding.entsoe.internal.price.service.PriceService;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Unit;
import javax.measure.quantity.Energy;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The {@link PriceHandler} is responsible for handling commands, which are sent to one of the channels.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class PriceHandler extends BaseThingHandler {
    private static final String MWH = "MWh";
    private static final String KWH = "kWh";

    public static long countInitialMinutes(long resolution, int now) {
        return (60 - now) % resolution;
    }

    private final Logger logger = LoggerFactory.getLogger(PriceHandler.class);
    private final HttpClientFactory httpClientFactory;
    private final ZoneId zone;

    private @Nullable PriceConfig config;
    private @Nullable EntsoeClient client;
    private @Nullable PriceService service;

    private @Nullable ZonedDateTime created;
    private @Nullable Double current;
    private @Nullable ScheduledFuture<?> currentPriceUpdateJob;
    private @Nullable ScheduledFuture<?> getDayAheadPricesJob;
    private @Nullable Unit<Energy> measure;
    private List<Double> prices = emptyList();
    private @Nullable Duration resolution;
    private @Nullable ZonedDateTime start;

    public PriceHandler(Thing thing, EntsoeHandlerFactory handlerFactory) {
        super(thing);
        httpClientFactory = handlerFactory.httpClientFactory;
        zone = handlerFactory.timeZoneProvider.getTimeZone();
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);

        getDayAheadPricesJob = scheduler.scheduleWithFixedDelay(this::handleGetDayAheadPrices, 0, 6, TimeUnit.HOURS);

        // Can't schedule current price update job yet, because the resolution isn't known.
    }

    private PriceConfig getConfiguration() {
        var config = this.config;
        if (config == null) {
            logger.trace("Transforming configuration.");
            config = getConfigAs(PriceConfig.class);
            this.config = config;
        }
        return config;
    }

    private EntsoeClient getClient() throws InvalidArea, InvalidToken {
        var client = this.client;
        if (client == null) {
            logger.trace("Creating ENTSO-E API client.");
            var config = getConfiguration();
            client = new EntsoeClient(httpClientFactory.getCommonHttpClient(), config.token, config.area);
            this.client = client;
        }
        return client;
    }

    private PriceService getService() throws InvalidArea, InvalidToken {
        var service = this.service;
        if (service == null) {
            logger.trace("Creating Price Service.");
            var config = getConfiguration();
            service = new PriceService(
                    new PriceService.Parameters(zone, config.transfer(), config.tax(), config.sellerVatRate(),
                            config.margin()), getClient());
            this.service = service;
        }
        return service;
    }

    private void bug(Throwable t) {
        logger.error("Please report the following bug!", t);
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
    }

    /*private List<Double> countPrices(List<Double> prices) {
        var config = getConfig();
        if (MWH.equals(measure)) {
            if (KWH.equals(config.unit))
                prices = prices.stream().map((price) -> price / 10F).toList();
        } else if (KWH.equals(measure)) {
            if (MWH.equals(config.unit))
                prices = prices.stream().map((price) -> price * 10F).toList();
        } else
            throw new UnsupportedOperationException("Source unit " + measure);
        var additions = config.transfer + config.tax + config.margin;
        if (additions != 0F)
            prices = prices.stream().map((price) -> price + additions).toList();
        return prices;
    }*/

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_1.equals(channelUID.getId())) {
            if (command instanceof RefreshType)
                handleGetDayAheadPrices();
        }
    }

    private void handleGetDayAheadPrices() {
        try {
            var now = ZonedDateTime.now();
            var document = (Publication) getClient().getDayAheadPrices(now, now.plusDays(1));
            if (document == null) {
                logger.debug("No matching data found!");
            } else
                try {
                    var timeSeries = document.timeSeries;
                    var period = timeSeries.period;
                    var start = period.timeInterval.start.withZoneSameInstant(zone);
                    this.start = start;
                    if (currentPriceUpdateJob == null) {
                        var properties = editProperties();
                        var currency = timeSeries.currency;
                        properties.put("currency", currency.getDisplayName());
                        String domain = timeSeries.domain;
                        try {
                            domain = Area.valueOf(timeSeries.domain).meaning;
                        } catch (IllegalArgumentException ignored) {
                        }
                        properties.put("domain", domain);
                        var measure = timeSeries.measure;
                        this.measure = measure;
                        properties.put("measure", measure.toString());
                        var resolution = period.resolution;
                        this.resolution = resolution;
                        properties.put("resolution", resolution.toMinutes() + " min");
                        properties.put("start", start.format(DateTimeFormatter.ISO_TIME));
                        updateProperties(properties);
                        scheduleCurrentPriceUpdateJob(resolution);
                    }
                    created = document.created.withZoneSameInstant(zone);
                    //prices = countPrices(period.getPrices());
                    updateStatus(ThingStatus.ONLINE);
                    handleUpdateCurrentPrice();
                } catch (Exception e) {
                    bug(e);
                }
        } catch (ExecutionException | InvalidParameter | TooLong | TooShort | UnknownResponse e) {
            bug(e);
        } catch (InterruptedException e) {
            logger.debug("Request cancelled!");
            updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.COMMUNICATION_ERROR);
        } catch (InvalidArea e) {
            logger.error("Invalid area!", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "invalid-area");
        } catch (InvalidToken e) {
            logger.error("Invalid token!", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "invalid-token");
        } catch (TimeoutException e) {
            logger.debug("Request timeout!");
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        } catch (TooMany e) {
            logger.debug("Too many requests!");
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "too-many");
        } catch (Unauthorized e) {
            logger.error("Unauthorized!");
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "unauthorized");
        }
    }

    private void handleRefresh() {
        try {
            getService().refresh();
        } catch (Bug e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "bug");
        } catch (InterruptedException e) {
            logger.debug("Request cancelled!");
            updateStatus(ThingStatus.UNKNOWN, ThingStatusDetail.COMMUNICATION_ERROR);
        } catch (InvalidArea e) {
            logger.error("Invalid area!", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "invalid-area");
        } catch (InvalidToken e) {
            logger.error("Invalid token!", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "invalid-token");
        } catch (TimeoutException e) {
            logger.debug("Request timeout!");
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        } catch (Unauthorized e) {
            logger.error("Unauthorized!");
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "unauthorized");
        }
    }

    private void handleUpdateCurrentPrice() {
        var now = ZonedDateTime.now(zone);
        var time = start;
        var resolution = this.resolution;
        Double current = null;
        if (time != null && resolution != null) {
            var minutes = resolution.toMinutes();
            for (var price : prices) {
                if (time.compareTo(now) < 0)
                    current = price;
                time = time.plusMinutes(minutes);
            }
            if (time.compareTo(now) < 0) {
                start = null;
                current = null;
                prices = emptyList();
            }
        }
        this.current = current;
    }

    private void scheduleCurrentPriceUpdateJob(Duration resolution) {
        cancel(currentPriceUpdateJob);
        var resolutionInMinutes = resolution.toMinutes();
        currentPriceUpdateJob = scheduler.scheduleAtFixedRate(this::handleUpdateCurrentPrice,
                countInitialMinutes(resolutionInMinutes, LocalTime.now().getMinute()), resolutionInMinutes,
                TimeUnit.MINUTES);
    }

    @Override
    protected void updateStatus(ThingStatus status, ThingStatusDetail statusDetail, @Nullable String offlineKey) {
        super.updateStatus(status, statusDetail, "@text/offline." + offlineKey);
    }

    private void cancel(@Nullable ScheduledFuture<?> job) {
        if (job != null)
            job.cancel(true);
    }

    @Override
    public void dispose() {
        cancel(currentPriceUpdateJob);
        cancel(getDayAheadPricesJob);
        client = null;
        config = null;
        currentPriceUpdateJob = null;
        getDayAheadPricesJob = null;
        measure = null;
        prices = emptyList();
        resolution = null;
        start = null;
    }

}
