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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.builder.BridgeBuilder;

/**
 * Abstract bridge handler unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public abstract class BridgeHandlerTest<I extends AbstractBridgeHandler<C>, C> extends ThingHandlerTest<I, C> {

    protected final Bridge bridge = (Bridge) thing;

    protected BridgeHandlerTest(ThingTypeUID type) {
        super(BridgeBuilder.create(type, "test").build());
    }

    /**
     * Create a new bridge handler instance before each test.
     *
     * @param bridge Mock of the bridge object.
     * @return The bridge handler to be tested.
     */
    protected abstract I create(Bridge bridge);

    protected final I create(Thing thing) {
        return create(bridge);
    }
}
