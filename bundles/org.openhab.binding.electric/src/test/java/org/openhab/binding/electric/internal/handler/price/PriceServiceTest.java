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

import static org.openhab.binding.electric.internal.ElectricBindingConstants.BRIDGE_TYPE_PRICE;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.common.openhab.thing.BridgeHandlerTest;
import org.openhab.core.thing.Bridge;

/**
 * Price service unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class PriceServiceTest extends BridgeHandlerTest<PriceService, PriceService.Config> {

    public PriceServiceTest() {
        super(BRIDGE_TYPE_PRICE);
    }
}
