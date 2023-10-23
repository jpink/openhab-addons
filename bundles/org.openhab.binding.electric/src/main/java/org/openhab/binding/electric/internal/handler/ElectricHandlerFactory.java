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

import static org.openhab.binding.electric.internal.ElectricBindingConstants.*;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.common.openhab.thing.AbstractThingHandlerFactory;
import org.openhab.binding.electric.internal.handler.entsoe.EntsoeClient;
import org.openhab.binding.electric.internal.handler.fixed.FixedPrice;
import org.openhab.binding.electric.internal.handler.price.PriceService;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link ElectricHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@Component(configurationPid = "binding.electric", service = ThingHandlerFactory.class)
@NonNullByDefault
public class ElectricHandlerFactory extends AbstractThingHandlerFactory {
    @Activate
    public ElectricHandlerFactory(@Reference HttpClientFactory client, @Reference TimeZoneProvider zone) {
        super(Map.of( //
                BRIDGE_TYPE_PRICE, PriceService::new, //
                THING_TYPE_ENTSOE, thing -> new EntsoeClient(client, thing), //
                THING_TYPE_FIXED, FixedPrice::new));
    }
}
