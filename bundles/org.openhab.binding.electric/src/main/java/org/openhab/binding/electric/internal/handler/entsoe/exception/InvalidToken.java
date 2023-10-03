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
package org.openhab.binding.electric.internal.handler.entsoe.exception;

import java.io.Serial;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Invalid token exception.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault

public class InvalidToken extends Exception {
    @Serial
    private static final long serialVersionUID = 3456308814200380427L;

    public InvalidToken(IllegalArgumentException cause) {
        super(cause);
    }
}
