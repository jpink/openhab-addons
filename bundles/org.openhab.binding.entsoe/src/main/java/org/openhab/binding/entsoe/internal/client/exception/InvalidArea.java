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

/**
 * Invalid area exception.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
public class InvalidArea extends Exception {
    @Serial
    private static final long serialVersionUID = -745040893268448199L;

    public InvalidArea(String area) {
        super(area);
    }
}
