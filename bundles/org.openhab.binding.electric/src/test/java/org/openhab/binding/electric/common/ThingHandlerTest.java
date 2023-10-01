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
package org.openhab.binding.electric.common;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.openhab.core.config.core.ConfigDescription;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingHandlerCallback;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.openhab.core.thing.type.ChannelGroupTypeUID;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract thing handler unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public abstract class ThingHandlerTest<I extends AbstractThingHandler<C>, C> extends UnitTest<I> {
    public static class Callback<I extends AbstractThingHandler<C>, C> implements ThingHandlerCallback {
        private final ThingHandlerTest<I, C> test;

        public Callback(ThingHandlerTest<I, C> test) {
            this.test = test;
        }

        @Override
        public void stateUpdated(ChannelUID channelUID, State state) {}

        @Override
        public void postCommand(ChannelUID channelUID, Command command) {}

        @Override
        public void statusUpdated(Thing thing, ThingStatusInfo thingStatusInfo) {
            thing.setStatusInfo(thingStatusInfo);
        }

        @Override
        public void thingUpdated(Thing thing) {}

        @Override
        public void validateConfigurationParameters(Thing thing, Map<String, Object> map) {}

        @Override
        public void validateConfigurationParameters(Channel channel, Map<String, Object> map) {}

        @Override
        public @Nullable ConfigDescription getConfigDescription(ChannelTypeUID channelTypeUID) {
            return null;
        }

        @Override
        public @Nullable ConfigDescription getConfigDescription(ThingTypeUID thingTypeUID) {
            return null;
        }

        @Override
        public void configurationUpdated(Thing thing) {}

        @Override
        public void migrateThingType(Thing thing, ThingTypeUID thingTypeUID, Configuration configuration) {}

        @Override
        public void channelTriggered(Thing thing, ChannelUID channelUID, String s) {}

        @Override
        public ChannelBuilder createChannelBuilder(ChannelUID channelUID, ChannelTypeUID channelTypeUID) {
            return ChannelBuilder.create(channelUID).withType(channelTypeUID);
        }

        @Override
        public ChannelBuilder editChannel(Thing thing, ChannelUID channelUID) {
            return ChannelBuilder.create(channelUID);
        }

        @Override
        public List<ChannelBuilder> createChannelBuilders(ChannelGroupUID channelGroupUID,
                ChannelGroupTypeUID channelGroupTypeUID) {
            return emptyList();
        }

        @Override
        public boolean isChannelLinked(ChannelUID channelUID) {
            return false;
        }

        @Override
        public @Nullable Bridge getBridge(ThingUID thingUID) {
            return null;
        }
    }

    protected final Thing thing;

    protected final Callback<I, C> callback = new Callback<>(this);

    protected ThingHandlerTest(Thing thing) {
        this.thing = thing;
    }

    protected ThingHandlerTest(ThingTypeUID type) {
        this(ThingBuilder.create(type, "test").build());
    }

    @Override
    protected final I create() {
        var instance = create(thing);
        assertEquals(thing, instance.getThing());
        instance.setCallback(callback);
        return instance;
    }

    /**
     * Create a new thing handler instance before each test.
     *
     * @param thing Mock of the thing object.
     * @return The thing handler to be tested.
     */
    protected abstract I create(Thing thing);

    protected void setParameter(String name, Object value) {
        thing.getConfiguration().put(name, value);
    }

    protected void initialize() {
        getInstance().initialize();
    }

    @AfterEach
    public void tearDown() {
        getInstance().dispose();
    }

    @org.junit.jupiter.api.Test
    public void initializeWhenDefaultThenInitialized() {
        var instance = getInstance();

        instance.initialize();

        assertTrue(instance.isInitialized(), "Initialized status must be UNKNOWN, ONLINE or OFFLINE!");
    }

    private ThingStatus getStatus() {
        return thing.getStatus();
    }

    private ThingStatusDetail getStatusDetail() {
        return getStatusInfo().getStatusDetail();
    }

    private ThingStatusInfo getStatusInfo() {
        return thing.getStatusInfo();
    }

    private @Nullable String getStatusDescription() {
        return getStatusInfo().getDescription();
    }

    protected void assertOffline() {
        assertStatus(ThingStatus.OFFLINE);
    }

    protected void assertOnline() {
        assertStatus(ThingStatus.ONLINE);
    }

    protected void assertUnknown() {
        assertStatus(ThingStatus.UNKNOWN);
    }

    private void assertStatus(ThingStatus expected) {
        assertEquals(expected, getStatus());
    }

    private void assertStatus(ThingStatusDetail expected) {
        assertEquals(expected, getStatusDetail());
        var status = ThingStatusKey.STATUS_BY_DETAIL.get(expected);
        if (status != null) {
            assertStatus(status);
        }
    }

    protected void assertStatus(ThingStatusKey key) {
        assertEquals(getStatusDescription(), key.getDescription());
        var detail = key.getStatusDetail();
        if (detail == null) {
            var status = key.getStatus();
            if (status != null) {
                assertStatus(status);
            }
        } else {
            assertStatus(detail);
        }
    }
}
