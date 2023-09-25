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
package org.openhab.binding.entsoe.internal.client.dto;

import java.time.ZonedDateTime;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Abstract market document data transfer object.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
public abstract class MarketDocument {
    @XStreamAlias("createdDateTime")
    public ZonedDateTime created;
}
