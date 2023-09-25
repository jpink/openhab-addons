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
package org.openhab.binding.entsoe.internal.price.service;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Generic holder to edit record values after creation.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class Holder<T> {
    public T value;

    public Holder(T value) {
        this.value = value;
    }
}
