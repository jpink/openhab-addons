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

import java.util.Locale;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Text helper functions.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Text {
    public static String[] EMPTY_ARRAY = new String[0];

    public static boolean isBlank(@Nullable String value) {
        return value == null || value.isBlank();
    }

    public static String[] splitWhitespace(@Nullable String str) {
        return str == null ? EMPTY_ARRAY : str.split("\\s");
    }

    public static String toExponent(int exponent) {
        return switch (exponent) {
            case 1 -> ""; // Could be '¹' but useless.
            case 2 -> "²";
            case 3 -> "³";
            default -> "^" + exponent;
        };
    }

    public static String toIdentityHashcodeHex(Object x) {
        return Integer.toHexString(System.identityHashCode(x));
    }

    /** Generates translation key from enumeration name. */
    public static String toTranslationKey(Enum<?> enumeration) {
        return toTranslationKey(enumeration.name());
    }

    /** Converts to translation key. */
    public static String toTranslationKey(String name) {
        return name.toLowerCase(Locale.ROOT).replace('_', '-');
    }

    public static String decapitalize(String text) {
        return text.isEmpty() ? text : text.substring(0, 1).toLowerCase() + text.substring(1);
    }
}
