package org.openhab.binding.entsoe.internal.client;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.openhab.core.library.unit.Units;

import javax.measure.Unit;

/** Energy measure converter. */
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
