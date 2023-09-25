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
package org.openhab.binding.entsoe.internal.common;

import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Time handling helper functions.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Time {
    public static ZonedDateTime convert(ZonedDateTime time, ZoneId zone) {
        return time.withZoneSameInstant(zone);
    }

    public static OffsetTime convert(OffsetTime time, ZoneOffset zone) {
        return time.withOffsetSameInstant(zone);
    }

    public static ZonedDateTime evenHour() {
        var now = ZonedDateTime.now();
        return ZonedDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), 0, 0, 0,
                now.getZone());
    }

    public static boolean gone(ZonedDateTime time) {
        return time.isBefore(ZonedDateTime.now());
    }

    public static ZonedDateTime set(ZonedDateTime dateTime, OffsetTime time) {
        return dateTime.with(convert(time, dateTime.getOffset()));
    }

    public static ZonedDateTime utc(ZonedDateTime time) {
        return convert(time, ZoneOffset.UTC);
    }
}
