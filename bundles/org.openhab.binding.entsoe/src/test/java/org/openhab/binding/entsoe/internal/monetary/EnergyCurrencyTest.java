package org.openhab.binding.entsoe.internal.monetary;

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.binding.entsoe.internal.monetary.CurrencyUnitTest.euro;
import static org.openhab.binding.entsoe.internal.monetary.CurrencyUnitTest.euroCent;
import static org.openhab.core.library.unit.Units.*;

import java.io.Serial;
import java.util.HashMap;
import java.util.stream.Stream;

import javax.measure.Unit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@NonNullByDefault
class EnergyCurrencyTest {
    static Unit<?> eMWh() {
        return euro().divide(MEGAWATT_HOUR);
    }

    static Unit<?> ckWh() {
        return euroCent().divide(KILOWATT_HOUR);
    }

    static Stream<Unit<?>> getSymbol_null() {
        return Stream.of(KILOWATT_HOUR, MEGAWATT_HOUR, ckWh(), eMWh());
    }

    @ParameterizedTest
    @MethodSource
    void getSymbol_null(Unit<?> unit) {
        assertNull(unit.getSymbol());
    }

    @Test
    void getSystemUnit_MWh_Ws() {
        assertEquals("Ws", MEGAWATT_HOUR.getSystemUnit().toString());
    }

    @Test
    void getSystemUnit_eMWh_eWs() {
        assertEquals("€/Ws", eMWh().getSystemUnit().toString());
    }

    @Test
    void getBaseUnits_MWh() {
        assertEquals(new HashMap<>() {
            @Serial
            private static final long serialVersionUID = -446125325966573532L;

            {
                put(WATT, 1);
                put(HOUR, 1);
            }
        }, MEGAWATT_HOUR.getBaseUnits());
    }

    @Test
    void getBaseUnits_eMWh() {
        assertEquals(new HashMap<>() {

            @Serial
            private static final long serialVersionUID = -8871095652320576989L;

            {
                put(euro(), 1);
                put(WATT, -1);
                put(HOUR, -1);
            }
        }, eMWh().getBaseUnits());
    }

    @Test
    void divide_euroPerMWh_eMWh() {
        assertEquals("€/MWh", eMWh().toString());
    }

    @Test
    void divide_euroCentPerkWh_ckWh() {
        assertEquals("c/kWh", ckWh().toString());
    }
}
