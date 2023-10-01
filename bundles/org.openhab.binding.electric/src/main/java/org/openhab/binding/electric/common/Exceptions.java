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

import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Kotlin style common exceptions.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Exceptions {
    /**
     * Checks that the required object exists.
     *
     * @param <T> The type of the required object.
     * @param object The required object instance.
     * @param message The error message supplier.
     * @throws IllegalArgumentException If the object is missing.
     */
    public static <T> T require(T object, Supplier<String> message) {
        if (object == null) {
            throw new IllegalArgumentException(message.get());
        }
        return object;
    }
}
