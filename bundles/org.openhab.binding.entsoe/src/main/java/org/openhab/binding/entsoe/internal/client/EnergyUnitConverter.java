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
package org.openhab.binding.entsoe.internal.client;

import javax.measure.Unit;

import org.openhab.core.library.unit.Units;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Energy measure converter for XStream.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
public class EnergyUnitConverter implements SingleValueConverter {
    /**
     * Marshals an Object into a single value representation.
     *
     * @param obj the Object to be converted
     * @return a String with the single value of the Object or <code>null</code>
     */
    @Override
    public String toString(Object obj) {
        return obj.toString();
    }

    /**
     * Unmarshals an Object from its single value representation.
     *
     * @param str the String with the single value of the Object
     * @return the Object
     */
    @Override
    public Object fromString(String str) {
        return switch (str) {
            case "MWH", "MWh" -> Units.MEGAWATT_HOUR;
            case "KWH", "kWh" -> Units.KILOWATT_HOUR;
            default -> throw new UnsupportedOperationException("Unable parse measure '" + str + "'!");
        };
    }

    /**
     * Determines whether the converter can marshall a particular type.
     *
     * @param type the Class representing the object type to be converted
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class type) {
        return Unit.class.isAssignableFrom(type);
    }
}
