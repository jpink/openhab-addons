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
import org.eclipse.jdt.annotation.Nullable;

/**
 * Lazy initialization pattern implementation.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Lazy<T> implements Supplier<T> {
    private final Supplier<T> factory;
    private @Nullable T instance;

    public Lazy(Supplier<T> factory) {
        this.factory = factory;
    }

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public synchronized T get() {
        var value = instance;
        if (value == null) {
            value = factory.get();
            instance = value;
        }
        return value;
    }

    public boolean initialized() {
        return instance != null;
    }
}
