package org.openhab.binding.entsoe.internal.monetary;

//import static org.junit.jupiter.api.Assertions.*;

import static org.openhab.binding.entsoe.internal.monetary.CurrencyUnitTest.euro;

import org.junit.jupiter.api.Test;
import org.openhab.core.library.unit.Units;

class EnergyCurrencyTest {
    @Test
    void create_s() {
        EnergyCurrency.ofQuotient(euro(), Units.MEGAWATT_HOUR);
    }
}
