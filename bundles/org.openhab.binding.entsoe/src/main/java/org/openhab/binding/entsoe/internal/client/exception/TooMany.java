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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.entsoe.internal.client.dto.MarketDocument;

/**
 * Too many requests exception.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault

public class TooMany extends ErrorResponse {

    @Serial
    private static final long serialVersionUID = -995333382226355244L;

    public TooMany(MarketDocument document) {
        super(document);
    }
}
