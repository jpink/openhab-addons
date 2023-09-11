package org.openhab.binding.entsoe.internal.price;

import org.junit.jupiter.api.Test;
import org.openhab.binding.entsoe.internal.price.service.VatRate;

import static org.junit.jupiter.api.Assertions.*;

class VatRateTest {
    private static final double PRICE = 100;
    private static final VatRate GENERAL = new VatRate(24);
    private static final double VAT = 24;
    private static final double TOTAL = 124;

    @Test
    public void create_minus0point1_illegal() {
        assertThrows(IllegalArgumentException.class, () -> new VatRate(-0.1));
    }

    @Test
    public void create_1point1_illegal() {
        assertThrows(IllegalArgumentException.class, () -> new VatRate(1.1));
    }

    @Test
    public void create_negative1_illegal() {
        assertThrows(IllegalArgumentException.class, () -> new VatRate(-1));
    }

    @Test
    public void create_101_illegal() {
        assertThrows(IllegalArgumentException.class, () -> new VatRate(101));
    }

    @Test
    public void create_0_ok() {
        new VatRate(0);
    }

    @Test
    public void price_124_100() {
        assertEquals(PRICE, GENERAL.price(TOTAL));
    }

    @Test
    public void vatFromTotal_124_24() {
        assertEquals(VAT, GENERAL.vatFromTotal(TOTAL));
    }

    @Test
    public void toString_24() {
        assertEquals("VAT 24%", GENERAL.toString());
    }

}