package org.openhab.binding.entsoe.internal.price.service.measure;

import org.junit.jupiter.api.Test;
import org.openhab.core.library.unit.Units;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Unit;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class MonetaryTest {
    private static final String EUR = "EUR";
    private static final Unit<Money> EUROS = Monetary.getBaseUnit(EUR);
    private static final Unit<Money> CENTS = Monetary.getCentUnit(EUR);
    private static final Money TAX = Monetary.getCents(2.79372, EUR);

    @Test
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
    }

}