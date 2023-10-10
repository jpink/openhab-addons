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
package org.openhab.binding.electric.internal.handler.entsoe.dto;

import java.time.ZonedDateTime;

import org.openhab.binding.electric.common.Interval;

/**
 * Time interval data transfer object.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
public class TimeInterval implements Interval {
    public ZonedDateTime start;
    public ZonedDateTime end;

    @Override
    public ZonedDateTime start() {
        return start;
    }

    @Override
    public ZonedDateTime end() {
        return end;
    }

    @Override
    public String toString() {
        return start + "/" + end;
    }
}
