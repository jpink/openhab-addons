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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Text helper functions.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Text {
    public static String[] splitWhitespace(@Nullable String str) {
        return str == null ? new String[0] : str.split("\\s");
    }

    public static String toExponent(int exponent) {
        return switch (exponent) {
            case 1 -> ""; // Could be '¹' but useless.
            case 2 -> "²";
            case 3 -> "³";
            default -> "^" + exponent;
        };
    }
}
