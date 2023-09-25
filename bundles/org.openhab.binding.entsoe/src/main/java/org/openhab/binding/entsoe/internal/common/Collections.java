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

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Collection extensions.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Collections {
    public static <E extends Comparable<? super E>> List<E> sort(List<E> list) {
        var sorted = new ArrayList<>(list);
        java.util.Collections.sort(sorted);
        return unmodifiableList(sorted);
    }
}
