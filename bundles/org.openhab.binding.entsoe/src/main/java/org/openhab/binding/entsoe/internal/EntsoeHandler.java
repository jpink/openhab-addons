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
package org.openhab.binding.entsoe.internal;

import static org.openhab.binding.entsoe.internal.EntsoeBindingConstants.CHANNEL_1;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.entsoe.internal.client.EntsoeClient;
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

import java.util.Map;
import java.util.UUID;

/**
 * The {@link EntsoeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class EntsoeHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(EntsoeHandler.class);

    private @Nullable EntsoeConfiguration config;

    private final HttpClientFactory factory;

    private @Nullable EntsoeClient client;

    public EntsoeHandler(Thing thing, HttpClientFactory factory)
    {
        super(thing);
        this.factory = factory;
    }

    @Override
    public void dispose() {
        // TODO Cancel schedule
        super.dispose();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_1.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);
    }

    @Override
    public void thingUpdated(Thing thing) {
        super.thingUpdated(thing);
    }

    @Override
    public void initialize() {
        config = getConfigAs(EntsoeConfiguration.class);

        UUID token;
        try {
            token = UUID.fromString(config.token);
        } catch (IllegalArgumentException ex) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Invalid Secure Token!");
            return;
        }

        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.NOT_YET_READY, "Waiting for refresh");

        client = new EntsoeClient(factory.getCommonHttpClient(), token, config.area);

        // TODO Set resolution and time range in thing properties.
        // TODO Schedule

        // Example for background initialization:
        scheduler.execute(() -> {
            boolean thingReachable = true; // <background task with long running initialization here>
            // when done do:
            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });
    }
}
