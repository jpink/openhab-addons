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
package org.openhab.binding.electric.common.openhab.thing;

import static org.openhab.core.thing.ThingStatus.OFFLINE;
import static org.openhab.core.thing.ThingStatus.ONLINE;
import static org.openhab.core.thing.ThingStatusDetail.BRIDGE_OFFLINE;
import static org.openhab.core.thing.ThingStatusDetail.COMMUNICATION_ERROR;
import static org.openhab.core.thing.ThingStatusDetail.CONFIGURATION_ERROR;
import static org.openhab.core.thing.ThingStatusDetail.CONFIGURATION_PENDING;
import static org.openhab.core.thing.ThingStatusDetail.DUTY_CYCLE;
import static org.openhab.core.thing.ThingStatusDetail.FIRMWARE_UPDATING;
import static org.openhab.core.thing.ThingStatusDetail.GONE;
import static org.openhab.core.thing.util.ThingHandlerHelper.isHandlerInitialized;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract thing handler.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public abstract class AbstractThingHandler<C> extends BaseThingHandler {
    public static final Map<ThingStatusDetail, ThingStatus> STATUS_BY_DETAIL = Map.of(COMMUNICATION_ERROR, OFFLINE,
            CONFIGURATION_ERROR, OFFLINE, CONFIGURATION_PENDING, ONLINE, BRIDGE_OFFLINE, OFFLINE, FIRMWARE_UPDATING,
            OFFLINE, DUTY_CYCLE, OFFLINE, GONE, OFFLINE);

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final Class<C> configurationClass;
    private @Nullable ThingStatusKey statusKey;

    /**
     * Creates a new instance of this class for the {@link Thing}.
     *
     * @param thing the thing that should be handled, not null
     */
    public AbstractThingHandler(Thing thing, Class<C> configurationClass) {
        super(thing);
        this.configurationClass = configurationClass;
    }

    protected void cancel(@Nullable ScheduledFuture<?>... jobs) {
        for (var job : jobs) {
            if (job != null) {
                job.cancel(true);
            }
        }
    }

    /*
     * private void channel(ChannelUID channelId, byte[] bytes, String mimeType) {
     * updateState(channelId, new RawType(bytes, mimeType));
     * }
     * 
     * protected void channel(ChannelUID channelId, Number state) {
     * updateState(channelId, new DecimalType(state));
     * }
     * 
     * protected <Q extends Quantity<Q>> void channel(ChannelUID channelId, Quantity<Q> state) {
     * updateState(channelId, new QuantityType<>(state.getValue(), state.getUnit()));
     * }
     * 
     * protected final ChannelUID channel(String id) {
     * return new ChannelUID(thing.getUID(), id);
     * }
     * 
     * protected void channel(ChannelUID channelId, ZonedDateTime state) {
     * updateState(channelId, new DateTimeType(state));
     * }
     * 
     * protected void channelJson(ChannelUID channelId, String text) {
     * channel(channelId, text.getBytes(StandardCharsets.UTF_8), "application/json");
     * }
     * 
     * protected void channelPercent(ChannelUID channelId, Quantity<Dimensionless> state) {
     * int value = (int) Math.round(state.getValue().doubleValue() * 100.0);
     * updateState(channelId, new PercentType(value));
     * }
     * 
     * protected void channelSvg(ChannelUID channelId, byte[] bytes) {
     * channel(channelId, bytes, "image/svg+xml");
     * }
     * 
     * protected void channelUndefined(ChannelUID channelId) {
     * updateState(channelId, UnDefType.UNDEF);
     * }
     * 
     * protected void channelsUndefined() {
     * getThing().getChannels().forEach(channel -> channelUndefined(channel.getUID()));
     * }
     */

    protected C getConfiguration() {
        var configuration = getConfigAs(configurationClass);
        if (configuration == null)
            throw new IllegalStateException("Can't create " + configurationClass.getName());
        return configuration;
    }

    private ThingStatus getStatus() {
        return thing.getStatus();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    public final boolean isInitialized() {
        return isHandlerInitialized(this);
    }

    public final boolean isOffline() {
        return OFFLINE.equals(getStatus());
    }

    public final boolean isOnline() {
        return ThingStatus.ONLINE.equals(getStatus());
    }

    public final boolean isUnknown() {
        return ThingStatus.UNKNOWN.equals(getStatus());
    }

    protected final void setOffline() {
        updateStatus(OFFLINE);
    }

    protected final void setOfflineConfigurationPending() {
        updateStatus(OFFLINE, CONFIGURATION_ERROR);
    }

    protected final void setOnline() {
        updateStatus(ThingStatus.ONLINE);
    }

    protected final void setOnlineConfigurationPending() {
        updateStatus(ThingStatus.ONLINE, CONFIGURATION_PENDING);
    }

    protected final void setStatus(ThingStatusKey key) {
        synchronized (this) {
            statusKey = key;
            var status = key.getStatus();
            if (status == null) {
                status = getStatus();
            }
            var detail = key.getStatusDetail();
            if (detail == null) {
                detail = thing.getStatusInfo().getStatusDetail();
            }
            updateStatus(status, detail, key.getDescription());
        }
    }

    protected final void setUnknown() {
        updateStatus(ThingStatus.UNKNOWN);
    }
}
