package org.openhab.binding.entsoe.internal.monetary;

import org.junit.jupiter.api.Test;
import org.openhab.core.library.unit.Units;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.binding.entsoe.internal.monetary.CurrencyUnitTest.*;

class MoneyTest {
    private static final Money TAX = new Money(0.0279372, EURO);
    private static final Money TAX2 = new Money(0.0279372, EURO2);

    @Test
    public void equals_same_true() {
        assertEquals(TAX, TAX2);
    }

    @Test
    public void hashCode_same_true() {
        assertEquals(TAX.hashCode(), TAX2.hashCode());
    }

    @Test
    public void toString_tax_euros() {
        assertEquals("0.0279372 €", TAX.toString());
    }

    /*@Test
    public void toSystemUnit_tax_euros() {
        var euros = TAX.to(EUROS);
        assertNotNull(euros);
        assertEquals("0.0279372 €", euros.toString());
    }

    @Test
    public void toString_tax_cents() {
        assertEquals("2.79372 c", TAX.toString());
    }

    @Test
    public void test() {
        var foo = Quantities.getQuantity(5, Units.KILOWATT_HOUR);
        System.out.println(foo);
        System.out.println(foo.to(Units.MEGAWATT_HOUR));
    }*/

}