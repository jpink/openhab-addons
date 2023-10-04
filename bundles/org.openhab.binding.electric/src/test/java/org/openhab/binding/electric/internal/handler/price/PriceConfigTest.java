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

import static org.openhab.binding.electric.internal.ElectricBindingConstants.UNIT_CENT_PER_KWH;

import java.math.BigDecimal;
import java.time.ZoneId;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Price config unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class PriceConfigTest {
    public static final ZoneId HELSINKI = ZoneId.of("Europe/Helsinki"), //
            PRAGUE = ZoneId.of("Europe/Prague");
    public static final BigDecimal FI_TRANSFER = BigDecimal.valueOf(3.4), // ESE-Verkko Oy
            FI_TAX = BigDecimal.valueOf(2.79), // The energy tax in Finland is actually 2.79372 c/kWh.
            FI_MARGIN = BigDecimal.valueOf(0.25); // Oomi Oy
    public static final int CZ_GENERAL = 21, //
            FI_GENERAL = 24, //
            FI_SELLER_2022 = 10; // Temporary VAT rate in Finland at winter 2022-2023.
    public static final PriceConfig CZ_CONFIG = new PriceConfig(PRAGUE, UNIT_CENT_PER_KWH, FI_TRANSFER, FI_TAX,
            FI_MARGIN, CZ_GENERAL, CZ_GENERAL), //
            FI_CONFIG = new PriceConfig(HELSINKI, UNIT_CENT_PER_KWH, FI_TRANSFER, FI_TAX, FI_MARGIN, FI_GENERAL,
                    FI_GENERAL), //
            FI_CONFIG_2022 = new PriceConfig(HELSINKI, UNIT_CENT_PER_KWH, FI_TRANSFER, FI_TAX, FI_MARGIN, FI_GENERAL,
                    FI_SELLER_2022);
}
