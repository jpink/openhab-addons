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

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Kotlin style Map helper extensions.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Maps {
    public static <K, V> Map<K, V> filter(Map<K, V> map, BiFunction<K, V, Boolean> action) {
        return map.entrySet().stream().filter(entry -> action.apply(entry.getKey(), entry.getValue()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <K extends Comparable<? super K>, V> SortedMap<K, V> filter(SortedMap<K, V> map,
            BiFunction<K, V, Boolean> action) {
        return Collections.unmodifiableSortedMap(
                new TreeMap<>(map.entrySet().stream().filter(entry -> action.apply(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
    }

    public static <K, V, R> Map<R, V> mapKeys(Map<K, V> map, Function<Map.Entry<K, V>, R> action) {
        return map.entrySet().stream().collect(Collectors.toUnmodifiableMap(action, Map.Entry::getValue));
    }

    public static <K, V> Map<K, V> mapValues(Map<K, V> map, Function<Map.Entry<K, V>, V> action) {
        return map.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, action));
    }

    public static <K, V> Map<K, V> of(Dictionary<K, V> dictionary) {
        return Collections.list(dictionary.keys()).stream()
                .collect(Collectors.toMap(Function.identity(), dictionary::get));
    }

    public static <K, V> Map<K, V> plus(Map<K, V> source, Map<K, V> overrides) {
        var map = new HashMap<K, V>(source.size() + overrides.size());
        map.putAll(source);
        map.putAll(overrides);
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> combine(Map<K, V> a, Map<K, V> b, BiFunction<V, V, V> action) {
        var map = new HashMap<K, V>(Math.max(a.size(), b.size()));
        map.putAll(a);
        b.forEach((key, bValue) -> {
            var aValue = map.get(key);
            map.put(key, aValue == null ? bValue : action.apply(aValue, bValue));
        });
        return Collections.unmodifiableMap(map);
    }
}
