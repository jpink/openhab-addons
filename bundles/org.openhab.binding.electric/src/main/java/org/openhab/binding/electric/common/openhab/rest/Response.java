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
package org.openhab.binding.electric.common.openhab.rest;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * REST API Response.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public record Response(int status, String content) {
    boolean success() {
        return 200 <= status && status < 300;
    }
    boolean clientError() {
        return 400 <= status && status < 500;
    }
    boolean serverError() {
        return 500 <= status && status < 600;
    }
}