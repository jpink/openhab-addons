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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.electric.common.AbstractBridgeHandler;
import org.openhab.binding.electric.common.AbstractThingHandler;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.openhab.binding.electric.internal.StatusKey.MISSING_PRICE;

/**
 * Price service.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class SingleTimeTariff extends AbstractThingHandler<SingleTimeTariff.Config> implements TariffContract {
    public static class Config {
        @Nullable BigDecimal transfer;
        @Nullable BigDecimal selling;
    }

    private @Nullable BigDecimal transfer;
    private @Nullable BigDecimal selling;

    public SingleTimeTariff(Thing thing) {
        super(thing, Config.class);
    }

    @Override
    public void initialize() {
        var config = getConfiguration();
        transfer = config.transfer;
        selling = config.selling;
        if (transfer == null && selling == null) {
            setStatus(MISSING_PRICE);
        } else {
            setOnline();
        }
    }

    /** Is this an electricity distribution company. */
    @Override
    public boolean isDistributor() {
        return transfer != null;
    }

    /** Is this an electricity sales company. */
    @Override
    public boolean isSeller() {
        return selling != null;
    }

    @Override
    public BigDecimal getTransferPrice(LocalDateTime time) {
        var price = transfer;
        if (price == null) {
            throw new UnsupportedOperationException();
        }
        return price;
    }

    @Override
    public BigDecimal getSalesPrice(LocalDateTime time) {
        var price = selling;
        if (price == null) {
            throw new UnsupportedOperationException();
        }
        return price;
    }
}
