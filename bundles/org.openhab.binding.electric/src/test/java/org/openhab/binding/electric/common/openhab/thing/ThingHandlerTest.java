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

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openhab.core.thing.util.ThingHandlerHelper.isHandlerInitialized;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.openhab.binding.electric.common.Reflections;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.openhab.binding.electric.common.osgi.ComponentTest;
import org.openhab.core.config.core.ConfigDescription;
import org.openhab.core.config.core.ConfigDescriptionRegistry;
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
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.BridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerCallback;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.openhab.core.thing.binding.builder.BridgeBuilder;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.openhab.core.thing.type.ChannelGroupTypeUID;
import org.openhab.core.thing.type.ChannelTypeRegistry;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.openhab.core.thing.type.ThingTypeRegistry;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;

/**
 * Abstract thing handler unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
//@ExtendWith(OsgiContextExtension.class)
//@ExtendWith(MockitoExtension.class)
@NonNullByDefault({})
public abstract class ThingHandlerTest<I extends ThingHandler, @Nullable C extends AbstractThingHandlerFactory>
        extends ComponentTest<I, C> {
    public static class Callback<I extends ThingHandler, @Nullable C extends AbstractThingHandlerFactory>
            implements ThingHandlerCallback {
        private final ThingHandlerTest<I, C> test;

        public Callback(ThingHandlerTest<I, C> test) {
            this.test = test;
        }

        @Override
        public void stateUpdated(ChannelUID channelUID, State state) {
        }

        @Override
        public void postCommand(ChannelUID channelUID, Command command) {
        }

        @Override
        public void statusUpdated(Thing thing, ThingStatusInfo thingStatusInfo) {
            thing.setStatusInfo(thingStatusInfo);
        }

        @Override
        public void thingUpdated(Thing thing) {
        }

        @Override
        public void validateConfigurationParameters(Thing thing, Map<String, Object> map) {
        }

        @Override
        public void validateConfigurationParameters(Channel channel, Map<String, Object> map) {
        }

        @Override
        public @Nullable ConfigDescription getConfigDescription(ChannelTypeUID channelTypeUID) {
            return null;
        }

        @Override
        public @Nullable ConfigDescription getConfigDescription(ThingTypeUID thingTypeUID) {
            return null;
        }

        @Override
        public void configurationUpdated(Thing thing) {
        }

        @Override
        public void migrateThingType(Thing thing, ThingTypeUID thingTypeUID, Configuration configuration) {
        }

        @Override
        public void channelTriggered(Thing thing, ChannelUID channelUID, String s) {
        }

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
            var bridge = test.bridge;
            return bridge != null && bridge.getUID().equals(thingUID) ? bridge : null;
        }
    }

    private static Bridge createBridge(ThingTypeUID type) {
        return createBridge(type, "test");
    }

    private static Bridge createBridge(ThingTypeUID type, String bridgeId) {
        return BridgeBuilder.create(type, bridgeId).build();
    }

    private static Thing createThing(ThingTypeUID type) {
        return createThing(type, "test");
    }

    private static Thing createThing(ThingTypeUID type, String thingId) {
        return ThingBuilder.create(type, thingId).build();
    }

    private final ThingTypeUID thingType;
    private final @Nullable ThingTypeUID bridgeType;
    private final Configuration bridgeConfiguration = new Configuration();
    protected Function<ThingTypeUID, BridgeHandler> bridgeHandlerFactory =
            type -> (BridgeHandler) create(type, bridgeConfiguration);
    private final Configuration configuration = new Configuration();

    @Deprecated
    private @Nullable Bridge bridge;

    protected final Callback<I, C> callback = new Callback<>(this);

    protected ThingHandlerTest(ThingTypeUID type) {
        this(type, null);
    }

    protected ThingHandlerTest(ThingTypeUID type, @Nullable ThingTypeUID bridge) {
        thingType = type;
        bridgeType = bridge;
        var channelTypeRegistry = new ChannelTypeRegistry();
        registerService(ChannelTypeRegistry.class, channelTypeRegistry);
        registerService(ThingTypeRegistry.class, new ThingTypeRegistry(channelTypeRegistry));
        registerService(ConfigDescriptionRegistry.class, new ConfigDescriptionRegistry());
        getComponent().activate(getComponentContext());
    }

    @Override
    protected void configureComponent() {}

    @Override
    @SuppressWarnings("unchecked")
    public final I create() {
        ThingUID bridgeId = null;
        var bridgeType = this.bridgeType;
        if (bridgeType != null) {
            bridgeId = bridgeHandlerFactory.apply(bridgeType).getThing().getUID();
        }
        return (I) create(thingType, configuration, bridgeId);
        /*var instance = create(thing);
        assertEquals(thing, instance.getThing());
        instance.setCallback(callback);
        thing.setHandler(instance);
        return instance;*/
    }

    /**
     * Create a new thing handler instance before each test.
     *
     * @param thing Mock of the thing object.
     * @return The thing handler to be tested.
     */
    @Deprecated
    protected I create(Thing thing) {
        var testClass = getClass().getName();
        return Reflections.create(Reflections.constructor(testClass.substring(0, testClass.length() - 4), Thing.class),
                thing);
    }

    protected ThingHandler create(ThingTypeUID type) {
        return create(type, new Configuration());
    }

    protected ThingHandler create(ThingTypeUID type, Configuration configuration) {
        return create(type, configuration, null);
    }

    protected ThingHandler create(ThingTypeUID type, Configuration configuration, @Nullable ThingUID bridgeUID) {
        var factory = getComponent();
        var thing = factory.createThing(type, configuration, null, bridgeUID);
        if (thing == null) throw new IllegalStateException("Unable create a thing!");
        return factory.registerHandler(thing);
    }

    protected BridgeHandler createBridge(ThingTypeUID type, Configuration configuration) {
        return (BridgeHandler) create(type, configuration);
    }

//    protected void setBridge(Class<? extends BridgeHandlerTest<?, ?>> type) {
//        var test = Reflections.create(type);
//        var handler = test.create();
//        handler.initialize();
//        bridge = (Bridge) handler.getThing();
//        thing.setBridgeUID(bridge.getUID());
//    }

    protected void setBridgeParameter(String name, Object value) {
        bridgeConfiguration.put(name, value);
    }

    protected void setParameter(String name, Object value) {
        configuration.put(name, value);
    }

    protected I initialize() {
        var instance = getInstance();
        instance.initialize();
        return instance;
    }

    @AfterEach
    public void tearDown()
    {
        getInstance().dispose();
    }

    @Test
    public void initializeWhenDefaultThenInitialized() {
        var instance = getInstance();

        instance.initialize();

        assertTrue(isHandlerInitialized(instance), "Initialized status must be UNKNOWN, ONLINE or OFFLINE!");
    }

    private ThingStatus getStatus() {
        return getThing().getStatus();
    }

    private ThingStatusDetail getStatusDetail() {
        return getStatusInfo().getStatusDetail();
    }

    private ThingStatusInfo getStatusInfo() {
        return getThing().getStatusInfo();
    }

    private @Nullable String getStatusDescription() {
        return getStatusInfo().getDescription();
    }

    private Thing getThing() {
        return getInstance().getThing();
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
