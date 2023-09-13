package org.openhab.binding.entsoe.internal.monetary;

import org.junit.jupiter.api.Test;
import org.openhab.core.library.unit.Units;
import tech.units.indriya.format.SimpleQuantityFormat;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyUnitTest {
    public static final Currency EUR = Currency.getInstance("EUR");
    public static final CurrencyUnit EURO = new CurrencyUnit(EUR);
    public static final CurrencyUnit EURO2 = new CurrencyUnit(Currency.getInstance("EUR"));

    @Test
    public void currency_euro_eur() {
        assertEquals(EUR, EURO.currency);
    }

    @Test
    public void isCompatible_same_true() {
        assertTrue(EURO.isCompatible(EURO2));
    }

    @Test
    public void isEquivalentTo_same_true() {
        assertTrue(EURO.isEquivalentTo(EURO2));
    }

    @Test
    public void divide_eurPerMwh_ok() {
        var simple = SimpleQuantityFormat.getInstance();
        var quotient = EURO.divide(Units.MEGAWATT_HOUR);

        var text = quotient.toString();
        assertEquals("€/MWh", text);
    }

    @Test
    public void equals_same_true() {
        assertEquals(EURO, EURO2);
    }

    @Test
    public void hashCode_same_true() {
        assertEquals(EURO.hashCode(), EURO2.hashCode());
    }

    @Test
    public void toString_euro_e() {
        assertEquals("€", EURO.toString());
    }

}