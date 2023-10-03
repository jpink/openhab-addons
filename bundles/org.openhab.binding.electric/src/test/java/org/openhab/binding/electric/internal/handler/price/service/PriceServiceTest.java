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
package org.openhab.binding.electric.internal.handler.price.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.binding.electric.common.Time.set;
import static org.openhab.binding.electric.internal.handler.entsoe.EntsoeClientTest.*;
import static org.openhab.binding.electric.internal.handler.price.PriceConfigTest.HELSINKI;
import static org.openhab.binding.electric.internal.handler.price.PriceConfigTest.PRAGUE;
import static org.openhab.binding.electric.internal.handler.price.service.PriceService.*;

import java.time.ZonedDateTime;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

/**
 * Price service unit tests.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
class PriceServiceTest {
    @Test
    void publishedCz1245() {
        assertEquals("2015-12-31T12:45+01:00[Europe/Prague]",
                set(ZonedDateTime.of(NEW_YEAR, PRAGUE), PUBLISHED).toString());
    }

    @Test
    void publishedFi1245() {
        assertEquals("2015-12-31T13:45+02:00[Europe/Helsinki]",
                set(ZonedDateTime.of(NEW_YEAR, HELSINKI), PUBLISHED).toString());
    }
}
