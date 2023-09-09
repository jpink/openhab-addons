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
package org.openhab.binding.entsoe.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link EntsoeConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class EntsoeConfiguration {

    /**
     * A security token to access ENTSO-E Transparency Platform RESTful API.
     */
    public String securityToken = "";
    public String password = "";
    public int refreshInterval = 600;
}
