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
package org.openhab.binding.electric.internal.handler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.electric.common.openhab.thing.ThingStatusKey;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;

/**
 * Thing status keys.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public enum StatusKey implements ThingStatusKey {
    MISSING_PRICE(ThingStatusDetail.CONFIGURATION_ERROR),
    MISSING_PRICE_BRIDGE(ThingStatusDetail.CONFIGURATION_ERROR);

    private final @Nullable ThingStatus status;
    private final @Nullable ThingStatusDetail detail;

    StatusKey() {
        this(null, null);
    }

    StatusKey(ThingStatusDetail detail) {
        this(STATUS_BY_DETAIL.get(detail), detail);
    }

    StatusKey(@Nullable ThingStatus status, @Nullable ThingStatusDetail detail) {
        this.status = status;
        this.detail = detail;
    }

    @Override
    public @Nullable ThingStatus getStatus() {
        return status;
    }

    @Override
    public @Nullable ThingStatusDetail getStatusDetail() {
        return detail;
    }
}
