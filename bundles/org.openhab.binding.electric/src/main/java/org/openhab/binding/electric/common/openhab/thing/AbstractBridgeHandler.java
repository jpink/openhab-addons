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
import org.openhab.core.thing.binding.BridgeHandler;
import org.openhab.core.thing.binding.builder.BridgeBuilder;

/**
 * Abstract bridge handler.
 *
 * @author Jukka Papinkivi - Initial contribution
 * @see org.openhab.core.thing.binding.BaseBridgeHandler
 */
@NonNullByDefault
public abstract class AbstractBridgeHandler<C> extends AbstractThingHandler<C> implements BridgeHandler {

    public AbstractBridgeHandler(Bridge bridge, Class<C> configurationClass) {
        super(bridge, configurationClass);
    }

    @Override
    public final Bridge getThing() {
        return (Bridge) thing;
    }

    protected BridgeBuilder editThing() {
        return BridgeBuilder.create(thing.getThingTypeUID(), thing.getUID()).withBridge(thing.getBridgeUID())
                .withChannels(thing.getChannels()).withConfiguration(thing.getConfiguration())
                .withLabel(thing.getLabel()).withLocation(thing.getLocation()).withProperties(thing.getProperties());
    }
}
