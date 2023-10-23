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

import static org.openhab.binding.electric.common.monetary.Monetary.EURO;
import static org.openhab.binding.electric.common.monetary.Monetary.currency;
import static org.openhab.binding.electric.common.monetary.Monetary.energyPriceUnit;
import static org.openhab.binding.electric.common.monetary.Monetary.moneyUnit;
import static org.openhab.binding.electric.common.monetary.Monetary.parseEnergyUnit;
import static org.openhab.binding.electric.common.monetary.Monetary.percent;
import static org.openhab.core.library.unit.Units.MEGAWATT_HOUR;

import java.math.BigDecimal;
import java.util.Currency;

import javax.measure.MetricPrefix;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.electric.common.monetary.EnergyPrice;
import org.openhab.binding.electric.common.openhab.thing.AbstractBridgeHandler;
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
        public @Nullable String currency;
        public @Nullable String subunit;
        public String energy = MEGAWATT_HOUR.toString();
        public BigDecimal tax = BigDecimal.ZERO;
        public int vat;
        public @Nullable Integer sales;
        public int precision = 7;
        public int scale = 2;
    }

    private @Nullable PriceProvider<?> distributor;
    private @Nullable PriceProvider<?> seller;

    private Currency currency = EURO;
    private Unit<EnergyPrice> unit = energyPriceUnit(currency, MEGAWATT_HOUR);
    private Quantity<Dimensionless> vatRate = percent(0);
    private Quantity<Dimensionless> salesVatRate = vatRate;

    public PriceService(Thing bridge) {
        super(bridge, Config.class);
    }

    @Override
    public void initialize() {
        try {
            var config = getConfiguration();
            currency = currency(config.currency);
            var money = moneyUnit(currency);
            var subunit = config.subunit;
            if (subunit != null && !subunit.isBlank()) {
                money = MetricPrefix.CENTI(money);
                // TODO register sub unit
            }
            unit = energyPriceUnit(money, parseEnergyUnit(config.energy));
            vatRate = percent(config.vat);
            var sales = config.sales;
            salesVatRate = sales == null ? vatRate : percent(sales);
        } catch (Exception e) {
            setOfflineConfigurationError();
            logger.error("Failed to initialize!", e);
        }
        setOffline();
    }

    public Currency getCurrency() {
        return currency;
    }

    public Unit<EnergyPrice> getUnit() {
        return unit;
    }

    public Quantity<Dimensionless> getVatRate(Product product) {
        return Product.SALES.equals(product) ? salesVatRate : vatRate;
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
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        var typeId = childThing.getThingTypeUID();
        if (childHandler instanceof PriceProvider<?> tariff) {
            switch (tariff.getProduct()) {
                case TRANSFER -> {
                    var distributor = this.distributor;
                    if (distributor == null) {
                        this.distributor = tariff;
                        logger.info("Assembled {} as a distributor.", typeId.getId());
                    } else {
                        logger.error("Ignored {} distributor because {} is already the distributor!", typeId.getId(),
                                distributor.getThing().getThingTypeUID().getId());
                    }
                }
                case SALES -> {
                    var seller = this.seller;
                    if (seller == null) {
                        this.seller = tariff;
                        logger.info("Assembled {} as a seller.", typeId.getId());
                    } else {
                        logger.error("Ignored {} seller because {} is already the seller!", typeId.getId(),
                                seller.getThing().getThingTypeUID().getId());
                    }
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
