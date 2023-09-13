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

import java.util.Locale;

import org.slf4j.LoggerFactory;

/**
 * Area
 *
 * @see <a href=
 *      "https://transparency.entsoe.eu/content/static_content/Static%20content/web%20api/Guide.html#_areas">Codes
 *      and their meaning</a>
 */
public enum Area {
    CZ("10YCZ-CEPS-----N", "SCA|CZ, MBA|CZ, Czech Republic (CZ), CTA|CZ, BZN|CZ, LFA|CZ, LFB|CZ"),
    FI("10YFI-1--------U", "MBA|FI, SCA|FI, CTA|FI, Finland (FI), BZN|FI, IPA|FI, IBA|FI");

    public static Area of(Locale locale) {
        var country = locale.getCountry();
        try {
            return Area.valueOf(country);
        } catch (IllegalArgumentException ex) {
            LoggerFactory.getLogger(Area.class)
                    .warn("No single area defined for country code {}! Configure area manually!", country);
            return null;
        }
    }

    /** EIC code */
    public final String code;

    public final String meaning;

    private Area(String code, String meaning) {
        this.code = code;
        this.meaning = meaning;
    }

    @Override
    public String toString() {
        return code;
    }
}
