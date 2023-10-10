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
package org.openhab.binding.electric.internal.handler.price;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.common.openhab.thing.AbstractThingHandler;
import org.openhab.binding.electric.common.openhab.thing.InvalidBridge;
import org.openhab.core.thing.Thing;

import java.util.List;

import static org.openhab.binding.electric.internal.handler.StatusKey.MISSING_PRICE_BRIDGE;

/**
 * Represents a single electricity product and tariff that the company charges.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public abstract class PriceProvider<C> extends AbstractThingHandler<C> {
    /**
     * Creates a new instance of this class for the {@link Thing}.
     *
     * @param thing the thing that should be handled, not null
     * @param configurationClass the configuration class
     */
    public PriceProvider(Thing thing, Class<C> configurationClass) {
        super(thing, configurationClass);
    }

    protected PriceService getPriceService() {
        var bridge = getBridge();
        if (bridge != null && bridge.getHandler() instanceof PriceService service) {
            return service;
        }
        setStatus(MISSING_PRICE_BRIDGE);
        throw new InvalidBridge();
    }

    public abstract Product getProduct();

    /** Get current and future known prices. May be empty. */
    public abstract List<ProductPrice> getPrices();
}
