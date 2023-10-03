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
import static org.openhab.binding.electric.internal.ElectricBindingConstants.THING_TYPE_SINGLE;

import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openhab.binding.electric.common.UnitTest;
import org.openhab.binding.electric.internal.handler.price.PriceService;
import org.openhab.binding.electric.internal.handler.single.SingleTimeTariff;
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
    /**
     * Create a new instance before each test.
     *
     * @return The instance to be tested.
     */
    @Override
    protected ElectricHandlerFactory create() {
        return new ElectricHandlerFactory();
    }

    static Stream<ThingTypeUID> supportsThingType() {
        return Stream.of(BRIDGE_TYPE_PRICE, THING_TYPE_SINGLE);
    }

    @ParameterizedTest
    @MethodSource
    void supportsThingType(ThingTypeUID type) {
        assertTrue(getInstance().supportsThingType(type));
    }

    static Stream<Arguments> createHandler() {
        return Stream.of(Arguments.of(true, BRIDGE_TYPE_PRICE, PriceService.class),
                Arguments.of(false, THING_TYPE_SINGLE, SingleTimeTariff.class));
    }

    @ParameterizedTest
    @MethodSource
    void createHandler(boolean bridge, ThingTypeUID type, Class<ThingHandler> expected) {
        var thing = bridge ? mock(Bridge.class) : mock(Thing.class);
        when(thing.getThingTypeUID()).thenReturn(type);

        var handler = getInstance().createHandler(thing);

        assertInstanceOf(expected, handler);
    }
}
