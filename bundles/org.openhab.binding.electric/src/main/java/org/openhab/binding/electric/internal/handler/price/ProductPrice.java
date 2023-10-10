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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.common.HasInterval;
import org.openhab.binding.electric.common.monetary.EnergyPrice;
import org.openhab.binding.electric.common.monetary.TaxPrice;
import org.threeten.extra.Interval;

/**
 * Product price
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public record ProductPrice(Product product, TaxPrice<EnergyPrice> price, Interval interval) implements HasInterval {}
