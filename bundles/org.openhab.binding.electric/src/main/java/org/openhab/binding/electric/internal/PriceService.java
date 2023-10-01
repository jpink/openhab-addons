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
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.binding.ThingHandler;

/**
 * Price service.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class PriceService extends AbstractBridgeHandler<PriceService.Config> {
    public static class Config {

    }

    private @Nullable TariffContract distributor;
    private @Nullable TariffContract seller;
    public PriceService(Bridge bridge) {
        super(bridge, Config.class);
    }

    private void checkStatus() {
        synchronized (this) {
            if (distributor == null && seller == null) {
                setOffline();
            } else if (distributor == null || seller == null) {
                setOnlineConfigurationPending();
            } else {
                setOnline();
            }
        }
    }

    @Override
    public void initialize() {
        setOffline();
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        var typeId = childThing.getThingTypeUID();
        if (childHandler instanceof TariffContract tariff) {
            if (tariff.isDistributor()) {
                if (distributor == null) {
                    distributor = tariff;
                    logger.info("Assembled {} as a distributor.", typeId.getId());
                } else {
                    logger.error("Ignored {} distributor because {} is already the distributor!",
                            typeId.getId(),
                            distributor.getThing().getThingTypeUID().getId());
                }
            }
            if (tariff.isSeller()) {
                if (seller == null) {
                    seller = tariff;
                    logger.info("Assembled {} as a seller.", typeId.getId());
                } else {
                    logger.error("Ignored {} seller because {} is already the seller!",
                            typeId.getId(),
                            seller.getThing().getThingTypeUID().getId());
                }
            }
            checkStatus();
        } else {
            logger.warn("Ignored unsupported {} thing!", typeId.getAsString());
        }
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        var typeId = childThing.getThingTypeUID().getId();
        if (childHandler.equals(distributor)) {
            distributor = null;
            logger.info("Disassembled the {} distributor.", typeId);
        }
        if (childHandler.equals(seller)) {
            seller = null;
            logger.info("Disassembled the {} seller.", typeId);
        }
        checkStatus();
    }
}
