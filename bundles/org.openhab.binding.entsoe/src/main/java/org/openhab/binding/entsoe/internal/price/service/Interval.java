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
package org.openhab.binding.entsoe.internal.price.service;

import java.time.ZonedDateTime;

/**
 * Time interval interface.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
public interface Interval {
    ZonedDateTime start();

    ZonedDateTime end();

    default boolean contains(ZonedDateTime that) {
        // Times are usually iterated in chronological order so end test fails faster.
        // End is exclusive and start is inclusive.
        return that.compareTo(end()) < 0 && start().compareTo(that) <= 0;
    }
}
