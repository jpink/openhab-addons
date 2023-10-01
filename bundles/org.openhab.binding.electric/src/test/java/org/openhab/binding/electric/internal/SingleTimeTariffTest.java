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
import org.junit.jupiter.api.Test;
import org.openhab.binding.electric.common.ThingHandlerTest;
import org.openhab.core.thing.Thing;

import static org.openhab.binding.electric.internal.ElectricBindingConstants.THING_TYPE_SINGLE;
import static org.openhab.binding.electric.internal.StatusKey.MISSING_PRICE;

/**
 * Single-time tariff unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
class SingleTimeTariffTest extends ThingHandlerTest<SingleTimeTariff, SingleTimeTariff.Config> {

    protected SingleTimeTariffTest() {
        super(THING_TYPE_SINGLE);
    }

    /**
     * Create a new thing handler instance before each test.
     *
     * @param thing Mock of the thing object.
     * @return The thing handler to be tested.
     */
    @Override
    protected SingleTimeTariff create(Thing thing) {
        return new SingleTimeTariff(thing);
    }

    @Test
    public void initializeWhenDefaultThenMissingPrice() {
        initialize();

        assertStatus(MISSING_PRICE);
    }

    @Test
    public void initializeWhenTransferPriceThenOnline() {
        setParameter("transfer", 3.4);

        initialize();

        assertOnline();
    }
}