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

public class TooLong extends Exception {
    @Serial
    private static final long serialVersionUID = 7232286219011534478L;
    public final Duration max;
    public final Duration value;

    public TooLong(Duration value, Duration max) {
        super(value + " > " + max);
        this.max = max;
        this.value = value;
    }
}
