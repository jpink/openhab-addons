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

import java.time.LocalDateTime;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.common.Interval;
import org.openhab.binding.electric.common.monetary.EnergyPrice;
import org.openhab.core.thing.binding.ThingHandler;

/**
 * Represents a single electricity product and tariff that the company charges.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public interface TariffContract extends Interval, ThingHandler {
    Product getProduct();

    EnergyPrice getPrice(LocalDateTime time);
}
