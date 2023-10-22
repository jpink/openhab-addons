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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.electric.common.openhab.thing.ThingHandlerTest;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.framework.ServiceRegistration;

import static org.mockito.Mockito.mock;
import static org.openhab.binding.electric.common.Core.uncheckedCast;

/**
 * Abstract electric handler unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public abstract class ElectricHandlerTest<I extends ThingHandler> extends ThingHandlerTest<I, @Nullable ElectricHandlerFactory> {
    private final HttpClientFactory client = mock();

    private final TimeZoneProvider zone = mock();

    protected ElectricHandlerTest(ThingTypeUID type) {
        this(type, null);
    }

    protected ElectricHandlerTest(ThingTypeUID type, @Nullable ThingTypeUID bridge) {
        super(type, bridge);
    }

    @Override
    protected ServiceRegistration<ElectricHandlerFactory> registerComponent() {
        return uncheckedCast(registerService(ThingHandlerFactory.class, new ElectricHandlerFactory(client, zone)));
    }
}
