package org.openhab.binding.entsoe.internal.monetary;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Currency;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openhab.core.library.unit.Units;

class CurrencyUnitTest {
    static CurrencyUnit euro() {
        return new CurrencyUnit(eur());
    }

    static CurrencyUnit euro(int fractionDigits) {
        return new CurrencyUnit(eur(), fractionDigits);
    }

    static Currency eur() {
        return Currency.getInstance("EUR");
    }

    @Test
    void getCurrency_euro_eur() {
        assertEquals(eur(), euro().getCurrency());
    }

    @Test
    void getFractionDigits_euro_2() {
        assertEquals(2, euro().getFractionDigits());
    }

    @Test
    void isCompatible_same_true() {
        assertTrue(euro().isCompatible(euro()));
    }

    @Test
    void isEquivalentTo_same_true() {
        assertTrue(euro().isEquivalentTo(euro()));
    }

    @Disabled
    @Test
    void divide_eurPerMwh_ok() {
        var quotient = euro().divide(Units.MEGAWATT_HOUR);

        var text = quotient.toString();
        assertEquals("€/MWh", text);
    }

    @Test
    void equals_same_true() {
        assertEquals(euro(), euro());
    }

    @Test
    void hashCode_same_true() {
        assertEquals(euro().hashCode(), euro().hashCode());
    }

    @Test
    void toString_euro_e() {
        assertEquals("€", euro().toString());
    }
}
