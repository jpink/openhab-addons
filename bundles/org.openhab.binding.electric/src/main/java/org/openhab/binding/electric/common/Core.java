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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Java language extensions.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Core {
    /**
     * The elvis operator like `?:` in Kotlin. Equal as ternary operator `value == null ? defaultValue : value` if the
     * default value is existing variable or constant.
     *
     * @param value The value which is tested against null.
     * @param defaultValue The non-null value which is returned if the tested value is null.
     * @return A non-null value.
     */
    public static <T> T elvis(@Nullable T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    @SuppressWarnings("unchecked")
    public static <T> T uncheckedCast(Object object) {
        return (T) object;
    }
}
