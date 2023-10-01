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
import org.openhab.core.thing.binding.ThingHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a single electricity tariff that the company charges.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public interface TariffContract extends ThingHandler {
    /** Is this an electricity distribution company. */
    boolean isDistributor();

    /** Is this an electricity sales company. */
    boolean isSeller();

    BigDecimal getTransferPrice(LocalDateTime time);

    BigDecimal getSalesPrice(LocalDateTime time);
}
