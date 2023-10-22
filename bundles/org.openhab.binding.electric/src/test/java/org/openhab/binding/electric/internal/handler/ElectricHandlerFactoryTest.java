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
package org.openhab.binding.electric.internal.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openhab.binding.electric.internal.ElectricBindingConstants.BRIDGE_TYPE_PRICE;
import static org.openhab.binding.electric.internal.ElectricBindingConstants.THING_TYPE_ENTSOE;
import static org.openhab.binding.electric.internal.ElectricBindingConstants.THING_TYPE_FIXED;

import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.openhab.binding.electric.common.UnitTest;
import org.openhab.binding.electric.internal.handler.entsoe.EntsoeClient;
import org.openhab.binding.electric.internal.handler.price.PriceService;
import org.openhab.binding.electric.internal.handler.fixed.FixedPrice;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.ThingHandler;

/**
 * Electric handler factory unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
class ElectricHandlerFactoryTest extends UnitTest<ElectricHandlerFactory> {
    HttpClientFactory client = mock();

    TimeZoneProvider zone = mock();

    /**
     * Create a new instance before each test.
     *
     * @return The instance to be tested.
     */
    @Override
    protected ElectricHandlerFactory create() {
        return new ElectricHandlerFactory(client, zone);
    }

    static Stream<ThingTypeUID> supportsThingType() {
        return Stream.of(BRIDGE_TYPE_PRICE, THING_TYPE_ENTSOE, THING_TYPE_FIXED);
    }

    @ParameterizedTest
    @MethodSource
    void supportsThingType(ThingTypeUID type) {
        assertTrue(getInstance().supportsThingType(type));
    }

    static Stream<Arguments> registerHandler() {
        return Stream.of(Arguments.of(true, BRIDGE_TYPE_PRICE, PriceService.class),
                Arguments.of(true, THING_TYPE_ENTSOE, EntsoeClient.class),
                Arguments.of(false, THING_TYPE_FIXED, FixedPrice.class));
    }

    @ParameterizedTest
    @MethodSource
    void registerHandler(boolean bridge, ThingTypeUID type, Class<ThingHandler> expected) {
        var thing = bridge ? mock(Bridge.class) : mock(Thing.class);
        when(thing.getThingTypeUID()).thenReturn(type);

        var handler = getInstance().registerHandler(thing);

        assertInstanceOf(expected, handler);
    }
}
