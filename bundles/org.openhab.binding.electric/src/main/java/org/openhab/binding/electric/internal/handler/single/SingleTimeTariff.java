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
package org.openhab.binding.electric.internal.handler.single;

import static org.openhab.binding.electric.internal.handler.StatusKey.MISSING_PRICE;

import java.math.BigDecimal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.common.openhab.thing.AbstractThingHandler;
import org.openhab.binding.electric.internal.handler.price.Product;
import org.openhab.core.thing.Thing;

/**
 * Single-time tariff product price provider.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class SingleTimeTariff extends AbstractThingHandler<SingleTimeTariff.Config> {
    public static class Config {
        Product product = Product.TRANSFER;
        BigDecimal price = BigDecimal.ZERO;
    }

    private BigDecimal price = BigDecimal.ZERO;

    public SingleTimeTariff(Thing thing) {
        super(thing, Config.class);
    }

    @Override
    public void initialize() {
        price = getConfiguration().price;
        if (BigDecimal.ZERO.equals(price)) {
            setStatus(MISSING_PRICE);
        } else {
            setOnline();
        }
    }
}
