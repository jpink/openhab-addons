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

import static org.openhab.binding.electric.common.monetary.Monetary.taxPriceOfSum;
import static org.openhab.binding.electric.internal.handler.StatusKey.MISSING_PRICE;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.internal.handler.price.PriceProvider;
import org.openhab.binding.electric.internal.handler.price.Product;
import org.openhab.binding.electric.internal.handler.price.ProductPrice;
import org.openhab.core.thing.Thing;
import org.threeten.extra.Interval;

/**
 * Single-time tariff product price provider.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class SingleTimeTariff extends PriceProvider<SingleTimeTariff.Config> {
    public static class Config {
        Product product = Product.TRANSFER;
        BigDecimal price = BigDecimal.ZERO;
    }

    private Product product = Product.TRANSFER;
    private List<ProductPrice> prices = Collections.emptyList();

    public SingleTimeTariff(Thing thing) {
        super(thing, Config.class);
    }

    @Override
    public void initialize() {
        var config = getConfiguration();
        product = config.product;
        var price = config.price;
        if (BigDecimal.ZERO.equals(price)) {
            setStatus(MISSING_PRICE);
        } else {
            var service = getPriceService();
            prices = Collections.singletonList(new ProductPrice(product,
                    taxPriceOfSum(config.price, service.getUnit(), service.getVatRate(product)), Interval.ALL));
            setOnline();
        }
    }

    @Override
    public Product getProduct() {
        return product;
    }

    /** Get current and future known prices. May be empty. */
    @Override
    public List<ProductPrice> getPrices() {
        return prices;
    }
}
