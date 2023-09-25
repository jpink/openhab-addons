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
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Invalid parameter exception.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class InvalidParameter extends Exception {
    @Serial
    private static final long serialVersionUID = -1662688835270360171L;

    private static @Nullable String getQuery(String url) {
        try {
            return new URL(url).getQuery();
        } catch (MalformedURLException e) {
            return e.getMessage();
        }
    }

    public InvalidParameter(String url) {
        super(getQuery(url));
    }
}
