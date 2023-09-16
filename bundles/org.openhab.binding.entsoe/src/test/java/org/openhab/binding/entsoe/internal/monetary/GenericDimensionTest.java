package org.openhab.binding.entsoe.internal.monetary;

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.binding.entsoe.internal.Collections.*;
import static org.openhab.binding.entsoe.internal.monetary.GenericDimension.*;
import static org.openhab.core.library.unit.Units.*;
import static tech.units.indriya.unit.Units.KILOGRAM;
import static tech.units.indriya.unit.Units.METRE;

import java.util.stream.Stream;

import javax.measure.Dimension;
import javax.measure.Unit;
import javax.measure.quantity.Energy;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@NonNullByDefault
class GenericDimensionTest {
    static final String MONETARY_ENERGY_SYMBOLS = "[T]²·[¤]/[L]²·[M]";

    @Test
    void toString_sorted() {
        var list = listOf("[T]²", "[¤]");
        assertEquals(list, sort(list));
    }

    @Test
    void toString_unsorted() {
        var list = listOf("[¤]", "[T]²");
        assertNotEquals(list, sort(list));
    }

    static Stream<Arguments> toString_dimension() {
        return Stream.of(Arguments.of("[¤]", MONETARY), Arguments.of("[L]²·[M]/[T]²", ENERGY),
                Arguments.of(MONETARY_ENERGY_SYMBOLS, MONETARY_ENERGY));
    }

    @ParameterizedTest
    @MethodSource
    void toString_dimension(String expected, Dimension dimension) {
        assertEquals(expected, dimension.toString());
    }

    static Stream<Arguments> toString_unit() {
        return Stream.of(Arguments.of("[M]", KILOGRAM), Arguments.of("[L]", METRE), Arguments.of("[T]", SECOND),
                Arguments.of("[L]²·[M]/[T]³", WATT));
    }

    @ParameterizedTest
    @MethodSource
    void toString_unit(String expected, Unit<?> unit) {
        toString_dimension(expected, unit.getDimension());
    }

    static Stream<Unit<Energy>> divide_monetaryPerEnergy_monetaryEnergy() {
        return Stream.of(JOULE, KILOWATT_HOUR, MEGAWATT_HOUR);
    }

    @ParameterizedTest
    @MethodSource
    void divide_monetaryPerEnergy_monetaryEnergy(Unit<Energy> unit) {
        toString_dimension(MONETARY_ENERGY_SYMBOLS, MONETARY.divide(unit.getDimension()));
    }
}
