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
package org.openhab.binding.electric.internal.handler.fixed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openhab.binding.electric.common.monetary.Monetary.EURO_PER_MEGAWATT_HOUR;
import static org.openhab.binding.electric.common.monetary.Monetary.ZERO;
import static org.openhab.binding.electric.common.monetary.Monetary.taxPrice;
import static org.openhab.binding.electric.internal.ElectricBindingConstants.BRIDGE_TYPE_PRICE;
import static org.openhab.binding.electric.internal.ElectricBindingConstants.THING_TYPE_FIXED;
import static org.openhab.binding.electric.internal.handler.StatusKey.MISSING_PRICE;

import java.util.Collections;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.openhab.binding.electric.internal.handler.ElectricHandlerTest;
import org.openhab.binding.electric.internal.handler.price.Product;
import org.openhab.binding.electric.internal.handler.price.ProductPrice;
import org.threeten.extra.Interval;

/**
 * Single-time tariff unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
class FixedPriceTest extends ElectricHandlerTest<FixedPrice> {

    protected FixedPriceTest() {
        super(THING_TYPE_FIXED, BRIDGE_TYPE_PRICE);
    }

    @Test
    public void initializeWhenDefaultThenMissingPrice() {
        initialize();

        assertStatus(MISSING_PRICE);
    }

    @Test
    public void initializeWhenPriceThenOnline() {
        setParameter("price", 3.4);

        initialize();

        assertOnline();
    }

    @Test
    public void getProductWhenDefaultThenTransfer() {
        assertEquals(Product.TRANSFER, initialize().getProduct());
    }

    @Test
    public void getProductWhenSalesThenSales() {
        setParameter("product", "SALES");
        assertEquals(Product.SALES, initialize().getProduct());
    }

    @Test
    public void getProductWhenTransferThenTransfer() {
        setParameter("product", "TRANSFER");
        assertEquals(Product.TRANSFER, initialize().getProduct());
    }

    @Test
    public void getPricesWhenDefaultThenEmpty() {
        assertTrue(initialize().getPrices().isEmpty());
    }

    @Test
    public void getPricesWhenPriceThenSingle() {
        var price = 3.4;
        setParameter("price", price);
        var expected = Collections.singletonList(
                new ProductPrice(Product.TRANSFER, taxPrice(price, EURO_PER_MEGAWATT_HOUR, ZERO), Interval.ALL));

        var prices = initialize().getPrices();

        assertEquals(expected, prices);
    }
}
