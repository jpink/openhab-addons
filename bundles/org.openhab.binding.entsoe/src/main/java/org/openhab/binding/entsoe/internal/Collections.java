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
package org.openhab.binding.entsoe.internal;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

public class Collections {
    @SafeVarargs
    public static <E> @NonNull List<E> listOf(E... elements) {
        return unmodifiableList(Arrays.asList(elements));
    }

    public static <E extends Comparable<? super E>> @NonNull List<E> sort(@NonNull List<E> list) {
        var sorted = new ArrayList<>(list);
        java.util.Collections.sort(sorted);
        return unmodifiableList(sorted);
    }
}
