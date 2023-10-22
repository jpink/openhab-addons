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

import static java.util.Collections.emptyEnumeration;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Collection extensions.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Collections {
    public static <E> Enumeration<E> empty(@Nullable Enumeration<E> enumeration) {
        return enumeration == null ? emptyEnumeration() : enumeration;
    }
    public static <E> @Nullable E find(Collection<E> collection, Predicate<? super E> predicate) {
        return collection.stream().filter(predicate).findFirst().orElse(null);
    }

    public static <E> E first(Collection<E> collection, Predicate<? super E> predicate) {
        return collection.stream().filter(predicate).findFirst().orElseThrow();
    }

    public static <E> boolean isEmpty(E[] array) {
        return array.length == 0;
    }

    public static <E> E @Nullable[] nullify(E[] array) {
        return array.length == 0 ? null : array;
    }

    public static <E> E @Nullable[] nullify(Collection<E> collection, IntFunction<E[]> generator) {
        return collection.isEmpty() ? null : collection.toArray(generator);
    }

    public static <E extends Comparable<? super E>> List<E> sort(List<E> list) {
        var sorted = new ArrayList<>(list);
        java.util.Collections.sort(sorted);
        return unmodifiableList(sorted);
    }
}
