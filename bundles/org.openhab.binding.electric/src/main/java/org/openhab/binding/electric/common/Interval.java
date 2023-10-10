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
package org.openhab.binding.electric.common;

import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Zoned time interval interface.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@Deprecated
@NonNullByDefault
public interface Interval {
    ZonedDateTime start();

    default boolean contains(ZonedDateTime time) {
        return time.isBefore(end()) && !time.isBefore(start());
    }

    ZonedDateTime end();

    default Duration duration() {
        return Duration.between(start(), end());
    }

    default Period period() {
        var end = end();
        var endDate = end.toLocalDate();
        if (end.toLocalTime().isAfter(LocalTime.MIN)) {
            endDate = endDate.plusDays(1);
        }
        return Period.between(start().toLocalDate(), endDate);
    }
}
