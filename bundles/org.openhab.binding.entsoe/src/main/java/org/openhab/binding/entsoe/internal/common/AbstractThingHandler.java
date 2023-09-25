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
package org.openhab.binding.entsoe.internal.common;

import static org.openhab.binding.entsoe.internal.common.Log.debug;
import static org.openhab.binding.entsoe.internal.common.Log.error;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.concurrent.ScheduledFuture;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.RawType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract thing handler.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public abstract class AbstractThingHandler extends BaseThingHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Creates a new instance of this class for the {@link Thing}.
     *
     * @param thing the thing that should be handled, not null
     */
    public AbstractThingHandler(Thing thing) {
        super(thing);
    }

    protected void cancel(@Nullable ScheduledFuture<?>... jobs) {
        for (var job : jobs) {
            if (job != null) {
                job.cancel(true);
            }
        }
    }

    private void channel(ChannelUID channelId, byte[] bytes, String mimeType) {
        updateState(channelId, new RawType(bytes, mimeType));
    }

    protected void channel(ChannelUID channelId, Number state) {
        updateState(channelId, new DecimalType(state));
    }

    protected <Q extends Quantity<Q>> void channel(ChannelUID channelId, Quantity<Q> state) {
        updateState(channelId, new QuantityType<>(state.getValue(), state.getUnit()));
    }

    protected final ChannelUID channel(String id) {
        return new ChannelUID(thing.getUID(), id);
    }

    protected void channel(ChannelUID channelId, ZonedDateTime state) {
        updateState(channelId, new DateTimeType(state));
    }

    protected void channelJson(ChannelUID channelId, String text) {
        channel(channelId, text.getBytes(StandardCharsets.UTF_8), "application/json");
    }

    protected void channelPercent(ChannelUID channelId, Quantity<Dimensionless> state) {
        int value = (int) Math.round(state.getValue().doubleValue() * 100.0);
        updateState(channelId, new PercentType(value));
    }

    protected void channelSvg(ChannelUID channelId, byte[] bytes) {
        channel(channelId, bytes, "image/svg+xml");
    }

    protected void channelUndefined(ChannelUID channelId) {
        updateState(channelId, UnDefType.UNDEF);
    }

    protected void channelsUndefined() {
        getThing().getChannels().forEach(channel -> channelUndefined(channel.getUID()));
    }

    protected void thingBug(Throwable t) {
        logger.error("Please report the following bug!", t);
        updateStatus(ThingStatus.UNKNOWN);
    }

    protected void thingCommunicationError(String message) {
        debug(logger, message);
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
    }

    protected void thingCommunicationError(String message, String key) {
        debug(logger, message);
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, key);
    }

    protected void thingConfigurationError(String message, Exception exception) {
        error(logger, message, exception);
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
    }

    protected void thingConfigurationError(String message, Exception exception, String key) {
        error(logger, message, exception);
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, key);
    }

    protected void thingOnline() {
        updateStatus(ThingStatus.ONLINE);
    }

    protected void thingUnknown() {
        updateStatus(ThingStatus.UNKNOWN);
    }

    @Override
    protected void updateStatus(ThingStatus status, ThingStatusDetail statusDetail, @Nullable String offlineKey) {
        super.updateStatus(status, statusDetail, "@text/offline." + offlineKey);
    }
}
