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

import java.time.Duration;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Period data transfer object.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
public class Period {
    public TimeInterval timeInterval;
    public Duration resolution;

    @XStreamImplicit(itemFieldName = "Point")
    public List<Point> points;
}
