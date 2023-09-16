package org.openhab.binding.entsoe.internal.monetary;

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.binding.entsoe.internal.monetary.CurrencyUnitTest.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MoneyTest {
    static final double TRANSFER_MONTHLY_AMOUNT = 8.75;
    static final double TAX_AMOUNT = 0.0279372;
    static final double SELLER_MONTHLY_AMOUNT = 4.9;
    static final Money TAX = new Money(TAX_AMOUNT, euro());
    static final Money TAX2 = new Money(TAX_AMOUNT, euro());

    static Money euros(double amount) {
        return new Money(amount, euro());
    }

    static Money cents(double amount) {
        return new Money(amount, euroCent());
    }

    void toString_euros(String expected, double amount) {
        assertEquals(expected + " €", euros(amount).toString());
    }

    @Test
    void equals_same_true() {
        assertEquals(TAX, TAX2);
    }

    @Test
    void hashCode_same_true() {
        assertEquals(TAX.hashCode(), TAX2.hashCode());
    }

    @Test
    void toString_euros_transferMonthly() {
        toString_euros("8.75", TRANSFER_MONTHLY_AMOUNT);
    }

    @Test
    void toString_euros_sellerMonthly() {
        toString_euros("4.90", SELLER_MONTHLY_AMOUNT);
    }

    @Test
    void toString_tax_euros() {
        assertEquals("0.03 €", TAX.toString());
    }

    @ParameterizedTest
    @CsvSource({ "3.40 c,3.4", // transfer
            "7.60 c,7.5957", // spot
            "0.25 c,0.25" // marginal
    })
    void toString_cents(String expected, double amount) {
        assertEquals(expected, cents(amount).toString());
    }

    @Test
    void toString_twoDecimals() {
        assertEquals("0.03 €", TAX.toString());
    }

    /*
     * @Test
     * public void toSystemUnit_tax_euros() {
     * var euros = TAX.to(EUROS);
     * assertNotNull(euros);
     * assertEquals("0.0279372 €", euros.toString());
     * }
     *
     * @Test
     * public void toString_tax_cents() {
     * assertEquals("2.79372 c", TAX.toString());
     * }
     */
}
