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

/** Unexpected exception which means bug in price service. */
public class Bug extends Exception {
    @Serial
    private static final long serialVersionUID = 6476362399567760465L;

    public Bug(Throwable cause) {
        super(cause);
    }
}
