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
package org.openhab.binding.electric.internal.handler.price;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.electric.common.monetary.Monetary;
import org.openhab.binding.electric.internal.handler.EntsoeHandlerFactory;
import org.openhab.binding.electric.internal.handler.entsoe.EntsoeClient;
import org.openhab.binding.electric.internal.handler.entsoe.exception.InvalidArea;
import org.openhab.binding.electric.internal.handler.entsoe.exception.InvalidToken;
import org.openhab.binding.electric.internal.handler.entsoe.exception.Unauthorized;
import org.openhab.binding.electric.internal.handler.price.service.Bug;
import org.openhab.binding.electric.internal.handler.price.service.CurrencyMismatch;
import org.openhab.binding.electric.internal.handler.price.service.PriceService;
import org.openhab.binding.electric.internal.old.AbstractThingHandler;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;

/**
 * The {@link PriceHandler} is responsible for handling commands, which are sent to one of the channels.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class PriceHandler extends AbstractThingHandler {
    public static long countInitialMinutes(long resolution, int now) {
        return (60 - now) % resolution;
    }

    private final HttpClientFactory httpClientFactory;
    private final ZoneId zone;
    private @Nullable PriceConfig config;
    private @Nullable EntsoeClient client;
    private @Nullable PriceService service;
    private @Nullable ScheduledFuture<?> refreshJob;
    private @Nullable ScheduledFuture<?> updateCurrentJob;

    private final ChannelUID//
    current = channel("current"), //
            normalized = channel("dailyNormalized"), //
            rank = channel("dailyRank"), //
            data = channel("data"), //
            graph = channel("graph"), //
            updated = channel("updated");

    public PriceHandler(Thing thing, EntsoeHandlerFactory handlerFactory) {
        super(thing);
        httpClientFactory = handlerFactory.httpClientFactory;
        zone = handlerFactory.timeZoneProvider.getTimeZone();
    }

    @Override
    public void initialize() {
        thingUnknown();
        refreshJob = scheduler.scheduleWithFixedDelay(this::handleRefresh, 0, 6, TimeUnit.HOURS);
        // Can't schedule current price update job yet, because the resolution isn't known.
    }

    private PriceConfig getConfiguration() {
        var config = this.config;
        if (config == null) {
            logger.trace("Transforming configuration.");
            config = getConfigAs(PriceConfig.class);
            config.zone = zone;
            Monetary.setPrecision(config.precision);
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

    private PriceService getService() throws Bug, CurrencyMismatch, InterruptedException, InvalidArea, InvalidToken,
            TimeoutException, Unauthorized {
        var service = this.service;
        if (service == null) {
            logger.trace("Creating Price Service.");
            service = new PriceService(getConfiguration(), getClient());
            this.service = service;
        }
        return service;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            handleRefresh();
        }
    }

    private void handleRefresh() {
        try {
            var service = getService();
            channel(updated, service.refresh());
            if (updateCurrentJob == null) {
                thingOnline();
                updateProperties(service.updateProperties(editProperties()));
                scheduleUpdateCurrentJob(service.resolution());
                try (var input = getClass().getResourceAsStream("entsoe.svg")) {
                    if (input != null) {
                        channelSvg(graph, input.readAllBytes());
                    }
                }
                channelJson(data, "{ \"foo\" : 1 }");
                handleUpdateCurrent();
            }
        } catch (Bug | IOException e) {
            thingBug(e);
        } catch (CurrencyMismatch e) {
            thingConfigurationError("Invalid currency!", e, "invalid-currency");
        } catch (InterruptedException e) {
            logger.debug("Request cancelled!");
        } catch (InvalidArea e) {
            thingConfigurationError("Invalid area!", e, "invalid-area");
        } catch (InvalidToken e) {
            thingConfigurationError("Invalid token!", e, "invalid-token");
        } catch (TimeoutException e) {
            thingCommunicationError("Request timeout!");
        } catch (Unauthorized e) {
            thingConfigurationError("Unauthorized!", e, "unauthorized");
        }
    }

    private void handleUpdateCurrent() {
        try {
            var service = getService();
            var price = service.currentPrice();
            if (price == null) {
                channelsUndefined();
                thingCommunicationError("Missing current price.");
            } else {
                channel(current, price.total());
                channelPercent(normalized, price.normalized());
                channel(rank, price.rank());
            }
        } catch (Exception e) {
            thingBug(e);
        }
    }

    private void scheduleUpdateCurrentJob(Duration resolution) {
        cancel(updateCurrentJob);
        var resolutionInMinutes = resolution.toMinutes();
        updateCurrentJob = scheduler.scheduleAtFixedRate(this::handleUpdateCurrent,
                countInitialMinutes(resolutionInMinutes, LocalTime.now().getMinute()), resolutionInMinutes,
                TimeUnit.MINUTES);
    }

    @Override
    public void dispose() {
        cancel(refreshJob, updateCurrentJob);
        client = null;
        config = null;
        updateCurrentJob = null;
        refreshJob = null;
    }
}
