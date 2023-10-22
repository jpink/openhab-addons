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

import java.util.function.Supplier;

/**
 * Late initialization variable.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Late<T> implements Supplier<T> {
    private @Nullable T instance;

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public T get() {
        var value = instance;
        if (value == null) {
            throw new NullPointerException("Late initialization variable not yet initialized!");
        }
        return value;
    }

    public void set(T instance) {
        this.instance = instance;
    }
}
