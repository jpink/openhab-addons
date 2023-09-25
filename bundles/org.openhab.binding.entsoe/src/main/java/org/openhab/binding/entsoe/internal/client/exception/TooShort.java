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
package org.openhab.binding.entsoe.internal.client.exception;

import java.io.Serial;
import java.time.Duration;

/**
 * Too short duration exception.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
public class TooShort extends Exception {
    @Serial
    private static final long serialVersionUID = 4818451922083980812L;
    public final Duration min;
    public final Duration value;

    public TooShort(Duration value, Duration min) {
        super(value + " < " + min);
        this.min = min;
        this.value = value;
    }
}
