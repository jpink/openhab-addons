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
package org.openhab.binding.electric.internal;

import static org.openhab.binding.electric.internal.ElectricBindingConstants.*;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link ElectricHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.electric", service = ThingHandlerFactory.class)
public class ElectricHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED = Set.of(BRIDGE_TYPE_PRICE, THING_TYPE_SINGLE);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        var type = thing.getThingTypeUID();
        if (thing instanceof Bridge bridge) {
            if (BRIDGE_TYPE_PRICE.equals(type)) {
                return new PriceService(bridge);
            }
        } else {
            if (THING_TYPE_SINGLE.equals(type)) {
                return new SingleTimeTariff(thing);
            }
        }
        return null;
    }
}
