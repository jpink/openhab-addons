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

import java.io.Serial;
import java.util.Currency;

/**
 * Currencies doesn't match exception.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
public class CurrencyMismatch extends Exception {
    @Serial
    private static final long serialVersionUID = -1424230311124706505L;
    public final Currency expected;
    public final Currency actual;

    public CurrencyMismatch(Currency expected, Currency actual) {
        super(expected + " != " + actual);
        this.expected = expected;
        this.actual = actual;
    }
}
