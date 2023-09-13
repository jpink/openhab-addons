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

public class UnknownResponse extends Exception {

    @Serial
    private static final long serialVersionUID = -3279306525482845066L;
    public final String url;
    public final int status;

    public UnknownResponse(String url, int status, String content, Exception cause) {
        super(content, cause);
        this.status = status;
        this.url = url;
    }
}
