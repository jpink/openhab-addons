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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Publication market document data transfer object.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@XStreamAlias("Publication_MarketDocument")
public class Publication extends MarketDocument {
    @XStreamAlias("period.timeInterval")
    public TimeInterval timeInterval;

    @XStreamImplicit(itemFieldName = "TimeSeries")
    public List<TimeSeries> timeSeries;
}
