/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional information.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.entsoe.internal.price;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link PriceConfig} class contains fields mapping thing configuration parameters.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class PriceConfig {

    /** A security token. */
    public String token = "";

    /** An area EIC code */
    public String area = "";

    public String unit = "c/kWh";

    public Float transfer = 0F;

    public Float tax = 0F;

    public Float margin = 0F;

}
