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

import java.util.*;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Kotlin style Map helper extensions.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
public class Maps {
    public static <K, V> @NonNull Map<K, V> filter(@NonNull Map<K, V> map, @NonNull BiFunction<K, V, Boolean> action) {
        return map.entrySet().stream().filter(entry -> action.apply(entry.getKey(), entry.getValue()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K extends Comparable<? super K>, V> @NonNull SortedMap<K, V> filter(@NonNull SortedMap<K, V> map,
            @NonNull BiFunction<K, V, Boolean> action) {
        return Collections.unmodifiableSortedMap(
                new TreeMap<>(map.entrySet().stream().filter(entry -> action.apply(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
    }

    public static <K, V, R> @NonNull Map<R, V> mapKeys(@NonNull Map<K, V> map,
            @NonNull Function<Map.Entry<K, V>, R> action) {
        return map.entrySet().stream().collect(Collectors.toUnmodifiableMap(action, Map.Entry::getValue));
    }

    public static <K, V> @NonNull Map<K, V> mapValues(@NonNull Map<K, V> map,
            @NonNull Function<Map.Entry<K, V>, V> action) {
        return map.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, action));
    }

    public static <K, V> @NonNull Map<K, V> plus(@NonNull Map<K, V> source, @NonNull Map<K, V> overrides) {
        var map = new HashMap<K, V>(source.size() + overrides.size());
        map.putAll(source);
        map.putAll(overrides);
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> @NonNull Map<K, V> combine(@NonNull Map<K, V> a, @NonNull Map<K, V> b,
            @NonNull BiFunction<V, V, V> action) {
        var map = new HashMap<K, V>(Math.max(a.size(), b.size()));
        map.putAll(a);
        b.forEach((key, bValue) -> {
            var aValue = map.get(key);
            map.put(key, aValue == null ? bValue : action.apply(aValue, bValue));
        });
        return Collections.unmodifiableMap(map);
    }
}
